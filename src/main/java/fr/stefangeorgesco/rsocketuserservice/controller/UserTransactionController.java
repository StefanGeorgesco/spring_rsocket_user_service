package fr.stefangeorgesco.rsocketuserservice.controller;

import fr.stefangeorgesco.rsocketuserservice.dto.TransactionRequest;
import fr.stefangeorgesco.rsocketuserservice.dto.TransactionResponse;
import fr.stefangeorgesco.rsocketuserservice.service.UserTransactionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@MessageMapping("user")
public class UserTransactionController {

    private final UserTransactionService userTransactionService;

    public UserTransactionController(UserTransactionService userTransactionService) {
        this.userTransactionService = userTransactionService;
    }

    @MessageMapping("transaction")
    public Mono<TransactionResponse> doTransaction(Mono<TransactionRequest> transactionRequestMono) {
        return transactionRequestMono.flatMap(userTransactionService::doTransaction);
    }
}
