package com.anurag.datapi.users.service;

import com.anurag.datapi.response.Response;
import com.anurag.datapi.users.dto.UpdatePasswordRequest;
import com.anurag.datapi.users.dto.UserDTO;
import com.anurag.datapi.users.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User getCurrentUser();

    Response<UserDTO> getMyUserDetails();

    Response<UserDTO> getUserById(Long userId);

    Response<List<UserDTO>> getAllUsers();

    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);

    Response<?> uploadProfilePicture(MultipartFile file);

    Response<?> uploadProfilePictureToS3(MultipartFile file);
}
