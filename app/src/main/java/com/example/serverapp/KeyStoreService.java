package com.example.serverapp;

import android.util.Base64;
import android.util.Log;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

class KeyStoreService {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    KeyStoreService() {
    }

    KeyStoreService(PublicKey publicKey, PrivateKey privateKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    PrivateKey getPrivateKey() {
        return privateKey;
    }

    PublicKey getPublicKey() {
        return publicKey;
    }

    KeyPair getKeyPair() {
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
            kpg.initialize(2048);
            kp = kpg.generateKeyPair();
        } catch (Exception e) {
            Log.e("error", e.toString());
        }

        return kp;
    }

    private Cipher getCipher() throws NoSuchPaddingException,
            NoSuchAlgorithmException {
        return Cipher.getInstance(
                String.format("%s/%s/%s",
                        MyServer.SecurityConstants.TYPE_RSA,
                        MyServer.SecurityConstants.BLOCKING_MODE,
                        MyServer.SecurityConstants.PADDING_TYPE));
    }

    String encrypt(String plaintext) {
        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeToString(
                    cipher.doFinal(plaintext.getBytes()), Base64.URL_SAFE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String decrypt(String ciphertext) {
        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(
                    Base64.decode(ciphertext, Base64.URL_SAFE)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}