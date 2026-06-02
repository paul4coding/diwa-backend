package Tg.OSEOR.DIWA.Backend.security.service;

import Tg.OSEOR.DIWA.Backend.security.model.User;

public interface UserService {
    User getCurrentUser();
    User updateProfile(String currentUsername, String newUsername, String newEmail, String newPassword);
    java.util.List<Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTOResponse> getUsersByRole(String roleName);
}
