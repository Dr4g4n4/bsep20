package com.example.bsep.controller;

import com.example.bsep.dto.CertificateDTO;
import com.example.bsep.model.Certificate;
import com.example.bsep.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

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

    @RequestMapping(method= RequestMethod.POST, consumes="application/json", value = "/createSelf")
    public void createCACertificate(@RequestBody Certificate certificate){
        certificateService.createSelfSignedCertificate(certificate);
    }

    @RequestMapping(method= RequestMethod.POST, consumes="application/json", value = "/createNoSelf")
    public void createNoCACertificate(@RequestBody Certificate certificate){
        certificateService.createNonSelfSignedCertificate(certificate);
    }

}
