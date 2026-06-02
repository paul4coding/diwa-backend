package Tg.OSEOR.DIWA.Backend.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Tg.OSEOR.DIWA.Backend.security.UserDetailsImpl;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	UserRepository userRepository;

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CustomUserDetailsService.class);

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
		logger.info("Tentative de chargement de l'utilisateur avec l'identifiant: [{}]", identifier);
		User user = userRepository.findByIdentifier(identifier)
				.orElseThrow(() -> {
					logger.warn("Utilisateur non trouvé avec l'identifiant: [{}]", identifier);
					return new UsernameNotFoundException("Utilisateur non trouvé avec: " + identifier);
				});

		logger.info("Utilisateur trouvé: [{}], rôles: {}", user.getUsername(), user.getRoles());
		return UserDetailsImpl.build(user);
	}
}
