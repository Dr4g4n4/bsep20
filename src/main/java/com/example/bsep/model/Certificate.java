package com.example.bsep.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // kome
    @Column(name = "serialNumberSubject", nullable = false, unique = true)
    private String serialNumberSubject;

    // ko
    @Column(name = "serialNumberIssuer", nullable = false)
    private String serialNumberIssuer;

    @Column(name = "startDate", nullable = false)
    private Date startDate;

    @Column(name = "endDate", nullable = false)
    private Date endDate;

    @Column(name = "ca", nullable = false)
    private boolean ca;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "purpose", nullable = false)
    private String purpose;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "extension", nullable = false)
    private String extension;

    public Certificate(){

    }

    public Certificate(Date startDate, String serialNumberSubject, String serialNumberIssuer, Date endDate, boolean isCA, boolean isRevoked,  String purpose, String city, String name, String surname, String email, String uid, String extension){
        super();
        this.serialNumberSubject = serialNumberSubject;
        this.serialNumberIssuer = serialNumberIssuer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ca = isCA;
        this.revoked = false;
        this.purpose = purpose;
        this.city = city;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.extension = extension;
    }

    public String getSerialNumberSubject(){
        return this.serialNumberSubject;
    }

    public void setSerialNumberSubject(String serialNumberSubject){
        this.serialNumberSubject = serialNumberSubject;
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

    public String getSerialNumberIssuer(){
        return this.serialNumberIssuer;
    }

    public void setSerialNumberIssuer(String serialNumberIssuer){
        this.serialNumberIssuer = serialNumberIssuer;
    }

    public String getPurpose(){
        return this.purpose;
    }

    public void setPurpose(String purpose){
        this.purpose = purpose;
    }

    public String getCity(){  return this.city; }

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

    public String getExtension(){
        return this.extension;
    }

    public void setExtension(String extension){
        this.extension = extension;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }
}
