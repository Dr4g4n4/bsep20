package com.example.bsep.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Certificate {

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="idSubject", strategy = "increment")
    private Long idSubject;

    @Column(name = "startDate", nullable = false)
    private Date startDate;

    @Column(name = "endDate", nullable = false)
    private Date endDate;

    @Column(name = "ca", nullable = false)
    private boolean ca;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "idIssuer", nullable = false)
    private Long idIssuer;

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

    @Column(name = "uid", nullable = false)
    private String uid;

    public Certificate(){

    }

    public Certificate(Date startDate, Date endDate, boolean isCA, boolean isRevoked, Long idIssuer, String purpose, String city, String name, String surname, String email, String uid){
        super();
        this.startDate = startDate;
        this.endDate = endDate;
        this.ca = isCA;
        this.revoked = false;
        this.idIssuer = idIssuer;
        this.purpose = purpose;
        this.city = city;
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

    public String getUid(){  return this.uid; }

    public void setUid(String uid){
        this.uid = uid;
    }
}