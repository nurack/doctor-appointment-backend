package com.anurag.datapi.patient.repo;

import com.anurag.datapi.patient.entity.Patient;
import com.anurag.datapi.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser(User user);
}
