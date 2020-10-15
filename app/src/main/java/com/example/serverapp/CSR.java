/*
package com.example.serverapp;

import com.example.serverapp.cryptolib.CryptoLibrary;

import org.spongycastle.pkcs.PKCS10CertificationRequest;

import java.security.PrivateKey;
import java.security.PublicKey;

class CSR {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    CSR(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

        // generate the CSR and return it in bytes format
    PKCS10CertificationRequest generateByteCertificate(
            String commonName,
            String organizationalUnit,
            String organization,
            String locality,
            String country
    ) {
        String issuerString = "C=" + country + ", O=" + organization
                + ", OU=" + organizationalUnit + ", CN=" + commonName + ", L=" + locality;
        CryptoLibrary cryptoLibrary = new CryptoLibrary();
        return cryptoLibrary.generateVariantCodingUserCsr(issuerString,
                "", "", "", "", "");
    }
}
*/
