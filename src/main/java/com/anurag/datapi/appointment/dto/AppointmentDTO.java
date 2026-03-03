package com.anurag.datapi.appointment.dto;

import com.anurag.datapi.doctor.dto.DoctorDTO;
import com.anurag.datapi.enums.AppointmentStatus;
import com.anurag.datapi.patient.dto.PatientDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentDTO {

    private Long id;

    @NotNull(message = "Doctor id is required for booking")
    private Long doctorId;

    private String purposeOfCancellation;

    private String initialSymptoms;

    @NotNull(message = "Start time is required for the appointment")
    @Future(message = "Appointment must be scheduled for future date and time")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String meetingLink;

    private AppointmentStatus status;

    private DoctorDTO doctor;

    private PatientDTO patient;


}
