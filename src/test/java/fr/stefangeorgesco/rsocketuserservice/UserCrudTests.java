package fr.stefangeorgesco.rsocketuserservice;

import fr.stefangeorgesco.rsocketuserservice.dto.UserDto;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "spring.rsocket.server.port=0"
})
@Import(TestcontainersConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings("JavaPrintToLogpoint")
class UserCrudTests {

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

    @Test
    @Order(1)
    void getAllUsers() {
        Flux<UserDto> flux = requester
                .route("user.get.all")
                .retrieveFlux(UserDto.class)
                .doOnNext(System.out::println);

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void getUserById() {
        UserDto randomUserDto = getRandomUserDto();

        Mono<UserDto> mono = requester
                .route("user.get.{id}", randomUserDto.id())
                .retrieveMono(UserDto.class)
                .doOnNext(System.out::println);

        StepVerifier.create(mono)
                .expectNextMatches(userDto -> userDto.id().equals(randomUserDto.id()))
                .verifyComplete();
    }

    @Test
    void createUser() throws InterruptedException {
        Long countBefore = getUserCount();
        System.out.println("Count before: " + countBefore);

        UserDto newUserDto = new UserDto(null, "New User", 2000);

        Mono<UserDto> mono = requester
                .route("user.create")
                .data(newUserDto)
                .retrieveMono(UserDto.class)
                .doOnNext(System.out::println);

        StepVerifier.create(mono)
                .consumeNextWith(userDto -> {
                            assertNotNull(userDto.id());
                            assertEquals(newUserDto.name(), userDto.name());
                            assertEquals(newUserDto.balance(), userDto.balance());
                        }
                )
                .verifyComplete();

        Thread.sleep(500);

        Long countAfter = getUserCount();
        System.out.println("Count after: " + countAfter);

        assertEquals(countBefore + 1, countAfter);
    }

    @Test
    void updateUser() {
        UserDto randomUserDto = getRandomUserDto();
        System.out.println("Random user: " + randomUserDto);

        UserDto updatedUserDto = new UserDto(null, "Updated Name", randomUserDto.balance() + 3000);

        Mono<UserDto> mono = requester
                .route("user.update.{id}", randomUserDto.id())
                .data(updatedUserDto)
                .retrieveMono(UserDto.class)
                .doOnNext(System.out::println);

        StepVerifier.create(mono)
                .consumeNextWith(userDto -> {
                            assertEquals(randomUserDto.id(), userDto.id());
                            assertEquals(updatedUserDto.name(), userDto.name());
                            assertEquals(updatedUserDto.balance(), userDto.balance());
                        }
                )
                .verifyComplete();
    }

    @Test
    void deleteUser() throws InterruptedException {
        UserDto randomUserDto = getRandomUserDto();
        System.out.println("Random user: " + randomUserDto);

        Long countBefore = getUserCount();
        System.out.println("Count before: " + countBefore);

        Mono<Void> mono = requester
                .route("user.delete.{id}", randomUserDto.id())
                .send()
                .doOnSuccess(v -> System.out.println("Deleted user with id: " + randomUserDto.id()));

        StepVerifier.create(mono)
                .verifyComplete();

        Thread.sleep(500);

        Long countAfter = getUserCount();
        System.out.println("Count after: " + countAfter);

        assertEquals(countBefore - 1, countAfter);
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

    private Long getUserCount() {
        return requester
                .route("user.get.all")
                .retrieveFlux(UserDto.class)
                .count()
                .block();
    }
}
