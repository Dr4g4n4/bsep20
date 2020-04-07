package com.example.bsep.service;

import com.example.bsep.certificates.CertificateGenerator;
import com.example.bsep.certificates.CertificateReader;
import com.example.bsep.data.IssuerData;
import com.example.bsep.data.SubjectData;
import com.example.bsep.dto.CertificateDTO;
import com.example.bsep.keystores.KeyStoreReader;
import com.example.bsep.keystores.KeyStoreWriter;
import com.example.bsep.model.Certificate;
import com.example.bsep.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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

    //@Autowired
    //CertificateReader certReader;

    public boolean validateFileds(Certificate certificate){
        boolean returnValue = true;
        try{
            Long.parseLong(certificate.getSerialNumberIssuer());
            Long.parseLong(certificate.getSerialNumberSubject());
            returnValue = true;
        }catch (NumberFormatException e){
            returnValue = false;
        }

        List<Certificate>certificates = certificateRepository.findAll();
        for(Certificate c:certificates){
            if(c.getSerialNumberSubject().equals(certificate.getSerialNumberSubject()) || c.getEmail().equals(certificate.getEmail())){
                returnValue = false;
            }
        }

        if(!certificate.getPurpose().equals("POTPISIVANJE SERTIFIKATA") && !certificate.getPurpose().equals("POTPISIVANJE CRLa")){
            returnValue = false;
        }

        if(certificate.getEndDate().compareTo(certificate.getStartDate()) <= 0){
            returnValue = false;
        }

         String emailRegex = "^[a-zA-Z0-9_+&*-] + (?:\\."+
                 "[a-zA-Z0-9_+&*-]+)*@"+
                 "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if(certificate.getEmail() == null || pattern.matcher(certificate.getEmail()).matches()){
            returnValue = false;
        }

        if(certificate.getCity()==null || certificate.getCity().equals("") || certificate.getName()==null || certificate.getName().equals("")){
            returnValue = false;
        }

        // provjera datuma

        Certificate issuer = certificateRepository.findOneBySerialNumberSubject(certificate.getSerialNumberIssuer());
        if(issuer!= null){
            if(issuer.getEndDate().compareTo(certificate.getEndDate()) < 0 ){
                returnValue = false;
            }
        }
        System.out.println("Da li je CA:   " + certificate.isCa());
        return returnValue;
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
        List<Certificate> all = certificateRepository.findAllByCa(true);
        List<CertificateDTO> retValue = new ArrayList<CertificateDTO>();
        for( Certificate c : all){
            if((!c.isRevoked()) && (c.isCa()))
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

    public boolean createSelfSignedCertificate(Certificate certificate, boolean isCa){
        certificate.setCa(isCa);
        boolean ok = validateFileds(certificate);
        certificateRepository.save(certificate);
        if(ok){
            Certificate cert = certificateRepository.save(certificate);
            KeyPair selfKey = getKeyPair();
            SubjectData subjectData = getSubjectData(cert,selfKey.getPublic());

            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.CN, certificate.getName() + " " + certificate.getSurname());
            builder.addRDN(BCStyle.SURNAME, certificate.getName());
            builder.addRDN(BCStyle.GIVENNAME, certificate.getSurname());
            builder.addRDN(BCStyle.L , certificate.getCity());
            builder.addRDN(BCStyle.E, certificate.getEmail());

            IssuerData issuerData = new IssuerData(selfKey.getPrivate(), builder.build());
            CertificateGenerator certGenerator = new CertificateGenerator();
            X509Certificate certX509 = certGenerator.generateCertificate(subjectData, issuerData);
            String keyStoreFile = "ks/"+certificate.getCity() + "_" + certificate.getEmail() + ".jks";

            // generisanje keyStore
            KeyStoreWriter keyStoreW = new KeyStoreWriter();
            keyStoreW.loadKeyStore(null, "sifra1".toCharArray());
            System.out.println("Serijski broj za prvo:  " + subjectData.getSerialNumber());
            keyStoreW.write(subjectData.getSerialNumber(), selfKey.getPrivate(), "sifra1".toCharArray(), certX509);
            keyStoreW.saveKeyStore(keyStoreFile, "sifra1".toCharArray());

            keyStoreFile = "ks/ksCA.jks";
            KeyStoreWriter kw = new KeyStoreWriter();
            kw.loadKeyStore(keyStoreFile, "sifra1".toCharArray());
            kw.write(subjectData.getSerialNumber(), selfKey.getPrivate(), "sifra1".toCharArray(), certX509);
            kw.saveKeyStore(keyStoreFile, "sifra1".toCharArray());
            return true;
        }
        else{
            return false;
        }

    }

    public boolean createNonSelfSignedCertificate(Certificate certificate, boolean isCa){
        certificate.setCa(isCa);
        boolean ok = validateFileds(certificate);
        if(ok){
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
            return true;
        }
        else{
            return false;
        }
    }

    private SubjectData getSubjectData(Certificate certificate, PublicKey pk) {
        //KeyPair keyPairSubject = getKeyPair();
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

        System.out.println("Serijski broj:  " + serialNumber);
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
    }

    public File downloadCertificate(Long id) {
        CertificateDTO wantedCertificate = getCertificate(id);
        java.security.cert.Certificate c = findFromFile(wantedCertificate.getSerialNumberSubject(), wantedCertificate.isCa());
        File downloadFile  = writeCertificate(c);
        try {
            FileInputStream inStream = new FileInputStream(downloadFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return downloadFile;
    }

    private java.security.cert.Certificate findFromFile(String serialNumber, boolean isCA) {
        String keyStoreFile = isCA ? "ks/ksCA.jks" : "ks/nonCA_KS.jks" ;
        return keyStoreReader.readCertificate(keyStoreFile, "sifra1", serialNumber);
    }

    private File writeCertificate(java.security.cert.Certificate cert) {
        File file = new File("certificate.cer");
        FileOutputStream os = null;
        byte[] buf = new byte[0];
        try {
            buf = cert.getEncoded();
            os = new FileOutputStream(file);
            os.write(buf);
            Writer wr = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            wr.write(new sun.misc.BASE64Encoder().encode(buf));
            wr.flush();
            os.close();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public boolean isValid(String alias){
        boolean ret = true;
        // ret = isRevoked(certificate)
        if(ret){
            return checkValidity(alias);
        }
        else {
            return false;
        }
    }

    private boolean checkValidity(String alias){
        Certificate cert = certificateRepository.findOneBySerialNumberSubject(alias);
        X509Certificate cer = (X509Certificate)findFromFile(alias, cert.isCa());
        if(cer.getSigAlgName().equals("SHA-1")){
            return false;
        }
        try {
            cer.verify(cer.getPublicKey());
        } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
        try {
            cer.checkValidity();
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            e.printStackTrace();
            return false;
        }

        if(!cert.isCa()){
            return false;
        }
        if(cert.getSerialNumberSubject().equals(cert.getSerialNumberIssuer())){
            return true;
        }
        else{
            return checkValidity(cert.getSerialNumberIssuer());
        }
    }
}