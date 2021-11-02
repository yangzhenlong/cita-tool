package com.github.yzl.cita.blockchain;


import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.methods.request.Transaction;
import com.citahub.cita.protocol.core.methods.response.AppGetTransactionReceipt;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.protocol.http.HttpService;
import com.citahub.cita.utils.HexUtil;
import com.github.yzl.cita.utils.BlockchainUtil;
import com.github.yzl.cita.utils.StringUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * 区块链信息
 */
public class Blockchain {

    private Blockchain() {}

    private static final String EVIDENCE_ADDRESS = "0xffffffffffffffffffffffffffffffffff010000";
    private static final String DEFAULT_TX_VALUE = "0";

    private String rpcAddr;
    private Transaction.CryptoTx cryptoType;
    private CITAj citAj;

    public String sendRawTransaction(String utf8String, String privateKey) {
        Transaction tx = buildTransaction(utf8String);
        try {
            String signedTx = tx.sign(privateKey, this.cryptoType, false);
            AppSendTransaction appSendTransaction
                    = citAj.appSendRawTransaction(signedTx).send();
            if(appSendTransaction.getError() != null){
                throw new RuntimeException("send transaction failed, " + appSendTransaction.getError().getMessage());
            }
            return appSendTransaction.getSendTransactionResult().getHash();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("send transaction failed. " + e.getMessage());
        }
    }

    private Transaction buildTransaction(String utf8String) {
        return new Transaction(
                EVIDENCE_ADDRESS,
                String.valueOf(System.nanoTime()),
                Quotas.estimateQuota(utf8String.getBytes(StandardCharsets.UTF_8).length),
                BlockchainUtil.getBlockNumber(citAj).longValue() + BlockchainUtil.VALID_UNTIL_BLOCK,
                2,
                new BigInteger("1"),
                DEFAULT_TX_VALUE,
                HexUtil.bytesToHex(utf8String.getBytes()));
    }

    public TransactionReceipt getTransactionReceiptByTxHash(String txHash) {
        return BlockchainUtil.getTransactionReceiptByTxHash(citAj, txHash);
    }

    public static Builder builder() {
        return new Builder();
    }



    public static class Builder {

        private Builder() {}

        private static Blockchain bc = new Blockchain();

        public Builder rpcAddr(String rpcAddr) {
            if (StringUtil.isBlank(rpcAddr)) {
                throw new NullPointerException("rpcAddr cannot be null!");
            }
            bc.rpcAddr = rpcAddr;
            bc.cryptoType = Transaction.CryptoTx.SM2;
            return this;
        }

        public Builder cryptoType(Transaction.CryptoTx cryptoType) {
            if (cryptoType == null) {
                cryptoType = Transaction.CryptoTx.SM2;
            }
            bc.cryptoType = cryptoType;
            return this;
        }

        public static Blockchain build() {
            bc.citAj = CITAj.build(new HttpService(bc.rpcAddr));
            return bc;
        }
    }
}
