package com.example.serverapp;

import android.util.Base64;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;

public class MyServer extends NanoHTTPD {
    private static final Logger LOG = Logger.getLogger(MyServer.class
            .getName());

    private KeyStoreService keyManager;

    MyServer(int port, PublicKey publicKey, PrivateKey privateKey) {
        super(port);
        keyManager = new KeyStoreService(publicKey, privateKey);
    }

    public static void main(String[] args) {
        ServerRunner.run(MyServer.class);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        MyServer.LOG.info(method + " '" + uri + "' ");

        if (uri.equals("/get")) {
            String msg = Base64.encodeToString(keyManager
                    .getPublicKey()
                    .getEncoded(), Base64.URL_SAFE);
            return newFixedLengthResponse(msg);
        }

        if (uri.equals("/encrypt")) {
            Map<String, String> parms = session.getParms();
            String givenString = parms.get("text");
            return newFixedLengthResponse(keyManager
                    .encrypt(givenString));
        }

        if (uri.equals("/decrypt")) {
            Map<String, String> parms = session.getParms();
            String givenString = parms.get("text");
            return newFixedLengthResponse(keyManager
                    .decrypt(givenString));
        }

        if (uri.equals("/certificate/get")) {
            Map<String, String> parms = session.getParms();
            String commonName = parms.get("CN");
            String organizationalUnit = parms.get("OU");
            String organization = parms.get("O");
            String locality = parms.get("L");
            String country = parms.get("C");

        }
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n"
                    + "  <p>Name: <input type='text' name='username'></p>\n"
                    + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }

        msg += "</body></html>\n";

        return newFixedLengthResponse(msg);
    }

    public interface SecurityConstants {
        String TYPE_RSA = "RSA";
        String PADDING_TYPE = "PKCS1Padding";
        String BLOCKING_MODE = "NONE";
    }

}
