package com.github.yzl.cita.utils;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.methods.response.AppGetTransactionReceipt;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;

public class BlockchainUtil {

    private BlockchainUtil() {}

    public static final int VALID_UNTIL_BLOCK = 88;
    public static final String HEX_PREFIX = "0x";
    public static final String ABI_ADDRESS = "ffffffffffffffffffffffffffffffffff010001";

    public static BigInteger getBlockNumber(CITAj citAj) {
        try {
            return citAj.appBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("get block number failed. " + e.getMessage());
        }
    }

    public static TransactionReceipt getTransactionReceiptByTxHash(CITAj citAj, String txHash) {
        try {
            AppGetTransactionReceipt appGetTransactionReceipt = citAj.appGetTransactionReceipt(txHash).send();
            return appGetTransactionReceipt.getTransactionReceipt();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("getTransactionReceiptByTxHash failed. " + e.getMessage());
        }
    }

    public static String hexRemove0x(String hex) {
        if (hex.contains(HEX_PREFIX)) {
            return hex.substring(2);
        }
        return hex;
    }
}
