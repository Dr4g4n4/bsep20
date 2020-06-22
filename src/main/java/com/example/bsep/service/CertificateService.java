package com.example.bsep.service;

import com.example.bsep.certificates.CertificateGenerator;
import com.example.bsep.data.IssuerData;
import com.example.bsep.data.SubjectData;
import com.example.bsep.dto.CertificateDTO;
import com.example.bsep.dto.RevocationDetails;
import com.example.bsep.keystores.KeyStoreReader;
import com.example.bsep.keystores.KeyStoreWriter;
import com.example.bsep.repository.CertificateRepository;
import com.example.bsep.validation.RegExp;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.ocsp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import com.example.bsep.model.Certificate;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.stereotype.Service;
import org.owasp.encoder.Encode;


@Service
public class CertificateService {

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    KeyStoreWriter keyStoreWriter;

    @Autowired
    KeyStoreReader keyStoreReader;

    private Revocation revocation;

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

        /*if(!certificate.getPurpose().equals("POTPISIVANJE SERTIFIKATA") && !certificate.getPurpose().equals("POTPISIVANJE CRLa")){
            returnValue = false;
        }*/

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
        if(issuer!= null) {
            if (issuer.getEndDate().compareTo(certificate.getEndDate()) < 0) {
                returnValue = false;
            }
        }
        if(!certificate.isCa()){
            RegExp reg = new RegExp();
            if(!reg.isValidEmail(certificate.getEmail())){
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
            if(isValid(c.getSerialNumberSubject()))
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

    public Certificate setForHtml(Certificate certificate){
        certificate.setCity(Encode.forHtml(certificate.getCity()));
        certificate.setName(Encode.forHtml(certificate.getName()));
        certificate.setSurname(Encode.forHtml(certificate.getSurname()));
        return certificate;
    }

    public boolean createSelfSignedCertificate(Certificate certificate, boolean isCa){
        certificate.setCa(isCa);
        boolean ok = validateFileds(certificate);
        if(ok){
            certificate = setForHtml(certificate);
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
            X509Certificate certX509 = certGenerator.generateCertificate(subjectData, issuerData,certificate.getKeyUsage(),certificate.getExtendedKeyUsage(),true);

            String keyStoreFile = "ks/ksCA.jks";
            KeyStoreWriter kw = new KeyStoreWriter();
            kw.loadKeyStore(keyStoreFile, "sifra1".toCharArray());
            kw.write(subjectData.getSerialNumber(), selfKey.getPrivate(), "sifra1".toCharArray(), new java.security.cert.Certificate[]{certX509});
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
            X509Certificate certX509 = certGenerator.generateCertificate(subjectData, issuerData,certificate.getKeyUsage(),certificate.getExtendedKeyUsage(),false);
            String keyStoreFile = "";

            if(certificate.isCa()) {
                keyStoreFile = "ks/ksCA.jks";
            }
            else {
                keyStoreFile = "ks/nonCA_KS.jks";
            }

            KeyStoreWriter kw = new KeyStoreWriter();
            java.security.cert.Certificate[] chain = addToChain(keyStoreFile, isCa, kw, certificate.getSerialNumberIssuer(), certX509);
            kw.write(subjectData.getSerialNumber(), subjectKey.getPrivate(), "sifra1".toCharArray(), chain);
            kw.saveKeyStore(keyStoreFile, "sifra1".toCharArray());
            return true;
        }
        else{
            return false;
        }
    }

    private java.security.cert.Certificate[] addToChain(String keyStoreFile, boolean isCA, KeyStoreWriter kw, String serialNumber, X509Certificate newCert){
        java.security.cert.Certificate[] ret = new java.security.cert.Certificate[1];
        kw.loadKeyStore(keyStoreFile, "sifra1".toCharArray());
        if (isCA) {
            java.security.cert.Certificate[] chain = kw.getChain(serialNumber);     // issuer's chain
            ArrayList<java.security.cert.Certificate> convertedArray = new ArrayList<java.security.cert.Certificate>(Arrays.asList(chain));
            convertedArray.add(newCert);
            ret = convertedArray.toArray(chain);
        } else {
            ret[0] = newCert;
        }

        return ret;
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
        ret = isRevoked(alias);
        if(ret){
            return checkValidity(alias);
        }
        else {
            return false;
        }
    }
    public boolean revokeCertificate(RevocationDetails details) {
          Certificate baseCertificate = revokeOne(details.getSerialNumberSubject(), details);     // ako nije ca, ovde je zavrsen posao

        if (baseCertificate.isCa()) {       // ucitati sve i povuci sve ispod ovog
            ArrayList<java.security.cert.Certificate> allCACertificates = revokeTheOnesBelow(baseCertificate.getSerialNumberSubject(), "ks/ksCA.jks");
            ArrayList<java.security.cert.Certificate> allEECertificates = keyStoreReader.readAllCertificates("ks/nonCA_KS.jks", "sifra1");

            for (java.security.cert.Certificate ee : allEECertificates) {   // povlacenje malih
                for ( java.security.cert.Certificate ca :  allCACertificates) {
                    if (((X509Certificate)ee).getIssuerX500Principal().getName()
                            .equals(((X509Certificate)ca).getSubjectX500Principal().getName())) {
                        baseCertificate = revokeOne(((X509Certificate)ee).getSerialNumber().toString(), details);
                        break;
                    }
                }
            }

            for ( java.security.cert.Certificate ca :  allCACertificates) {     // povlacenje ca
                baseCertificate = revokeOne(((X509Certificate)ca).getSerialNumber().toString(), details);
            }
        }
        return true;
    }

    private Certificate revokeOne(String serialNumber, RevocationDetails details) {
        Certificate baseCertificate = certificateRepository.findOneBySerialNumberSubject(serialNumber);
        baseCertificate.setRevoked(true);
        baseCertificate.setRevocationReason(details.getRevocationReason());
        baseCertificate.setRevocationTimestamp(details.getRevocationTimestamp());
        certificateRepository.save(baseCertificate);
        return baseCertificate;
    }

    private ArrayList<java.security.cert.Certificate> revokeTheOnesBelow(String serialNumber, String keystoreFile) {
        KeyStore ks = null;
        ArrayList<java.security.cert.Certificate> certs = new ArrayList<>(50);
        try {
            ks = KeyStore.getInstance("JKS", "SUN");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(Paths.get(ResourceUtils.getFile("classpath:")+"\\..\\..\\src\\main\\resources").toRealPath().toString() + "\\" + keystoreFile));
            ks.load(in, "sifra1".toCharArray());
            Enumeration<String> es = ks.aliases();
            String alias = "";
            while (es.hasMoreElements()) {
                alias = (String) es.nextElement();
                java.security.cert.Certificate[] chain = ks.getCertificateChain(alias);
                java.security.cert.Certificate c = chain[chain.length-1];

                for (int i = 0; i < chain.length; i++) {
                    if (((X509Certificate)chain[i]).getSerialNumber().toString().equals(serialNumber)) {
                        certs.add(c);
                        break;
                    }
                }
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return certs;
    }

    public List<Certificate> revokedCertificates(boolean isRevoked) {
        return certificateRepository.findAllByRevoked(isRevoked);
    }

    private boolean checkValidity(String alias){
        Certificate cert = certificateRepository.findOneBySerialNumberSubject(alias);
        Certificate certIssuer = certificateRepository.findOneBySerialNumberSubject(cert.getSerialNumberIssuer());
        X509Certificate cer = (X509Certificate)findFromFile(alias, cert.isCa());
        X509Certificate cerIssuer = (X509Certificate)findFromFile(cert.getSerialNumberIssuer(), true);
        if(cer.getSigAlgName().equals("SHA-1")){
            return false;
        }
        try {
            cer.verify(cerIssuer.getPublicKey());
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

        if(!certIssuer.isCa()){
            return false;
        }
        if(cert.getSerialNumberSubject().equals(cert.getSerialNumberIssuer())){
            return true;
        }
        else{
            return checkValidity(cert.getSerialNumberIssuer());
        }
    }

    public boolean isRevoked(String alias) {
        CertificateDTO cert;
        String keyStoreFile = keyStoreFileHelper(alias);
        ArrayList<java.security.cert.Certificate> certsToCheck = keyStoreReader.readCertificateChain(keyStoreFile, "sifra1", alias);

        for (java.security.cert.Certificate c : certsToCheck) {
            cert = getCertificate(((X509Certificate)c).getSerialNumber().toString());
            if ( cert.isRevoked() ) {
                return false;           // cim je jedan u lancu povucen, ne valja sert
            }
        }
        return true;        // lanac je ispravan
    }

    public String keyStoreFileHelper(String alias) {
        CertificateDTO cert = getCertificate(alias);
        return cert.isCa() ? "ks/ksCA.jks" : "ks/nonCA_KS.jks" ;
    }

    private int handleOCSP(OCSPReq ocspreq, String certAlias) throws IOException {
        BasicOCSPRespBuilder respBuilder = revocation.initOCSPRespBuilder(ocspreq, getPublicKey(certAlias));
        Req[] requests = ocspreq.getRequestList();
        java.security.cert.Certificate respCertificate = null;
        for (Req req : requests) {
            Certificate cert = new Certificate(getCertificate(certAlias));
            respCertificate = findFromFile(certAlias, cert.isCa());
            if (cert == null) {
                respBuilder.addResponse(req.getCertID(), new UnknownStatus());
            } else if (!isRevoked(certAlias)) {  // Check if certificate has been revoked
                try {
                    Date revokedDate = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(cert.getRevocationTimestamp());
                    respBuilder.addResponse(req.getCertID(), new RevokedStatus(revokedDate , revocation.getCodeFormString(cert.getRevocationReason())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                respBuilder.addResponse(req.getCertID(), CertificateStatus.GOOD);
            }

        }
        OCSPResp response = revocation.generateOCSPResponse(respBuilder, respCertificate);

        return response.getStatus();
    }

    public PublicKey getPublicKey(String alias) {       // vraca javni kljuc sertifikata
        CertificateDTO certFromBase = getCertificate(alias);
        java.security.cert.Certificate cert = findFromFile(alias, certFromBase.isCa());
        return cert.getPublicKey();
    }

    public int middlemanOCSP(String alias) throws IOException {
        CertificateDTO certFromBase = getCertificate(alias);
        X509Certificate toCheck = (X509Certificate) findFromFile(alias, certFromBase.isCa());
        OCSPReq request = Revocation.generateOCSPRequest(toCheck, new BigInteger(alias));
        return handleOCSP(request, alias);
    }
}
