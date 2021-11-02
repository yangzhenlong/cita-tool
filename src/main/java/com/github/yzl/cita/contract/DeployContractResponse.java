package com.github.yzl.cita.contract;

public class DeployContractResponse {

    /**
     * 部署合约的交易哈希
     */
    private String contractTxHash;
    /**
     * 部署合约的合约地址
     */
    private String contractAddress;
    /**
     * 部署合约 abi 的交易哈希
     */
    private String abiTxHash;

    public String getContractTxHash() {
        return contractTxHash;
    }

    public void setContractTxHash(String contractTxHash) {
        this.contractTxHash = contractTxHash;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getAbiTxHash() {
        return abiTxHash;
    }

    public void setAbiTxHash(String abiTxHash) {
        this.abiTxHash = abiTxHash;
    }

    @Override
    public String toString() {
        return "DeployContractResponse{" +
                "contractTxHash='" + contractTxHash + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", abiTxHash='" + abiTxHash + '\'' +
                '}';
    }
}
