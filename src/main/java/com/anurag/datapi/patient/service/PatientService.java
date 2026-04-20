package com.anurag.datapi.patient.service;

import com.anurag.datapi.enums.BloodGroup;
import com.anurag.datapi.enums.GenoType;
import com.anurag.datapi.patient.dto.PatientDTO;
import com.anurag.datapi.response.Response;

import java.util.List;

public interface PatientService {

    Response<PatientDTO> getPatientProfile();

    Response<?> updatePatientProfile(PatientDTO patientDTO);

    Response<PatientDTO> getPatientById(Long patientId);

    Response<List<BloodGroup>> getAllBloodGroupEnums();

    Response<List<GenoType>> getAllGenotypeEnums();

}
