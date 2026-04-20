package com.anurag.datapi.users.service;

import com.anurag.datapi.doctor.entity.Doctor;
import com.anurag.datapi.doctor.repo.DoctorRepo;
import com.anurag.datapi.exceptions.BadRequestException;
import com.anurag.datapi.exceptions.NotFoundException;
import com.anurag.datapi.notification.dto.NotificationDTO;
import com.anurag.datapi.notification.service.NotificationService;
import com.anurag.datapi.patient.entity.Patient;
import com.anurag.datapi.patient.repo.PatientRepo;
import com.anurag.datapi.response.Response;
import com.anurag.datapi.role.entity.Role;
import com.anurag.datapi.role.repo.RoleRepo;
import com.anurag.datapi.security.JwtService;
import com.anurag.datapi.users.dto.LoginRequest;
import com.anurag.datapi.users.dto.LoginResponse;
import com.anurag.datapi.users.dto.RegistrationRequest;
import com.anurag.datapi.users.dto.ResetPasswordRequest;
import com.anurag.datapi.users.entity.PasswordResetCode;
import com.anurag.datapi.users.entity.User;
import com.anurag.datapi.users.repo.PasswordResetRepo;
import com.anurag.datapi.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationService notificationService;
    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;
    private final PasswordResetRepo passwordResetRepo;
    private final CodeGenerator codeGenerator;

    @Override
    public Response<String> register(RegistrationRequest registrationRequest) {

        //check if the user already exists
        if(userRepo.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new BadRequestException("User with email already exists");
        }

        //get the roles to assign for the new user to register
        List<String> requestedRole = (registrationRequest.getRoles() != null && !registrationRequest.getRoles().isEmpty())
                ? registrationRequest.getRoles().stream().map(String::toUpperCase).collect(Collectors.toList()) :
                List.of("PATIENT"); //defualt patient role

        boolean isDoctor = requestedRole.contains("DOCTOR");

        //if doctor license number is needed
        if(isDoctor && (registrationRequest.getLicenseNumber() == null || registrationRequest.getLicenseNumber().isBlank())){
            throw new BadRequestException("License number is required for Doctor registration");
        }

        List<Role> roles = requestedRole.stream()
                .map(roleRepo::findByName)
                .flatMap(Optional::stream)
                .toList();

        if(roles.isEmpty()) {
            throw new NotFoundException("Registration failed as requested roles were not found in the database.");
        }

        User newUser = User.builder()
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .name(registrationRequest.getName())
                .roles(roles)
                .build();

        User savedUser = userRepo.save(newUser);
        log.info("New user registered: {} with {} roles", savedUser.getName(), savedUser.getRoles());

        //profile of the new user

        for (Role role : roles) {
            String roleName = role.getName();

            switch (roleName) {
                case "PATIENT" :
                    createPatientProfile(savedUser);
                    log.info("Patient profile created : {}", savedUser.getEmail());
                    break;
                case "DOCTOR" :
                    createDoctorProfile(registrationRequest, savedUser);
                    log.info("Doctor profile created : {}", savedUser.getEmail());
                    break;
                case "ADMIN":
                    log.info("Admin role assigned to the user : {}", savedUser.getEmail());
                    break;
                default:
                    log.info("Unknown role");
                    break;
            }
        }

        sendRegistrationEmail(registrationRequest, savedUser);

        return Response.<String>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Registration is successful, a welcome email has been sent")
                .data(savedUser.getEmail())
                .build();
    }

    private void sendRegistrationEmail(RegistrationRequest registrationRequest, User savedUser) {
        NotificationDTO welcomeEmail = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Welcome to AHS Health!")
                .templateName("welcome")
                .message("Thanks for registering, your account is ready!")
                .templateVariable(Map.of(
                        "name", registrationRequest.getName(),
                        "loginLink", "test.com"
                ))
                .build();

        notificationService.sendMail(welcomeEmail, savedUser);
    }

    private void createDoctorProfile(RegistrationRequest registrationRequest, User savedUser) {

        Doctor doctor = Doctor.builder()
                .specialization(registrationRequest.getSpecialization())
                .licenseNumber(registrationRequest.getLicenseNumber())
                .user(savedUser)
                .build();

        doctorRepo.save(doctor);
        log.info("Doctor profile created.");

    }

    private void createPatientProfile(User savedUser) {

        Patient patient = Patient.builder()
                .user(savedUser)
                .build();
        patientRepo.save(patient);

        log.info("Patient profile is created {}", savedUser.getEmail());
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Email Not found"));
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Password doesn't Match");
        }

        String token = jwtService.generateToken(email);

        LoginResponse loginResponse = LoginResponse.builder()
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .token(token)
                .build();

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login Successful")
                .data(loginResponse)
                .build();
    }

    @Override
    public Response<?> forgetPassword(String email) {

        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("User Not found"));
        passwordResetRepo.deleteByUserId(user.getId());

        String code = codeGenerator.generateUniqueCode();

        PasswordResetCode passwordResetCode = PasswordResetCode.builder()
                .user(user)
                .code(code)
                .expiryDate(calculateExpiryDate())
                .used(false)
                .build();

        passwordResetRepo.save(passwordResetCode);

        NotificationDTO passResetEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password reset code")
                .templateName("password-reset")
                .templateVariable(Map.of(
                        "name", user.getName(),
                        "resetLink", "test.com"
                ))
                .build();

        notificationService.sendMail(passResetEmail, user);

        return Response.builder()
                .statusCode(200)
                .message("Password reset code sent to email")
                .build();

    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusHours(1);
    }

    @Override
    public Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {

        String code = resetPasswordRequest.getCode();
        String newPassword = resetPasswordRequest.getNewPassword();

        log.info("Code is " + code);
        log.info("New pass is " + newPassword);

        PasswordResetCode resetCode = passwordResetRepo.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid reset code"));

        if(resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetRepo.delete(resetCode);
            throw new BadRequestException("Reset code has expried");
        }

        User user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        passwordResetRepo.delete(resetCode);

        NotificationDTO passwordResetEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password updated successfully")
                .templateName("password-update-confirmation")
                .templateVariable(Map.of(
                        "name", user.getName()
                ))
                .build();

        notificationService.sendMail(passwordResetEmail, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password updated successfully")
                .build();

    }
}
