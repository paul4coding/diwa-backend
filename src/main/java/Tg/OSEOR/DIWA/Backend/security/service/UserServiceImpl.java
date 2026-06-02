package Tg.OSEOR.DIWA.Backend.security.service;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Tg.OSEOR.DIWA.Backend.mappers.UserMapper userMapper;

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));
    }

    @Override
    @Transactional
    public User updateProfile(String currentUsername, String newUsername, String newEmail, String newPassword) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Vérifier si le nouvel email est déjà pris par un autre utilisateur
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }
            user.setEmail(newEmail);
        }

        // Vérifier si le nouveau username est déjà pris
        if (newUsername != null && !newUsername.equals(user.getUsername())) {
            if (userRepository.existsByUsername(newUsername)) {
                throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
            }
            user.setUsername(newUsername);
        }

        // Mettre à jour le mot de passe si fourni
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public java.util.List<Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTOResponse> getUsersByRole(String roleName) {
        Tg.OSEOR.DIWA.Backend.security.enums.ERole eRole = Tg.OSEOR.DIWA.Backend.security.enums.ERole.valueOf(roleName);
        return userRepository.findByRolesName(eRole).stream()
                .map(userMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }
}
