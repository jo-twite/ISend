package org.cleviox.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cleviox.backend.exception.BalanceRetrievalException;
import org.cleviox.backend.exception.InsufficientFundsException;
import org.cleviox.backend.model.Balance;
import org.cleviox.backend.model.TransactionHash;
import org.cleviox.backend.model.TransactionRequest;
import org.cleviox.backend.service.EthereumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EthereumController.class)
@ExtendWith(MockitoExtension.class)
class EthereumControllerTest {

    private final String apiUrl = "http://localhost:8080/api";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EthereumService ethereumService;

    BigDecimal bigDecimal = new BigDecimal("1");
    private final String validEthAddress = "^0x[a-fA-F0-9]{40}$";
    private final String invalidEthAddress = "invalidEthAddress";

    private final String validEthPrivateKey = "^[0-9a-fA-F]{64}$";
    private final String invalidEthPrivateKey = "invalidEthPrivateKey";

    private final TransactionRequest validRequest = new TransactionRequest(validEthPrivateKey, bigDecimal, validEthAddress);
    private final TransactionRequest invalidRequest = new TransactionRequest(invalidEthPrivateKey, bigDecimal, invalidEthAddress);


    BigDecimal validEthAmount = BigDecimal.valueOf(0.03125);
    //BigDecimal invalidEthAmount = BigDecimal.valueOf(-1);

    Balance balance = new Balance(validEthAmount);

    @Autowired
    private EthereumController ethereumController;

    @Test
    void getBalance_ValidAddress_ShouldReturnBalance() throws Exception {
        when(ethereumService.getBalance(validEthAddress)).thenReturn(balance);

        mockMvc.perform(get(apiUrl + "/eth_getBalance")
                .param("ethAddress", validEthAddress))
                .andExpect(status().isOk());
    }

    @Test
    void getBalance_InvalidAddress_ShouldReturnBadRequest() throws Exception {
        when(ethereumService.getBalance(invalidEthAddress)).thenThrow(new IllegalArgumentException("Invalid Ethereum address"));

        mockMvc.perform(get( apiUrl+ "/eth_getBalance")
                        .param("ethAddress", invalidEthAddress))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBalance_BalanceRetrievialException_InternalServerError() throws Exception {
        when(ethereumService.getBalance(validEthAddress)).thenThrow(BalanceRetrievalException.class);

        mockMvc.perform(get( apiUrl+ "/eth_getBalance")
                        .param("ethAddress", validEthAddress))
                .andExpect(status().isInternalServerError());
    }


    // SEND TRANSACTION


    @Test
    void sendTransactionValidRequestShouldReturnHash() throws Exception {
        given(ethereumService.sendTransaction(validRequest)).willReturn(new TransactionHash("Success"));

        ResultActions response = mockMvc.perform(post(apiUrl + "/eth_sendRawTransaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(validRequest)));

        response.andExpect(status().isOk());
    }


    @Test
    void sendTransaction_InvalidRequest_ThrowsException() throws Exception {
        given(ethereumService.sendTransaction(invalidRequest)).willThrow(IllegalArgumentException.class);

        ObjectMapper objectMapper = new ObjectMapper();
        ResultActions response = mockMvc.perform(post(apiUrl + "/eth_sendRawTransaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    void sendTransaction_InsufficientFunds_ThrowsException() throws Exception {
        given(ethereumService.sendTransaction(validRequest)).willThrow(InsufficientFundsException.class);

        ObjectMapper objectMapper = new ObjectMapper();
        ResultActions response = mockMvc.perform(post(apiUrl + "/eth_sendRawTransaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)));

        response.andExpect(status().isInternalServerError());
    }

}