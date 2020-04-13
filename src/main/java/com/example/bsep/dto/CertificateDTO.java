package com.example.bsep.dto;

import com.example.bsep.model.Certificate;

import java.util.ArrayList;
import java.util.List;

public class CertificateDTO {

    private  Long id;
    private String serialNumberSubject;
    private String serialNumberIssuer;
    private String startDate;
    private String endDate;
    private boolean ca;
    private boolean revoked;

    private String city;
    private String name;
    private String surname;
    private String email;
    private ArrayList<String> keyUsage;
    private ArrayList<String> extendedKeyUsage;

    public CertificateDTO(){

    }

    public CertificateDTO(Certificate certificate){
        this.id = certificate.getId();
        this.serialNumberSubject = certificate.getSerialNumberSubject();
        this.serialNumberIssuer = certificate.getSerialNumberIssuer();
        this.startDate = certificate.getStartDate().toString();
        this.endDate = certificate.getEndDate().toString();
        this.ca = certificate.isCa();
        this.revoked = certificate.isRevoked();
        this.city = certificate.getCity();
        this.name = certificate.getName();
        this.surname = certificate.getSurname();
        this.email = certificate.getEmail();
    }

    public String getSerialNumberSubject(){
        return this.serialNumberSubject;
    }

    public void setSerialNumberSubject(String serialNumberSubject){
        this.serialNumberSubject = serialNumberSubject;
    }

    public String getStartDate(){
        return this.startDate;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }

    public String getEndDate(){
        return this.endDate;
    }

    public void setEndDate(String endDate){
        this.endDate = endDate;
    }

    public boolean isCa(){
        return this.ca;
    }

    public void setCa(boolean ca){
        this.ca = ca;
    }

    public boolean isRevoked(){
        return this.revoked;
    }

    public void setRevoked(boolean revoked){
        this.ca = revoked;
    }

    public String getSerialNumberIssuer(){
        return this.serialNumberIssuer;
    }

    public void setSerialNumberIssuer(String serialNumberIssuer){
        this.serialNumberIssuer = serialNumberIssuer;
    }



    public String getCity(){
        return this.city;
    }

    public void setCity(String city){
        this.city = city;
    }

    public String getName(){  return this.name; }

    public void setName(String name){
        this.name = name;
    }

    public String getSurname(){  return this.surname; }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public String getEmail(){  return this.email; }

    public void setEmail(String email){
        this.email = email;
    }



    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }

    public ArrayList<String> getKeyUsage() {
        return keyUsage;
    }

    public void setKeyUsage(ArrayList<String> keyUsage) {
        this.keyUsage = keyUsage;
    }

    public ArrayList<String> getExtendedKeyUsage() {
        return extendedKeyUsage;
    }

    public void setExtendedKeyUsage(ArrayList<String> extendedKeyUsage) {
        this.extendedKeyUsage = extendedKeyUsage;
    }
}

