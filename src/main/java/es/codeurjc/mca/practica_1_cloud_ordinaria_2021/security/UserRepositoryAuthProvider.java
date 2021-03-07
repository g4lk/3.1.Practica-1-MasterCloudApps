package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.User;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.UserComponent;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.UserRepository;

/**
 * This class is used to check http credentials against database data. Also it
 * is responsible to set database user info into userComponent, a session scoped
 * bean that holds session user information.
 * 
 * NOTE: This class is not intended to be modified by app developer.
 */
@Component
public class UserRepositoryAuthProvider implements AuthenticationProvider {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserComponent userComponent;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getName();
		String password = (String) authentication.getCredentials();

		Optional<User> op = userRepository.findByName(username);

		if (!op.isPresent()) {
			throw new BadCredentialsException("User not found");
		}
		
		User user = op.get();

		if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {

			throw new BadCredentialsException("Wrong password");
		} else {

			userComponent.setLoggedUser(user);

			List<GrantedAuthority> roles = new ArrayList<>();
			for (String role : user.getRoles()) {
				roles.add(new SimpleGrantedAuthority(role));
			}

			return new UsernamePasswordAuthenticationToken(username, password, roles);
		}
	}

	@Override
	public boolean supports(Class<?> authenticationObject) {
		return true;
	}
}
