package com.example.bsep.repository;

import com.example.bsep.model.Certificate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Certificate findOneByIdSubject(Long idSubject);

    List<Certificate> findAll();

    List<Certificate> findAllByNonCa();

    List<Certificate> findAllByCa();

    List<Certificate>findAllByRevoked();

    List<Certificate>findAllByNonRevoked();

    @SuppressWarnings("unchecked")
    Certificate save(Certificate certificate);



}
