package Tg.OSEOR.DIWA.Backend.security.service;

import Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTOResponse;
import Tg.OSEOR.DIWA.Backend.security.dto.JwtAuthenticationResponse;
import Tg.OSEOR.DIWA.Backend.security.dto.LoginRequest;
import Tg.OSEOR.DIWA.Backend.security.dto.LoginStep1Response;
import Tg.OSEOR.DIWA.Backend.security.dto.LoginStep2Request;
import Tg.OSEOR.DIWA.Backend.security.dto.SignUpRequest;
import Tg.OSEOR.DIWA.Backend.security.model.User;

public interface AuthService {
    JwtAuthenticationResponse login(LoginRequest loginRequest);
    UserDTOResponse register(SignUpRequest signUpRequest);

    LoginStep1Response loginStep1(LoginRequest loginRequest);
    JwtAuthenticationResponse loginStep2(LoginStep2Request loginRequest);
    void confirmAccount(String token);
    void resetPassword(String email, String newPassword, String confirmPassword);
    void resetPasswordWithOtp(String email, String otpCode, String newPassword, String confirmPassword);
    void forgotPassword(String email);
    void changePasswordFirstLogin(String email, String newPassword, String confirmPassword);
    User getCurrentUser();
}
