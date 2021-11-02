package com.github.yzl.cita.contract;

public class DefaultContractCallback implements ContractCallback {

    @Override
    public void listen(DeployContractResponse response) {
        System.out.println("部署合约回调信息：" + response);
    }
}
