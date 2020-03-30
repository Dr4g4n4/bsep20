package com.example.bsep.service;

import com.example.bsep.certificates.CertificateGenerator;
import com.example.bsep.data.IssuerData;
import com.example.bsep.data.SubjectData;
import com.example.bsep.dto.CertificateDTO;
import com.example.bsep.keystores.KeyStoreReader;
import com.example.bsep.keystores.KeyStoreWriter;
import com.example.bsep.model.Certificate;
import com.example.bsep.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

public class CertificateService {

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    KeyStoreWriter keyStoreWriter;

    @Autowired
    KeyStoreReader keyStoreReader;

    public Certificate getById(Long id) {
        return certificateRepository.findOneByIdSubject(id);
    }

    public List<CertificateDTO> getAllCertificates(){
        List<Certificate> all = certificateRepository.findAll();
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            retValue.add(new CertificateDTO(c));
        }
        return retValue;
    }

    public List<CertificateDTO> getAllCaCertificates(){
        List<Certificate> all = certificateRepository.findAllByCa();
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            retValue.add(new CertificateDTO(c));
        }
        return retValue;
    }

    public List<CertificateDTO> getAllNoCaCertificates(){
        List<Certificate> all = certificateRepository.findAllByNonCa();
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            retValue.add(new CertificateDTO(c));
        }
        return retValue;
    }

    public List<CertificateDTO> getAllRevokedCertificates(){
        List<Certificate> all = certificateRepository.findAllByRevoked();
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            retValue.add(new CertificateDTO(c));
        }
        return retValue;
    }

    public List<CertificateDTO> getAllNoRevokedCertificates(){
        List<Certificate> all = certificateRepository.findAllByNonRevoked();
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            retValue.add(new CertificateDTO(c));
        }
        return retValue;
    }

    public void createCertificate(Certificate certificate, Long idIssuer){
        Certificate newCertificate = certificateRepository.save(certificate);

        certificate.setIdIssuer(idIssuer);
        IssuerData issuerData = keyStoreReader.readIssuerFromStore("ks/ksCA.jks", Long.toString(idIssuer), "sifra1".toCharArray(), "sifra1".toCharArray());
        KeyPair subjectKey = getKeyPair();
        SubjectData subjectData = getSubjectData(newCertificate,subjectKey.getPublic());
        CertificateGenerator certGenerator = new CertificateGenerator();
        X509Certificate certX509 = certGenerator.generateCertificate(subjectData, issuerData);
        String keyStoreFile = "ks/"+certificate.getCity()+".jks";

        // generisanje keyStore
        KeyStoreWriter keyStoreW = new KeyStoreWriter();
        keyStoreW.loadKeyStore(null, "sifra1".toCharArray());
        keyStoreW.write(subjectData.getSerialNumber(), subjectKey.getPrivate(), "admin123".toCharArray(), certX509);
        keyStoreW.saveNewKeyStore(keyStoreFile, "sifra1".toCharArray());

        keyStoreW = new KeyStoreWriter();
        keyStoreW.loadKeyStore(null, "sifra1".toCharArray());
        keyStoreW.saveNewKeyStore("ks/"+certificate.getCity()+"Store.jks", "sifra1".toCharArray());

        if(certificate.isCa()) {
            keyStoreFile = "ks/KSca.jks";
        }
        else {
            keyStoreFile = "ks/nonCA_KS.jks";
        }

        KeyStoreWriter kw = new KeyStoreWriter();

        kw.loadKeyStore(keyStoreFile, "sifra1".toCharArray());
        kw.write(subjectData.getSerialNumber(), subjectKey.getPrivate(), "sifra1".toCharArray(), certX509);
        kw.saveKeyStore(keyStoreFile, "sifra1".toCharArray());

    }

    private SubjectData getSubjectData(Certificate certificate, PublicKey pk) {
        KeyPair keyPairSubject = getKeyPair();
        Date startDate = certificate.getStartDate();
        Date endDate = certificate.getEndDate();
        String serialNumber = Long.toString(certificate.getIdIssuer());

        // podaci vlasnika
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, "Pero Peric");
        builder.addRDN(BCStyle.SURNAME, "Pero");
        builder.addRDN(BCStyle.GIVENNAME, "Peric");
        builder.addRDN(BCStyle.L , certificate.getCity());
        builder.addRDN(BCStyle.E, "peroperic@uns.ac.rs");
        builder.addRDN(BCStyle.UID, "1234");

        return new SubjectData(pk, builder.build(), serialNumber, startDate, endDate);
    }

    private KeyPair getKeyPair(){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }
}