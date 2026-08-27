package fr.stefangeorgesco.rsocketuserservice.service;

import fr.stefangeorgesco.rsocketuserservice.dto.TransactionRequest;
import fr.stefangeorgesco.rsocketuserservice.dto.TransactionResponse;
import fr.stefangeorgesco.rsocketuserservice.entity.User;
import fr.stefangeorgesco.rsocketuserservice.repository.UserRepository;
import fr.stefangeorgesco.rsocketuserservice.util.EntityDtoUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

import static fr.stefangeorgesco.rsocketuserservice.domain.TransactionType.CREDIT;
import static fr.stefangeorgesco.rsocketuserservice.domain.TransactionStatus.*;

@Service
public class UserTransactionService {

    private final UserRepository userRepository;

    public UserTransactionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<TransactionResponse> doTransaction(TransactionRequest request) {
        UnaryOperator<Mono<User>> operation = CREDIT.equals(request.type()) ?
                credit(request) :
                debit(request);

        return userRepository.findById(request.userId())
                .transform(operation)
                .flatMap(userRepository::save)
                .map(user -> EntityDtoUtil.toResponse(request, COMPLETED))
                .switchIfEmpty(Mono.just(EntityDtoUtil.toResponse(request, FAILED)));
    }

    private UnaryOperator<Mono<User>> credit(TransactionRequest request) {
        return userMono -> userMono
                .doOnNext(user -> user.setBalance(user.getBalance() + request.amount()));
    }

    private UnaryOperator<Mono<User>> debit(TransactionRequest request) {
        return userMono -> userMono
                .filter(user -> user.getBalance() >= request.amount())
                .doOnNext(user -> user.setBalance(user.getBalance() - request.amount()));
    }
}
