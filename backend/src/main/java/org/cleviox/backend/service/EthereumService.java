package org.cleviox.backend.service;


import org.cleviox.backend.model.Balance;
import org.cleviox.backend.model.TransactionHash;
import org.cleviox.backend.model.TransactionRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public interface EthereumService {

    Balance getBalance(String address) throws ExecutionException, InterruptedException, TimeoutException;

    TransactionHash sendTransaction(TransactionRequest transactionRequest) throws Exception;
}
