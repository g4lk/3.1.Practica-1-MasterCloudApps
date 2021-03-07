package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.User;

@RestController
@RequestMapping("/api/events")
public class EventRestController{
	
	@Autowired
	private EventService eventService;
	public interface EventView extends Event.OrganizerAtt, User.BasicAtt{}
	
	@GetMapping("/")
	@JsonView(EventView.class)
	public Collection<Event> getAll() {
		return eventService.findAll();
	}

	@GetMapping("/{id}")
	@JsonView(EventView.class)
	public ResponseEntity<Event> getOne(@PathVariable long id) {
		return new ResponseEntity<>(eventService.getEvent(id), HttpStatus.OK);
	}

	@PostMapping("/")
	@JsonView(Event.BasicAtt.class)
	public ResponseEntity<Event> createEvent(EventDto eventDto) throws Exception{	
		Event createdEvent = eventService.createEvent(eventDto);
		return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
	}

	@PutMapping("/")
	@JsonView(Event.BasicAtt.class)
	public ResponseEntity<Event> updateEvent(@RequestBody Event event) throws Exception {
		Event createdEvent = eventService.updateEvent(event);
		return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteEvent(@PathVariable long id) {

		Event eventToDelete = eventService.getEvent(id);
		if (eventService.belongsToMe(eventToDelete)) {
			eventService.deleteEvent(eventToDelete);
			return new ResponseEntity<>("Event deleted successfully", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Only the owner can delete the event", HttpStatus.FORBIDDEN);
		}

	}

}
