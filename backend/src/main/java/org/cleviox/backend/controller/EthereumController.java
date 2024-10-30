package org.cleviox.backend.controller;

import org.cleviox.backend.exception.BalanceRetrievalException;
import org.cleviox.backend.model.Balance;
import org.cleviox.backend.model.TransactionHash;
import org.cleviox.backend.model.TransactionRequest;
import org.cleviox.backend.service.EthereumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class EthereumController {

    private final EthereumService ethereumService;

    public EthereumController(EthereumService ethereumService) {
        this.ethereumService = ethereumService;
    }


    @GetMapping("/eth_getBalance")
    public ResponseEntity<?> getBalance(@RequestParam String ethAddress) {
        try {
            Balance balance = ethereumService.getBalance(ethAddress);
            return ResponseEntity.ok(balance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (BalanceRetrievalException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
        }
    }


    // Endpoint 2: Send ETH
    @PostMapping("/eth_sendRawTransaction")
    public ResponseEntity<?> sendTransaction(@RequestBody TransactionRequest transactionRequest) {
        try {
            TransactionHash transactionHash = ethereumService.sendTransaction(transactionRequest);
            return ResponseEntity.ok(transactionHash);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
        }
    }
}