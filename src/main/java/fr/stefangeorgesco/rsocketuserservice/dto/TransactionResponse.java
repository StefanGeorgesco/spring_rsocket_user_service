package fr.stefangeorgesco.rsocketuserservice.dto;

import fr.stefangeorgesco.rsocketuserservice.domain.TransactionStatus;
import fr.stefangeorgesco.rsocketuserservice.domain.TransactionType;

public record TransactionResponse(String userId,
                                  TransactionType type,
                                  int amount,
                                  TransactionStatus status) {
}
