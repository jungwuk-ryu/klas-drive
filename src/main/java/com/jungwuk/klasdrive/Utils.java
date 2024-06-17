package com.jungwuk.klasdrive;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Utils {
    public static RSAPublicKey parsePublicKey(String publicKeyStr) throws Exception {
        String publicKeyPEM = publicKeyStr.replace("-----BEGIN PUBLIC KEY-----\n", "").replace("-----END PUBLIC KEY-----", "").replace("\n", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);

        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }
}
