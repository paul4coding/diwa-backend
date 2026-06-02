package Tg.OSEOR.DIWA.Backend.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tg.OSEOR.DIWA.Backend.security.enums.ERole;
import Tg.OSEOR.DIWA.Backend.security.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	Optional<Role> findByName(ERole name);
	boolean existsByName(ERole name);
}
