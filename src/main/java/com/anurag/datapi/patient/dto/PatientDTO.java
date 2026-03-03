package com.anurag.datapi.patient.dto;

import com.anurag.datapi.enums.BloodGroup;
import com.anurag.datapi.enums.GenoType;
import com.anurag.datapi.users.dto.UserDTO;
import com.anurag.datapi.users.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class PatientDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String phone;

    private LocalDateTime dateOfBirth;

    private String knownAllergies;

    private BloodGroup bloodGroup;

    private GenoType genoType;
    private UserDTO user;
}
