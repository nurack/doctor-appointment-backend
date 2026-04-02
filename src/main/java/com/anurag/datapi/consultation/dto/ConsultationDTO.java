package com.anurag.datapi.consultation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ConsultationDTO {

    private Long id;

    private Long appointmentId;

    private LocalDateTime consultationDate;

    private String subjectiveNotes;

    private String objectiveFinding;

    private String assessment;

    private String plan;
}
