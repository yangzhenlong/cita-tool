package com.github.yzl.cita.key;

import com.citahub.cita.crypto.ECKeyPair;
import com.citahub.cita.crypto.sm2.SM2;
import com.citahub.cita.crypto.sm2.SM2KeyPair;
import com.citahub.cita.crypto.sm2.SM2Keys;
import com.citahub.cita.protocol.core.methods.request.Transaction;

public class Keys {

    private Keys() {}

    private static final String HEX_PREFIX = "0x";

    public static Key create() {
        return create(Transaction.CryptoTx.SM2);
    }

    public static Key create(Transaction.CryptoTx cryptoType) {
        Key key = new Key();
        key.setCryptoTx(cryptoType);
        if (Transaction.CryptoTx.SECP256K1.equals(cryptoType)) {
            try {
                ECKeyPair keyPair = com.citahub.cita.crypto.Keys.createEcKeyPair();
                key.setPublicKey(HEX_PREFIX + keyPair.getPublicKey().toString(16));
                key.setPrivateKey(HEX_PREFIX + keyPair.getPrivateKey().toString(16));
                key.setAddress(HEX_PREFIX + com.citahub.cita.crypto.Keys.getAddress(key.getPublicKey()));
                return key;
            } catch (Exception var4) {
                var4.printStackTrace();
                throw new RuntimeException("create SECP256K1 key error, error message is: " + var4.getMessage());
            }
        } else if (Transaction.CryptoTx.SM2.equals(cryptoType)) {
            SM2 sm2 = new SM2();
            SM2KeyPair sm2KeyPair = sm2.generateKeyPair();
            key.setPublicKey(HEX_PREFIX + getPublicKeyFromSM2Key(sm2KeyPair.getPublicKey().getEncoded(false)));
            key.setPrivateKey(HEX_PREFIX + sm2KeyPair.getPrivateKey().toString(16));
            key.setAddress(HEX_PREFIX + SM2Keys.getAddress(sm2KeyPair.getPublicKey()));
            return key;
        } else {
            throw new IllegalArgumentException("CryptoTx type not supported!");
        }
    }

    private static String getPublicKeyFromSM2Key(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b1 : bytes) {
            String hex = Integer.toHexString(b1 & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

}
