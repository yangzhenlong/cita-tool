package com.github.yzl.cita.key;

import com.citahub.cita.protocol.core.methods.request.Transaction;

/**
 * 公私钥对
 */
public class Key {

    private Transaction.CryptoTx cryptoTx;

    private String publicKey;

    private String address;

    private String privateKey;

    public Transaction.CryptoTx getCryptoTx() {
        return cryptoTx;
    }

    public void setCryptoTx(Transaction.CryptoTx cryptoTx) {
        this.cryptoTx = cryptoTx;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
