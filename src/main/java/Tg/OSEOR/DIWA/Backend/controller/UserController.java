package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.service.UserService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getCurrentProfile() {
        User user = userService.getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("roles", user.getRoles());
        
        return ResponseEntity.ok(new BaseResponse<>(200, "Profil récupéré", data));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Map<String, Object>>> updateProfile(@RequestBody Map<String, String> request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        
        User updatedUser = userService.updateProfile(
                currentUsername,
                request.get("username"),
                request.get("email"),
                request.get("password")
        );

        Map<String, Object> data = new HashMap<>();
        data.put("id", updatedUser.getId());
        data.put("username", updatedUser.getUsername());
        data.put("email", updatedUser.getEmail());

        return ResponseEntity.ok(new BaseResponse<>(200, "Profil mis à jour avec succès", data));
    }

    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONNISTE')")
    public ResponseEntity<java.util.List<Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTOResponse>> getUsersByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(userService.getUsersByRole(roleName));
    }
}
