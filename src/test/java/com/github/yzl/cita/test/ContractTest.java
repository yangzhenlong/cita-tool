package com.github.yzl.cita.test;

import com.citahub.cita.abi.TypeReference;
import com.citahub.cita.abi.datatypes.Function;
import com.citahub.cita.abi.datatypes.Type;
import com.citahub.cita.abi.datatypes.Utf8String;
import com.citahub.cita.protocol.core.methods.request.Transaction;
import com.github.yzl.cita.contract.Contract;
import com.github.yzl.cita.contract.ContractCallback;
import com.github.yzl.cita.contract.DeployContractResponse;
import com.github.yzl.cita.key.Key;
import com.github.yzl.cita.key.Keys;
import org.junit.Assert;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 合约测试
 */
public class ContractTest {

    public static void main(String[] args) {
        Key key = Keys.create(Transaction.CryptoTx.SM2);

        Contract contract = Contract
                .builder()
                .rpcAddr("http://localhost:1337").cryptoType(Transaction.CryptoTx.SM2)
                .build();

        // 读取合约文件
        String contractBin = ContractFileUtil.getFileContant("Example.bin");
        String contractAbi = ContractFileUtil.getFileContant("Example.abi");

        // 部署合约
        contract.deployContract(contractBin, contractAbi, key.getPrivateKey(), new CustomCallback(contract, key));
    }

    /**
     * 自定义合约部署的回调事件
     */
    private static class CustomCallback implements ContractCallback {

        private Contract contract;
        private Key key;

        public CustomCallback(Contract contract, Key key) {
            this.contract = contract;
            this.key = key;
        }

        @Override
        public void listen(DeployContractResponse response) {
            System.out.println("合约部署结果：" + response);

            // 调用 setName
            sendTransaction(contract, key.getPrivateKey(), response.getContractAddress());

            try {
                TimeUnit.SECONDS.sleep(6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 调用 getName
            call(contract, key.getAddress(), response.getContractAddress());
        }
    }

    private static void sendTransaction(Contract contract, String privateKey, String contractAddress) {
        String requestData = "zhangsan";
        String txHash = contract.sendTransaction(privateKey, contractAddress, "setName", Collections.singletonList(new Utf8String(requestData)));
        System.out.println("调用合约 setName 交易哈希：" + txHash);
    }

    private static void call(Contract contract, String msgSender, String contractAddress) {
        Function func = new Function("getName", Collections.emptyList(), Collections.singletonList(new TypeReference<Utf8String>() {
        }));
        List<Type> callResult = contract.call(msgSender, contractAddress, func);
        Utf8String result = (Utf8String)callResult.get(0);
        System.out.println("调用合约 getName 结果：" + result.getValue());
        Assert.assertEquals("zhangsan", result);
    }
}
