package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(1)
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	public UserRepositoryAuthProvider userRepoAuthProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/logIn").authenticated();
		
		// USERS

		// POST /api/users/newCustomer is PUBLIC
		// POST /api/users/newOrganizer is PUBLIC
		// GET /api/users/me is PRIVATE -> ALL ROLES
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/users/customers/me").hasAnyRole("CUSTOMER");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/users/organizers/me").hasAnyRole("ORGANIZER");
		// GET /api/users/ is PRIVATE -> ONLY ADMIN
		// GET /api/users/customers/ is PRIVATE -> ONLY ADMIN
		// GET /api/users/organizers/ is PRIVATE -> ONLY ADMIN
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN");
		// DELETE /api/users/ is PRIVATE -> ONLY ADMIN
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN");


		// EVENTS

		// GET /api/events/ is PUBLIC
		// GET /api/events/:id is PUBLIC
		// POST /api/events/ is PRIVATE -> ONLY ORGANIZER
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/events/**").hasRole("ORGANIZER");
		// DELETE /api/events/ is PRIVATE -> ONLY ORGANIZER AND ADMIN
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/events/**").hasAnyRole("ADMIN", "ORGANIZER");

		// TICKETS

		// POST /api/tickets/ is PRIVATE -> ONLY CUSTOMER
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/tickets/**").hasRole("CUSTOMER");
		// DELETE /api/tickets/ is PRIVATE -> ONLY CUSTOMER
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/tickets/**").hasAnyRole("CUSTOMER");
		
		// Other URLs can be accessed without authentication
		http.authorizeRequests().anyRequest().permitAll();

		// Disable CSRF protection
		http.csrf().disable();

		// Use Http Basic Authentication
		http.httpBasic();

		// Do not redirect when logout
		// http.logout().logoutSuccessHandler((rq, rs, a) -> {	});
		http.logout()
			.logoutUrl("/api/logOut")
			.invalidateHttpSession(true)
			.deleteCookies("JSESSIONID")
			.logoutSuccessHandler((rq, rs, a) -> {});


		// For H2
		http.headers().frameOptions().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		// Database authentication provider
		auth.authenticationProvider(userRepoAuthProvider);
	}
}