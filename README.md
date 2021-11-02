# CITA BlockChain Transaction Tools

## generate key

```java
Key key1 = Keys.create(Transaction.CryptoTx.SM2);
Key key2 = Keys.create(Transaction.CryptoTx.SECP256K1);
```

## Send Transaction

> see com.github.yzl.cita.test.TransactionTest#testTransaction

```java
public class TransactionTest {

    @Test
    public void testTransaction() throws InterruptedException, JsonProcessingException {
        // generate key
        Key key = Keys.create(Transaction.CryptoTx.SM2);

        // build blockchain object
        Blockchain blockchain = Blockchain.builder().rpcAddr("http://localhost:1337").cryptoType(Transaction.CryptoTx.SM2).build();

        // sendRawTransaction
        String txHash = blockchain.sendRawTransaction("abc", key.getPrivateKey());
        System.out.println("txHash: " + txHash);

        // getTransactionReceipt
        TimeUnit.SECONDS.sleep(6);
        TransactionReceipt receipt = blockchain.getTransactionReceiptByTxHash(txHash);
        System.out.println(new ObjectMapper().writeValueAsString(receipt));
    }
}
```

## Call Contract

> see com.github.yzl.cita.test.ContractTest#main

```java

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
}

```

测试结果：
```sh
com.github.yzl.cita.contract.Contract deployABI
信息: deploy abi tx_hash: 0x138d84776f252f80eb0f96cd5421773ae7fcc8ade22c9a42fd3428e1c658fa55
合约部署结果：DeployContractResponse{contractTxHash='0x9eff24ac8cb2fe2730a8cc1640fb8576f119992101b66382370b920c5a8680ce', contractAddress='0x7f2e88807f39c86e55594571d6a45a5abbf072ab', abiTxHash='0x138d84776f252f80eb0f96cd5421773ae7fcc8ade22c9a42fd3428e1c658fa55'}
调用合约 setName 交易哈希：0x2e905a8fab5563139b34690901f425311371e3f83bcc5a8ccdf82a69f8cf003d
com.github.yzl.cita.contract.Contract call
信息: call contract request data: 0x17d7de7c
com.github.yzl.cita.contract.Contract call
信息: call contract response data: 0x000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000087a68616e6773616e000000000000000000000000000000000000000000000000
调用合约 getName 结果：zhangsan
```

## Parse Contract Object To Java Object