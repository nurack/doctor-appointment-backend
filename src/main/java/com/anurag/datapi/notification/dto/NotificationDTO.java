package com.anurag.datapi.notification.dto;

import com.anurag.datapi.enums.NotificationType;
import com.anurag.datapi.users.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {

    private Long id;

    private String subject;

    @NotBlank(message = "recipient is required")
    private String recipient;

    private String message;

    private NotificationType type;

    private LocalDateTime createdAt;

    private Map<String, Object> templateVariable;
}
