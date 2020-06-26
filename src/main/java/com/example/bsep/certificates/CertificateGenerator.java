package com.example.bsep.certificates;

import com.example.bsep.data.IssuerData;
import com.example.bsep.data.SubjectData;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.net.ssl.X509ExtendedKeyManager;
import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class CertificateGenerator {

    public CertificateGenerator() {}

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData,ArrayList<Integer> keyUsage,ArrayList<String> extendedKeyUsage,boolean isCa,X509Certificate parentCert) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());

            certGen.addExtension(Extension.basicConstraints,true,new BasicConstraints(isCa));

            if(parentCert != null)
            {
                int ret = getParams(keyUsage,parentCert.getKeyUsage());
                if(ret != 0) {
                    X509KeyUsage ku = new X509KeyUsage(ret);
                    certGen.addExtension(Extension.keyUsage, true, ku.toASN1Primitive());
                }

                addExtendedKeyUsage(certGen,extendedKeyUsage,parentCert.getExtendedKeyUsage());
            }else{
                int ret = getParamsSelfSigned(keyUsage);
                if(ret != 0) {
                    X509KeyUsage ku = new X509KeyUsage(ret);
                    certGen.addExtension(Extension.keyUsage, true, ku.toASN1Primitive());
                }

                addExtendedKeyUsageSelfSigned(certGen,extendedKeyUsage);
            }

            X509CertificateHolder certHolder = certGen.build(contentSigner);

            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            return certConverter.getCertificate(certHolder);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (CertIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getParams(ArrayList<Integer> keyUsage,boolean[] parentKeyUsage) {
        int ret = 0;
        for(Integer i : keyUsage){

            if(i == 128 && parentKeyUsage[0])
                ret = ret | i;
            if(i == 64 && parentKeyUsage[1])
                ret = ret | i;
            if(i == 32 && parentKeyUsage[2])
                ret = ret | i;
            if(i == 16 && parentKeyUsage[3])
                ret = ret | i;
            if(i == 8 && parentKeyUsage[4])
                ret = ret | i;
            if(i == 4 && parentKeyUsage[5])
                ret = ret | i;
            if(i == 2 && parentKeyUsage[6])
                ret = ret | i;
            if(i == 1 && parentKeyUsage[7])
                ret = ret | i;
            if(i == 32768 && parentKeyUsage[8])
                ret = ret | i;

        }
        return ret;
    }

    private int getParamsSelfSigned(ArrayList<Integer> keyUsage) {
        int ret = 0;
        for(Integer i : keyUsage){
            ret = ret | i;
        }
        return ret;
    }

    private void addExtendedKeyUsage(X509v3CertificateBuilder certGen, ArrayList<String> extendedKeyUsage, List<String> parenExtendedKeyUsage) throws CertIOException {

        ArrayList<KeyPurposeId> ids = new ArrayList<KeyPurposeId>();

        if(extendedKeyUsage.contains("tlsServer")){
            KeyPurposeId kp = KeyPurposeId.id_kp_serverAuth;
            if(parenExtendedKeyUsage.contains(kp.toOID().getId()))
                ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_serverAuth));
        }

        if(extendedKeyUsage.contains("tlsClient")){
            KeyPurposeId kp = KeyPurposeId.id_kp_clientAuth;
            if(parenExtendedKeyUsage.contains(kp.toOID().getId()))
                ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_clientAuth));
        }

        if(extendedKeyUsage.contains("codeSigning")){
            KeyPurposeId kp = KeyPurposeId.id_kp_codeSigning;
            if(parenExtendedKeyUsage.contains(kp.toOID().getId()))
                ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_codeSigning));
        }

        if(extendedKeyUsage.contains("emailProtection")){
            KeyPurposeId kp = KeyPurposeId.id_kp_emailProtection;
            if(parenExtendedKeyUsage.contains(kp.toOID().getId()))
                ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_emailProtection));
        }

        if(extendedKeyUsage.contains("timestamping")){
            KeyPurposeId kp = KeyPurposeId.id_kp_timeStamping;
            if(parenExtendedKeyUsage.contains(kp.toOID().getId()))
                ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_timeStamping));
        }

        if(extendedKeyUsage.contains("ocsp")){
            KeyPurposeId kp = KeyPurposeId.id_kp_OCSPSigning;
            if(parenExtendedKeyUsage.contains(kp.toOID().getId()))
                ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_OCSPSigning));
        }

        if(extendedKeyUsage.contains("ipsec")){
            KeyPurposeId kp = KeyPurposeId.id_kp_ipsecUser;
            if(parenExtendedKeyUsage.contains(kp.toOID().getId()))
                ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_ipsecUser));
        }

        KeyPurposeId[] arr = new KeyPurposeId[ids.size()];
        ids.toArray(arr);

        certGen.addExtension(Extension.extendedKeyUsage,true,new ExtendedKeyUsage(arr));
    }

    private void addExtendedKeyUsageSelfSigned(X509v3CertificateBuilder certGen,ArrayList<String> extendedKeyUsage) throws CertIOException {

        ArrayList<KeyPurposeId> ids = new ArrayList<KeyPurposeId>();

        if(extendedKeyUsage.contains("tlsServer"))
            ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_serverAuth));

        if(extendedKeyUsage.contains("tlsClient"))
            ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_clientAuth));

        if(extendedKeyUsage.contains("codeSigning"))
            ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_codeSigning));

        if(extendedKeyUsage.contains("emailProtection"))
            ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_emailProtection));

        if(extendedKeyUsage.contains("timestamping"))
            ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_timeStamping));

        if(extendedKeyUsage.contains("ocsp"))
            ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_OCSPSigning));

        if(extendedKeyUsage.contains("ipsec"))
            ids.add(KeyPurposeId.getInstance(KeyPurposeId.id_kp_ipsecUser));


        KeyPurposeId[] arr = new KeyPurposeId[ids.size()];
        ids.toArray(arr);

        certGen.addExtension(Extension.extendedKeyUsage,true,new ExtendedKeyUsage(arr));
    }
}
