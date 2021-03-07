package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user;

import java.util.Collection;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event.Event;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.ticket.Ticket;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/")
	@JsonView(User.BasicAtt.class)
	public Collection<User> getAll() {
		return userService.findAll();
	}

	@PostMapping("/")
	@JsonView(User.BasicAtt.class)
	public ResponseEntity<User> createUser(@RequestParam(name = "type") String type, @RequestBody User user) {

		if (type == null || (!"Organizer".equals(type) && !"Customer".equals(type))) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		String role = type.equals("Customer") ? User.ROLE_CUSTOMER : User.ROLE_ORGANIZER;

		Optional<User> createdUser = userService.createUser(user, role);

		if (createdUser.isPresent()) {
			return new ResponseEntity<>(createdUser.get(), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable long id) {
		if (userService.deleteUser(id)) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public interface CustomerView extends User.TicketsAtt, Ticket.BasicAtt, Event.BasicAtt {}

	@GetMapping("/customers/me")
	@JsonView(CustomerView.class)
	public ResponseEntity<User> getMeCustomer() {
		return findUser(userService.getMe().getId());
	}

	@GetMapping("/customers/{id}")
	@JsonView(CustomerView.class)
	public ResponseEntity<User> getOneCustomer(@PathVariable long id) {
		return findUser(id);
	}

	public interface OrganizerView extends User.EventsAtt, Event.BasicAtt {}

	@GetMapping("/organizers/me")
	@JsonView(OrganizerView.class)
	public ResponseEntity<User> getMeOrganizer() {
		return findUser(userService.getMe().getId());
	}

	@GetMapping("/organizers/{id}")
	@JsonView(OrganizerView.class)
	public ResponseEntity<User> getOneOrganizer(@PathVariable long id) {
		return findUser(id);
	}

	private ResponseEntity<User> findUser(Long id){
		Optional<User> op = userService.getUser(id);
		if (op.isPresent()) {
			return new ResponseEntity<>(op.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
