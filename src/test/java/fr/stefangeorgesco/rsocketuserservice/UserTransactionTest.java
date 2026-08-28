package fr.stefangeorgesco.rsocketuserservice;

import fr.stefangeorgesco.rsocketuserservice.domain.TransactionStatus;
import fr.stefangeorgesco.rsocketuserservice.domain.TransactionType;
import fr.stefangeorgesco.rsocketuserservice.dto.TransactionRequest;
import fr.stefangeorgesco.rsocketuserservice.dto.TransactionResponse;
import fr.stefangeorgesco.rsocketuserservice.dto.UserDto;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static fr.stefangeorgesco.rsocketuserservice.domain.TransactionStatus.COMPLETED;
import static fr.stefangeorgesco.rsocketuserservice.domain.TransactionStatus.FAILED;
import static fr.stefangeorgesco.rsocketuserservice.domain.TransactionType.CREDIT;
import static fr.stefangeorgesco.rsocketuserservice.domain.TransactionType.DEBIT;
import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.rsocket.server.port=0"
})
@Import(TestcontainersConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("JavaPrintToLogpoint")
class UserTransactionTest {

    @Value("${local.rsocket.server.port}")
    private int rsocketPort;

    @Autowired
    private RSocketRequester.Builder builder;

    private RSocketRequester requester;

    @BeforeAll
    void setUp() {
        requester = this.builder
                .transport(TcpClientTransport.create("localhost", rsocketPort));
    }

    @ParameterizedTest
    @MethodSource("testData")
    void doTransactionTest(String testName,
                           TransactionType transactionType,
                           UnaryOperator<Integer> amountOperator,
                           TransactionStatus expectedStatus) throws InterruptedException {
        System.out.println("Running test: " + testName);

        UserDto randomUserDto = getRandomUserDto();
        int initialBalance = randomUserDto.balance();
        int amount = amountOperator.apply(initialBalance);

        TransactionRequest request = new TransactionRequest(randomUserDto.id(), transactionType, amount);

        Mono<TransactionResponse> responseMono = requester
                .route("user.transaction")
                .data(request)
                .retrieveMono(TransactionResponse.class)
                .doOnNext(System.out::println);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.status().equals(expectedStatus))
                .verifyComplete();

        Thread.sleep(500);

        UserDto updatedUserDto = getUserById(randomUserDto.id());
        System.out.println("Updated user: " + updatedUserDto);
        assertNotNull(updatedUserDto);

        int expectedBalance;
        if (expectedStatus == COMPLETED) {
            expectedBalance = (transactionType == CREDIT) ? initialBalance + amount : initialBalance - amount;
        } else {
            expectedBalance = initialBalance;
        }
        assertEquals(expectedBalance, updatedUserDto.balance());
    }

    /*
        Test data source
    */

    private Stream<Arguments> testData() {
        UnaryOperator<Integer> fiftyOperator = integer -> 50;
        UnaryOperator<Integer> oneTenthOperator = integer -> integer / 10;
        UnaryOperator<Integer> plusOneOperator = integer -> integer + 1;
        return Stream.of(
                Arguments.of(
                        "Credit Test: 50",
                        CREDIT,
                        fiftyOperator,
                        COMPLETED
                ),
                Arguments.of(
                        "Debit Test Success: one tenth of balance",
                        DEBIT,
                        oneTenthOperator,
                        COMPLETED
                ),
                Arguments.of(
                        "Debit Test Failure: balance + 1",
                        DEBIT,
                        plusOneOperator,
                        FAILED
                )
        );
    }

    /*
        Méthodes utilitaires
     */

    private UserDto getRandomUserDto() {
        return requester
                .route("user.get.all")
                .retrieveFlux(UserDto.class)
                .blockFirst();
    }

    private UserDto getUserById(String userId) {
        return requester
                .route("user.get.{id}", userId)
                .retrieveMono(UserDto.class)
                .block();
    }
}
