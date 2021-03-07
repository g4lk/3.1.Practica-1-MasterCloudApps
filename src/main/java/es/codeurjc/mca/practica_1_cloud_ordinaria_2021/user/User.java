package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event.Event;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.ticket.Ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This is the entity to store in database user information. It contains the
 * following basic information:
 * <ul>
 * <li>name: The name of the user. This name have to be used to logIn into the
 * service</li>
 * <li>password: The hash of the password. The password in never stored in
 * plain text to avoid information leak</li>
 * <li>roles: The roles of this user</li>
 * 
 * To check if a user can be logged into the service, this object is loaded from
 * database and password is verified. If user is authenticated, then this
 * database object is returned to the user.
 * 
 * NOTE: This class is intended to be extended by developer adding new
 * attributes. Current attributes can not be removed because they are used in
 * authentication procedures.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

	public static String ROLE_ORGANIZER = "ROLE_ORGANIZER";
	public static String ROLE_ADMIN = "ROLE_ADMIN";
	public static String ROLE_CUSTOMER = "ROLE_CUSTOMER";

	
	public interface BasicAtt {}
	public interface EventsAtt extends BasicAtt{}
	public interface TicketsAtt extends BasicAtt{}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(BasicAtt.class)
	private Long id;
	
	@JsonView(BasicAtt.class)
	private String name;
	
	@JsonView(BasicAtt.class)
	@Column(unique = true)
	private String email;

	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;

	@ElementCollection(fetch = FetchType.EAGER)
	@JsonView(BasicAtt.class)
	private List<String> roles;

	@OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
	@JsonView(EventsAtt.class)
	private List<Event> events;

	@JsonView(TicketsAtt.class)
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Ticket> tickets;

	public User(String name, String email, String password, String... roles) {
		this.name = name;
		this.email = email;
		this.password = new BCryptPasswordEncoder().encode(password);
		this.roles = new ArrayList<>(Arrays.asList(roles));
		this.events = new ArrayList<>();
	}

	@Override
	public String toString(){
		return "User [id=" + id + ", name=" + this.getName() + ", email=" + this.getEmail() + "]";
	}

}