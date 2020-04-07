package com.example.bsep.controller;

import com.example.bsep.certificates.CertificateReader;
import com.example.bsep.dto.CertificateDTO;
import com.example.bsep.keystores.KeyStoreWriter;
import com.example.bsep.model.Certificate;
import com.example.bsep.service.CertificateService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.List;

@RestController
@RequestMapping(value = "/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateReader certificateReader;

    @RequestMapping(method=RequestMethod.GET, value = "/getAllCertificates")
    public ResponseEntity<List<CertificateDTO>> getAllCerts() {

        // HttpServletRequest request
        // String token = tokenUtils.getToken(request);
        // String email = tokenUtils.getUsernameFromToken(token);
        // User user = userService.findOneByEmail(email);
        // if (user != null) {
            List<CertificateDTO>retValue = certificateService.getAllCertificates();
            if (retValue.size() > 0) {
                return new ResponseEntity<>(retValue, HttpStatus.OK);
            }
       // }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    @GetMapping(value = "/getAllCaCertificates")
    public ResponseEntity<List<CertificateDTO>> getAllCaCerts() {

        // String token = tokenUtils.getToken(request);
        //  String email = tokenUtils.getUsernameFromToken(token);
        //  User user = userService.findOneByEmail(email);
        // if (user != null) {
        List<CertificateDTO>retValue = certificateService.getAllCaCertificates();
        if (retValue.size() > 0) {
            return new ResponseEntity<>(retValue, HttpStatus.OK);
        }
        // }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/getAllNonCaCertificates")
    public ResponseEntity<List<CertificateDTO>> getAllNonCaCerts() {

        // String token = tokenUtils.getToken(request);
        //  String email = tokenUtils.getUsernameFromToken(token);
        //  User user = userService.findOneByEmail(email);
        // if (user != null) {
        List<CertificateDTO>retValue = certificateService.getAllNoCaCertificates();
        if (retValue.size() > 0) {
            return new ResponseEntity<>(retValue, HttpStatus.OK);
        }
        // }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method= RequestMethod.POST, consumes="application/json", value = "/createSelf/{isCa}")
    public void  createCACertificate(@RequestBody Certificate certificate, @PathVariable boolean isCa){
        System.out.print("Kontroler da li je CA:   " + isCa);
        boolean retValue = certificateService.createSelfSignedCertificate(certificate, isCa);
// ResponseEntity<ResponseEntity>
     /*   if(retValue){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
    }

    @RequestMapping(method= RequestMethod.POST, consumes="application/json", value = "/createNoSelf/{isCa}")
    public ResponseEntity<ResponseEntity> createNoCACertificate(@RequestBody Certificate certificate, @PathVariable boolean isCa){
        boolean retValue = certificateService.createNonSelfSignedCertificate(certificate, isCa);
        retValue = true;
        if(retValue){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{serialNumber}")
    public CertificateDTO getCertificate(@PathVariable String serialNumber){
        return certificateService.getCertificate(serialNumber);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/downloadCertificate/{id}")
    public void downloadCertificate(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id){
        File certificateForDownload = certificateService.downloadCertificate(id);
        response.setContentType("application/pkix-cert");
        response.setContentLength((int) certificateForDownload.length());
        response.addHeader("Content-Disposition", "attachment; filename="+ certificateForDownload.getName());

        try {
            Files.copy(Paths.get(certificateForDownload.getPath()), response.getOutputStream() );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/validate/{serialNumber}")
    public ResponseEntity<Boolean> validateCertificate(@PathVariable String serialNumber){
        boolean ret = certificateService.isValid(serialNumber);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

}


