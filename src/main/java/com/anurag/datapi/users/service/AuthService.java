package com.anurag.datapi.users.service;

import com.anurag.datapi.response.Response;
import com.anurag.datapi.users.dto.LoginRequest;
import com.anurag.datapi.users.dto.LoginResponse;
import com.anurag.datapi.users.dto.RegistrationRequest;
import com.anurag.datapi.users.dto.ResetPasswordRequest;

public interface AuthService {

    Response<String> register(RegistrationRequest registrationRequest);

    Response<LoginResponse> login(LoginRequest loginRequest);

    Response<?> forgetPassword(String email);

    Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);

}
