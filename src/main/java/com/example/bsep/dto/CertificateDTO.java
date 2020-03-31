package com.example.bsep.dto;

import com.example.bsep.model.Certificate;

import java.util.Date;

public class CertificateDTO {

    private Long idSubject;
    private Date startDate;
    private Date endDate;
    private boolean ca;
    private boolean revoked;
    private Long idIssuer;
    private String purpose;
    private String city;
    private String name;
    private String surname;
    private String email;
    private String uid;

    public CertificateDTO(){

    }

    public CertificateDTO(Certificate certificate){
        this.startDate = certificate.getStartDate();
        this.endDate = certificate.getEndDate();
        this.ca = certificate.isCa();
        this.revoked = certificate.isRevoked();
        this.idIssuer = certificate.getIdIssuer();
        this.purpose = certificate.getPurpose();
        this.city = certificate.getPurpose();
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.uid = uid;
    }

    public Long getIdSubject(){
        return this.idSubject;
    }

    public void setIdSubject(Long idSubject){
        this.idSubject = idSubject;
    }

    public Date getStartDate(){
        return this.startDate;
    }

    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }

    public Date getEndDate(){
        return this.endDate;
    }

    public void setEndDate(Date endDate){
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

    public Long getIdIssuer(){
        return this.idIssuer;
    }

    public void setIdIssuer(Long idIssuer){
        this.idIssuer = idIssuer;
    }

    public String getPurpose(){
        return this.purpose;
    }

    public void setPurpose(String purpose){
        this.purpose = purpose;
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

    public String getUid(){  return this.uid; }

    public void setUid(String uid){
        this.uid = uid;
    }

}
