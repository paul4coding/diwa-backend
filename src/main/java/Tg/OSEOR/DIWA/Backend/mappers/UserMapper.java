package Tg.OSEOR.DIWA.Backend.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTOResponse;
import Tg.OSEOR.DIWA.Backend.security.model.Role;
import Tg.OSEOR.DIWA.Backend.security.model.User;

@Component
public class UserMapper {

    public UserDTOResponse toResponse(User entity) {
        if (entity == null) return null;

        UserDTOResponse dto = new UserDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setPrenom(entity.getPrenom());
        dto.setNom(entity.getNom());
        
        if (entity.getRoles() != null) {
            Set<String> roleNames = entity.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
            dto.setRoles(roleNames);
        }
        
        dto.setCreateDate(entity.getCreateDate());
        
        return dto;
    }

    public User toEntity(UserDTORequest dto) {
        if (dto == null) return null;

        User entity = new User();
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        
        // Note: Les rôles doivent être gérés dans le service car ils nécessitent un accès au Repository
        
        return entity;
    }

    public void updateEntityFromRequest(UserDTORequest dto, User entity) {
        if (dto == null) return;

        if (dto.getUsername() != null) entity.setUsername(dto.getUsername());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPassword() != null) entity.setPassword(dto.getPassword());
        
        // Note: Les rôles sont généralement gérés séparément dans la mise à jour
    }
}
