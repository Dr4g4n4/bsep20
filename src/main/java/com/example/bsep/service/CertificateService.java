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
import org.springframework.stereotype.Service;

@Service
public class CertificateService {

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    KeyStoreWriter keyStoreWriter;

    @Autowired
    KeyStoreReader keyStoreReader;

    public List<CertificateDTO> getAllCertificates(){
        List<Certificate> all = certificateRepository.findAll();
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            retValue.add(new CertificateDTO(c));
        }
        return retValue;
    }

    public List<CertificateDTO> getAllCaCertificates(){
        List<Certificate> all = certificateRepository.findAllByCa(true);
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            if(!c.isRevoked())
            retValue.add(new CertificateDTO(c));
        }
        return retValue;
    }

    public List<CertificateDTO> getAllNoCaCertificates(){
        List<Certificate> all = certificateRepository.findAllByCa(false);
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            if(!c.isRevoked())
            retValue.add(new CertificateDTO(c));
        }
        return retValue;
    }

    public List<CertificateDTO> getAllRevokedCertificates(){
        List<Certificate> all = certificateRepository.findAllByRevoked(true);
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            retValue.add(new CertificateDTO(c));
        }
        return retValue;
    }

    public List<CertificateDTO> getAllNoRevokedCertificates(){
        List<Certificate> all = certificateRepository.findAllByRevoked(false);
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            retValue.add(new CertificateDTO(c));
        }
        return retValue;
    }

    public void createSelfSignedCertificate(Certificate certificate){
        Certificate cert = certificateRepository.save(certificate);
        KeyPair selfKey = getKeyPair();
        SubjectData subjectData = getSubjectData(cert,selfKey.getPublic());

        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certificate.getName() + " " + certificate.getSurname());
        builder.addRDN(BCStyle.SURNAME, certificate.getName());
        builder.addRDN(BCStyle.GIVENNAME, certificate.getSurname());
        builder.addRDN(BCStyle.L , certificate.getCity());
        builder.addRDN(BCStyle.E, certificate.getEmail());
        builder.addRDN(BCStyle.UID, certificate.getUid());
        IssuerData issuerData = new IssuerData(selfKey.getPrivate(), builder.build());
        CertificateGenerator certGenerator = new CertificateGenerator();
        X509Certificate certX509 = certGenerator.generateCertificate(subjectData, issuerData);
        String keyStoreFile = "ks/"+certificate.getCity()+ certificate.getUid() + ".jks";

        // generisanje keyStore
        KeyStoreWriter keyStoreW = new KeyStoreWriter();
        keyStoreW.loadKeyStore(null, "sifra1".toCharArray());
        keyStoreW.write(subjectData.getSerialNumber(), selfKey.getPrivate(), "sifra1".toCharArray(), certX509);
        keyStoreW.saveKeyStore(keyStoreFile, "sifra1".toCharArray());

        keyStoreFile = "ks/ksCA.jks";
        KeyStoreWriter kw = new KeyStoreWriter();
        kw.loadKeyStore(keyStoreFile, "sifra1".toCharArray());
        kw.write(subjectData.getSerialNumber(), selfKey.getPrivate(), "sifra1".toCharArray(), certX509);
        kw.saveKeyStore(keyStoreFile, "sifra1".toCharArray());
    }

    public void createNonSelfSignedCertificate(Certificate certificate){
        Certificate newCertificate = certificateRepository.save(certificate);
        // ucitava se privatni kljuc sertifikata koji izdaje neki drugi sertifikat
        IssuerData issuerData = keyStoreReader.readIssuerFromStore("ks/ksCA.jks", certificate.getSerialNumberIssuer(), "sifra1".toCharArray(), "sifra1".toCharArray());
        KeyPair subjectKey = getKeyPair();
        SubjectData subjectData = getSubjectData(newCertificate,subjectKey.getPublic());
        CertificateGenerator certGenerator = new CertificateGenerator();
        X509Certificate certX509 = certGenerator.generateCertificate(subjectData, issuerData);
        String keyStoreFile = "";

        if(certificate.isCa()) {
            keyStoreFile = "ks/ksCA.jks";
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
        String serialNumber = certificate.getSerialNumberSubject();

        // podaci vlasnika
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certificate.getName() + " " + certificate.getSurname());
        builder.addRDN(BCStyle.SURNAME, certificate.getName());
        builder.addRDN(BCStyle.GIVENNAME, certificate.getSurname());
        builder.addRDN(BCStyle.L , certificate.getCity());
        builder.addRDN(BCStyle.E, certificate.getEmail());
        builder.addRDN(BCStyle.UID, certificate.getUid());

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

    public CertificateDTO getCertificate(Long id){
        return new CertificateDTO(certificateRepository.findOneById(id));
    }

    public CertificateDTO getCertificate(String serialNumber){
        return new CertificateDTO(certificateRepository.findOneBySerialNumberSubject(serialNumber));
    }}