package com.example.bsep.data;

import sun.security.x509.X500Name;

import java.security.PrivateKey;

public class IssuerData {

    private X500Name x500name;
    private PrivateKey privateKey;

    public IssuerData() {
    }

    public IssuerData(PrivateKey privateKey, X500Name x500name) {
        this.privateKey = privateKey;
        this.x500name = x500name;
    }

    public X500Name getX500name() {
        return x500name;
    }

    public void setX500name(X500Name x500name) {
        this.x500name = x500name;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

}
