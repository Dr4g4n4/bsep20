package com.example.bsep.dto;

import java.sql.Timestamp;

public class RevocationDetails {
    private  Long id;
    private String serialNumberSubject;
    private String serialNumberIssuer;
    private String revocationReason;
    private String revocationTimestamp;

    public RevocationDetails(Long id, String serialNumberSubject, String serialNumberIssuer, String revocationReason, String revocationTimestamp) {
        this.id = id;
        this.serialNumberSubject = serialNumberSubject;
        this.serialNumberIssuer = serialNumberIssuer;
        this.revocationReason = revocationReason;
        this.revocationTimestamp = revocationTimestamp;
    }

    public RevocationDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumberSubject() {
        return serialNumberSubject;
    }

    public void setSerialNumberSubject(String serialNumberSubject) {
        this.serialNumberSubject = serialNumberSubject;
    }

    public String getSerialNumberIssuer() {
        return serialNumberIssuer;
    }

    public void setSerialNumberIssuer(String serialNumberIssuer) {
        this.serialNumberIssuer = serialNumberIssuer;
    }

    public String getRevocationReason() {
        return revocationReason;
    }

    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }

    public String getRevocationTimestamp() {
        return revocationTimestamp;
    }

    public void setRevocationTimestamp(String revocationTimestamp) {
        this.revocationTimestamp = revocationTimestamp;
    }
}
