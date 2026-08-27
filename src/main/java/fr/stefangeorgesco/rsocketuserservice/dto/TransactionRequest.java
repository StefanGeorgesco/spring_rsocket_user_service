package fr.stefangeorgesco.rsocketuserservice.dto;

import fr.stefangeorgesco.rsocketuserservice.domain.TransactionType;

public record TransactionRequest(String userId,
                                 TransactionType type,
                                 int amount) {
}
