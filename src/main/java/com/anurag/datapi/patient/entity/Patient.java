package com.anurag.datapi.patient.entity;

import com.anurag.datapi.appointment.entity.Appointment;
import com.anurag.datapi.enums.BloodGroup;
import com.anurag.datapi.enums.GenoType;
import com.anurag.datapi.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String phone;

    private LocalDateTime dateOfBirth;

    @Lob
    private String knownAllergies;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    private GenoType genoType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

}
