package com.anurag.datapi.users.service;

import com.anurag.datapi.exceptions.BadRequestException;
import com.anurag.datapi.exceptions.NotFoundException;
import com.anurag.datapi.notification.dto.NotificationDTO;
import com.anurag.datapi.notification.service.NotificationService;
import com.anurag.datapi.response.Response;
import com.anurag.datapi.users.dto.UpdatePasswordRequest;
import com.anurag.datapi.users.dto.UserDTO;
import com.anurag.datapi.users.entity.User;
import com.anurag.datapi.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    private final String uploadDir = "uploads/profile-pictures/";

    @Override
    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            throw new NotFoundException("User is not authenticated");
        }

        String email = authentication.getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

    }

    @Override
    public Response<UserDTO> getMyUserDetails() {

        User user = getCurrentUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User DTO retireved successfully")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<UserDTO> getUserById(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(200)
                .message("User details retrieved successfully.")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<List<UserDTO>> getAllUsers() {

        List<UserDTO> userDTOS = userRepo.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();

        return Response.<List<UserDTO>>builder()
                .statusCode(200)
                .message("All users retrieved successfully.")
                .data(userDTOS)
                .build();

    }

    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User user = getCurrentUser();

        String newPassword = updatePasswordRequest.getNewPassword();
        String oldPassword = updatePasswordRequest.getOldPassword();


        if (oldPassword == null || newPassword == null) {
            throw new BadRequestException("Old and New Password Required");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old Password not Correct");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Send password change confirmation email.
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Your Password Was Successfully Changed")
                .templateName("password-change")
                .templateVariable(Map.of(
                        "name", user.getName()
                ))
                .build();
        notificationService.sendMail(notificationDTO, user);

        return Response.builder()
                .statusCode(200)
                .message("Password Changed Successfully")
                .build();

    }

    @Override
    public Response<?> uploadProfilePicture(MultipartFile file) {
        return null;
    }

    @Override
    public Response<?> uploadProfilePictureToS3(MultipartFile file) {
        return null;
    }
}
