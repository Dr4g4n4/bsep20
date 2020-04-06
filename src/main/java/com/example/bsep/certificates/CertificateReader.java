package com.example.bsep.certificates;


import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Component
public class CertificateReader {


    public static final String BASE64_ENC_CERT_FILE = "./data/jovan2.cer";
    public static final String BIN_ENC_CERT_FILE = "./data/jovan1.cer";

    public void testIt() {
        System.out.println("Cita sertifikat iz Base64 formata");
        readFromBase64EncFile(BASE64_ENC_CERT_FILE);
        System.out.println("\n\nCita sertifikat iz binarnog formata");
        readFromBinEncFile();
    }


    public Certificate readFromBase64EncFile(String filepath) {
        try {
            FileInputStream fis = new FileInputStream(filepath);
            BufferedInputStream bis = new BufferedInputStream(fis);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            //Cita sertifikat po sertifikat
            //Svaki certifikat je izmedju
            //-----BEGIN CERTIFICATE-----,
            //i
            //-----END CERTIFICATE-----.
            if (bis.available() > 0) {
                Certificate cert = cf.generateCertificate(bis);
                return cert;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @SuppressWarnings("rawtypes")
    private void readFromBinEncFile() {
        try {
            FileInputStream fis = new FileInputStream(BIN_ENC_CERT_FILE);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            //Ovde se vade svi sertifkati
            Collection c = cf.generateCertificates(fis);
            Iterator i = c.iterator();
            while (i.hasNext()) {
                Certificate cert = (Certificate)i.next();
                System.out.println(cert);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        CertificateReader test = new CertificateReader();
        test.testIt();
    }

}
