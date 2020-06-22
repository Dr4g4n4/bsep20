package com.example.bsep.service;


import com.example.bsep.keystores.KeyStoreReader;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class Revocation {

    private KeyStoreReader keyStoreReader;

    public int getCodeFormString(String revocationReason) {
        int reason = 0;

        switch (revocationReason) {
            case "aACompromise":
                reason = CRLReason.aACompromise;
                break;
            case "affiliationChanged":
                reason = CRLReason.affiliationChanged;
                break;
            case "cACompromise":
                reason = CRLReason.cACompromise;
                break;
            case "certificateHold":
                reason = CRLReason.certificateHold;
                break;
            case "cessationOfOperation":
                reason = CRLReason.cessationOfOperation;
                break;
            case "keyCompromise":
                reason = CRLReason.keyCompromise;
                break;
            case "privilegeWithdrawn":
                reason = CRLReason.privilegeWithdrawn;
                break;
            case "removeFromCRL":
                reason = CRLReason.removeFromCRL;
                break;
            default:
                reason = CRLReason.unspecified;
        }

        return reason;
    }

    public BasicOCSPRespBuilder initOCSPRespBuilder(OCSPReq request, PublicKey publicKey) {
        SubjectPublicKeyInfo keyinfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        BasicOCSPRespBuilder respBuilder;
        try {
            respBuilder = new BasicOCSPRespBuilder(keyinfo,
                    new JcaDigestCalculatorProviderBuilder().setProvider("BC").build().get(CertificateID.HASH_SHA1)); // Create builder
        } catch (Exception e) {
            return null;
        }

        Extension ext = request.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
        if (ext != null) {
            respBuilder.setResponseExtensions(new Extensions(new Extension[] { ext })); // Put the nonce back in the response
        }
        return respBuilder;
    }

    public static OCSPReq generateOCSPRequest(X509Certificate genCert, BigInteger serialNumber)  {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        OCSPReqBuilder gen = new OCSPReqBuilder();
        JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();
        DigestCalculatorProvider digestCalculatorProvider = null;
        try {
            digestCalculatorProvider = digestCalculatorProviderBuilder.build();
            DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);
            CertificateID id = new CertificateID(digestCalculator, new JcaX509CertificateHolder(genCert), serialNumber);
            gen.addRequest(id);
            BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
            Extension ext = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, true, new DEROctetString(nonce.toByteArray()));
            gen.setRequestExtensions(new Extensions(new Extension[]{ext}));
            return gen.build();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (OCSPException e) {
            e.printStackTrace();
        }

        return null;
    }

    public OCSPResp generateOCSPResponse(BasicOCSPRespBuilder respBuilder, Certificate signingCert) {
        //String keyStoreFile = ((X509Certificate)signingCert). isCA iz ekstenzije izvuci
        PrivateKey privateKey = keyStoreReader.readPrivateKey("ks/ksCA.jks", "sifra1",
                ((X509Certificate)signingCert).getSerialNumber().toString(), "sifra1");
        try {
            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(privateKey);
            BasicOCSPResp basicResp = respBuilder.build(contentSigner,
                    new X509CertificateHolder[] { new X509CertificateHolder(signingCert.getEncoded()) }, new Date());
            int response = OCSPRespBuilder.SUCCESSFUL;
            return new OCSPRespBuilder().build(response, basicResp);        // salje povuceni sert u responsu
        } catch (Exception e) {
            return null;
        }
    }
}
