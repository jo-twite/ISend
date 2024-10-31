# ISend 

## A simple API for interacting with the Ethereum blockchain

It uses Spring Boot, Web3j, and integrates with the Ethereum blockchain using a remote node.

## Features
- Retrieve Ethereum Balance: Get the current balance of any Ethereum address.
- Send Ethereum Transactions: Transfer ETH from one address to another.

## Stack

- Java 21 
- Spring boot
- Web3j
- Maven: To build and run the project. 
- Infura Account: Set up an Infura (or any other) project to connect to the Ethereum blockchain.

## Endpoints

1. Get Balance
   Retrieve the balance of an Ethereum address.

Endpoint: 

```GET /api/eth_getBalance```
#### Parameters:
- ethAddress: The Ethereum address to check (e.g., 0x...).
#### Response:
- 200 OK: Returns the balance.
- 400 Bad Request: Invalid Ethereum address.
- 500 Internal Server Error: If balance retrieval fails.

#### Request example:

```
curl -X GET "http://localhost:8080/api/eth_getBalance?ethAddress=YourEthereumAddress
```

#### Response example:
```json
{
  "balance": "0.01234"
}
```

2. Send Transaction
Send ETH from one address to another.

Endpoint: POST /api/eth_sendRawTransaction

#### Body:
```
ethPrivateKey: Ethereum private key (64-character hex string).
ethAmount: Amount of ETH to send.
recipientAddress: Ethereum address to receive the ETH.
Response:
- 200 OK: Returns transaction hash on success.
- 400 Bad Request: Invalid request data.
- 500 Internal Server Error: Insufficient funds or other errors.
```
#### Request example

```
curl -X POST "http://localhost:8080/api/eth_sendRawTransaction" -H "Content-Type: application/json" -d '{
"ethPrivateKey": "yourPrivateKey",
"ethAmount": 0.01,
"recipientAddress": "0xRecipientAddress"
}'
```
Exception Handling

The API returns appropriate HTTP status codes for error cases:

- 400 Bad Request: Invalid input (e.g., invalid Ethereum address or private key).
- 500 Internal Server Error: Insufficient funds or unexpected errors.


## Notes

- In `src/main/resources/application.properties` you can set up the link to your remote project. 