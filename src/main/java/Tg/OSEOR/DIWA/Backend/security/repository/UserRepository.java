package Tg.OSEOR.DIWA.Backend.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tg.OSEOR.DIWA.Backend.security.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
	
	@org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.username = :id OR u.email = :id")
	Optional<User> findByIdentifier(@org.springframework.data.repository.query.Param("id") String identifier);

	@org.springframework.data.jpa.repository.Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r WHERE r.name = :name")
	java.util.List<User> findByRolesName(@org.springframework.data.repository.query.Param("name") Tg.OSEOR.DIWA.Backend.security.enums.ERole name);
}
