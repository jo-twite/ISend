package org.cleviox.backend.service;

import org.cleviox.backend.exception.BalanceRetrievalException;
import org.cleviox.backend.exception.InsufficientFundsException;
import org.cleviox.backend.exception.TransactionFailedException;
import org.cleviox.backend.model.Balance;
import org.cleviox.backend.model.TransactionHash;
import org.cleviox.backend.model.TransactionRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;


@Service
public class EthereumServiceImpl implements EthereumService {

    private static final Logger log = LoggerFactory.getLogger(EthereumServiceImpl.class);
    private final Web3j web3j;

    public EthereumServiceImpl(Web3j web3j) {
        this.web3j = web3j;
    }

    @Override
    public Balance getBalance(String ethereumAddress) {
        if (addressNotValid(ethereumAddress)) {
            throw new IllegalArgumentException("Invalid Ethereum address.");
        }

        try {
            BigInteger balance = web3j.ethGetBalance(
                    ethereumAddress, org.web3j.protocol.core.DefaultBlockParameterName.LATEST)
                    .send()
                    .getBalance();

            return new Balance(Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER));
        } catch (IOException e) {
            log.error("Error while retrieving balance for address {}: {}", ethereumAddress, e.getMessage());
            throw new BalanceRetrievalException("Failed to retrieve balance due to an unexpected error.", e); // Custom exception
        }
    }

    @Override
    public TransactionHash sendTransaction(TransactionRequest transactionRequest) {
        log.info("Sending transaction request:\n{}", transactionRequest);

        validateTransactionRequest(transactionRequest);

        try {
            Credentials credentials = Credentials.create(transactionRequest.ethPrivateKey());

            // Execute the transaction and get the receipt
            TransactionReceipt transactionReceipt = Transfer.sendFunds(
                    web3j, credentials, transactionRequest.recipientAddress(),
                    transactionRequest.ethAmount(), Convert.Unit.ETHER)
                    .send();

            log.info("Transaction successful: {}", transactionReceipt.getTransactionHash());
            return new TransactionHash(transactionReceipt.getTransactionHash());

        } catch (Exception e) {
            if (e.getMessage().contains("insufficient funds")) {
                log.error("Transaction failed: insufficient funds.", e);
                throw new InsufficientFundsException("Transaction failed due to insufficient funds.");
            } else {
                log.error("Unexpected runtime error during transaction", e);
                throw new TransactionFailedException("Transaction failed due to an unexpected error.", e);
            }
        }
    }


    // Validation

    private void validateTransactionRequest(TransactionRequest transactionRequest) {
        if (privateKeyNotValid(transactionRequest.ethPrivateKey())) {
            throw new IllegalArgumentException("Invalid or empty Ethereum private key.");
        }
        if (transactionRequest.ethAmount() == null || transactionRequest.ethAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Ethereum amount must be a positive value.");
        }
        if (addressNotValid(transactionRequest.recipientAddress())) {
            throw new IllegalArgumentException("Invalid recipient Ethereum address.");
        }
    }

    // Ethereum pattern matching

    private boolean privateKeyNotValid(String ethPrivateKey) {
        return ethPrivateKey == null || !ethPrivateKey.matches("^[0-9a-fA-F]{64}$");
    }

    private boolean addressNotValid(String recipientAddress) {
        return recipientAddress == null || !recipientAddress.matches("^0x[a-fA-F0-9]{40}$");
    }

}
