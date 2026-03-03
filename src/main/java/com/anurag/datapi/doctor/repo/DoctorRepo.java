package com.anurag.datapi.doctor.repo;

import com.anurag.datapi.doctor.entity.Doctor;
import com.anurag.datapi.enums.Specialization;
import com.anurag.datapi.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepo extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUser(User user);

    List<Doctor> findBySpecialization(Specialization specialization);

}
