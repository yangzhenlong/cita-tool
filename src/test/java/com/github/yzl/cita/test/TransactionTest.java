package com.github.yzl.cita.test;

import com.citahub.cita.protocol.core.methods.request.Transaction;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yzl.cita.blockchain.Blockchain;
import com.github.yzl.cita.key.Key;
import com.github.yzl.cita.key.Keys;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TransactionTest {

    @Test
    public void testTransaction() throws InterruptedException, JsonProcessingException {
        // generate key
        Key key = Keys.create(Transaction.CryptoTx.SM2);

        // build blockchain object
        Blockchain blockchain = Blockchain.builder().rpcAddr("https://testnet-sm2.rivus.rivtower.com").cryptoType(Transaction.CryptoTx.SM2).build();

        // sendRawTransaction
        String txHash = blockchain.sendRawTransaction("abc", key.getPrivateKey());
        System.out.println("txHash: " + txHash);

        // getTransactionReceipt
        TimeUnit.SECONDS.sleep(6);
        TransactionReceipt receipt = blockchain.getTransactionReceiptByTxHash(txHash);
        System.out.println(new ObjectMapper().writeValueAsString(receipt));
    }
}

