package com.github.yzl.cita.contract;

import com.citahub.cita.abi.FunctionEncoder;
import com.citahub.cita.abi.FunctionReturnDecoder;
import com.citahub.cita.abi.datatypes.Function;
import com.citahub.cita.abi.datatypes.Type;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameterName;
import com.citahub.cita.protocol.core.methods.request.Call;
import com.citahub.cita.protocol.core.methods.request.Transaction;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.protocol.http.HttpService;
import com.citahub.cita.protocol.system.CITASystemContract;
import com.citahub.cita.utils.HexUtil;
import com.github.yzl.cita.utils.BlockchainUtil;
import com.github.yzl.cita.utils.ObjectUtil;
import com.github.yzl.cita.utils.StringUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Contract {

    private static final Logger log = Logger.getLogger("Contract");

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

    private Contract(String rpcAddr, Transaction.CryptoTx cryptoType) {
        this.cryptoType = cryptoType;
        this.citAj = CITAj.build(new HttpService(rpcAddr));
    }

    private Transaction.CryptoTx cryptoType;
    private CITAj citAj;

    public static Builder builder() {
        return new Builder();
    }

    public String deployContract(String code, String abi, String privateKey, ContractCallback callback) {
        Transaction tx = Transaction.createContractTransaction(
                String.valueOf(System.nanoTime()),
                10000000,
                BlockchainUtil.getBlockNumber(citAj).longValue() + BlockchainUtil.VALID_UNTIL_BLOCK,
                2,
                new BigInteger("1"),
                "0",
                code);
        try {
            String signedTx = tx.sign(privateKey, this.cryptoType, false);
            AppSendTransaction appSendTransaction
                    = citAj.appSendRawTransaction(signedTx).send();
            if (appSendTransaction.getError() != null) {
                throw new RuntimeException("deploy contract failed, " + appSendTransaction.getError().getMessage());
            }
            String contractTxHash = appSendTransaction.getSendTransactionResult().getHash();
            contractCallback(privateKey, contractTxHash, abi, callback);
            return contractTxHash;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("deploy contract failed. " + e.getMessage());
        }
    }

    public String sendTransaction(String privateKey, String contractAddress, String methodName, List<Type> params) {
        String funcData = CITASystemContract.encodeFunction(methodName, params);
        Transaction tx = new Transaction(
                contractAddress,
                String.valueOf(System.nanoTime()),
                10000000,
                BlockchainUtil.getBlockNumber(citAj).longValue() + BlockchainUtil.VALID_UNTIL_BLOCK,
                2,
                BigInteger.valueOf(1),
                "0",
                funcData);
        try {
            String signedTx = tx.sign(privateKey, this.cryptoType, false);
            AppSendTransaction appSendTransaction
                    = citAj.appSendRawTransaction(signedTx).send();
            if (appSendTransaction.getError() != null) {
                throw new RuntimeException("send transaction to contract failed, " + appSendTransaction.getError().getMessage());
            }
            return appSendTransaction.getSendTransactionResult().getHash();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("send transaction to contract failed. " + e.getMessage());
        }
    }

    public List<Type> call(String msgSender, String contractAddress, Function func) {
        String funcData = FunctionEncoder.encode(func);
        log.info("call contract request data: " + funcData);
        Call call = new Call(msgSender, contractAddress, funcData);
        try {
            String result = this.citAj.appCall(call, DefaultBlockParameterName.PENDING)
                    .send().getValue();
            log.info("call contract response data: " + result);
            return FunctionReturnDecoder.decode(result, func.getOutputParameters());
        } catch (IOException e) {
            throw new RuntimeException("call contract data occurred exception.", e);
        }
    }

    private void contractCallback(String privateKey, String contractTxHash, String abi, ContractCallback callback) {

        executor.schedule(() -> {
            DeployContractResponse response = new DeployContractResponse();
            response.setContractTxHash(contractTxHash);
            TransactionReceipt receipt = BlockchainUtil.getTransactionReceiptByTxHash(citAj, contractTxHash);
            String contractAddress = receipt.getContractAddress();
            response.setContractAddress(contractAddress);
            String abiTxHash = deployABI(privateKey, contractAddress, abi);
            response.setAbiTxHash(abiTxHash);
            callback.listen(response);
        }, 6, TimeUnit.SECONDS);
    }

    private String deployABI(String privateKey, String contractAddress, String abi) {
        if (StringUtil.isBlank(abi)) {
            return null;
        }
        String hexData =
                BlockchainUtil.hexRemove0x(contractAddress) + BlockchainUtil.hexRemove0x(HexUtil.bytesToHex(abi.getBytes()));
        Transaction transaction = new Transaction(
                BlockchainUtil.ABI_ADDRESS, String.valueOf(System.nanoTime()), 10000000,
                BlockchainUtil.getBlockNumber(citAj).longValue() + BlockchainUtil.VALID_UNTIL_BLOCK,
                2, BigInteger.valueOf(1), "0", hexData);
        try {
            String signedTx = transaction.sign(
                    privateKey, this.cryptoType, false);
            AppSendTransaction txResult = this.citAj.appSendRawTransaction(signedTx).send();
            if (txResult.getError() != null) {
                throw new RuntimeException("deploy abi failed. error message: " + txResult.getError().getMessage());
            }
            log.info("deploy abi tx_hash: " + txResult.getSendTransactionResult().getHash());
            return txResult.getSendTransactionResult().getHash();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static class Builder {

        private String rpcAddr;
        private Transaction.CryptoTx cryptoType;

        public Builder rpcAddr(String rpcAddr) {
            this.rpcAddr = rpcAddr;
            return this;
        }

        public Builder cryptoType(Transaction.CryptoTx cryptoType) {
            this.cryptoType = cryptoType;
            return this;
        }

        public Contract build() {
            if (ObjectUtil.isNull(cryptoType)) {
                this.cryptoType = Transaction.CryptoTx.SM2;
            }
            return new Contract(this.rpcAddr, this.cryptoType);
        }
    }
}
