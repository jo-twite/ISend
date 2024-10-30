package org.cleviox.backend.model;

import java.math.BigDecimal;

public record TransactionRequest(String ethPrivateKey, BigDecimal ethAmount, String recipientAddress) {


    @Override
    public String toString() {
        return """
            {
                "ethPrivateKey": "%s",
                "ethAmount": "%s",
                "recipientAddress": "%s"
            }
            """.formatted(encryptedPrivateKey(), ethAmount, recipientAddress);
    }

    private String encryptedPrivateKey() {
        return ethPrivateKey.substring(0, 4) + "****...";
    }
}
