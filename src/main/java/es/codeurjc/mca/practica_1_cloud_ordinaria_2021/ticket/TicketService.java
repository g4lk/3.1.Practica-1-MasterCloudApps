package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.ticket;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event.Event;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event.EventService;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.User;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.UserService;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

	public Ticket createTicket(Long eventId) {
        User user = userService.getMe();
        Event event = eventService.getEvent(eventId);

        // Check for remaining tickets
        eventService.bookTicket(eventId);

        Ticket ticket = new Ticket(user, event);
        ticketRepository.save(ticket);
        return ticket;
		
	}

	public Optional<Ticket> getTicket(long id) {
		return ticketRepository.findById(id);
    }
    
    public void deleteTicket(Ticket ticket) {
        eventService.releaseTicket(ticket.getEvent().getId());
        ticketRepository.delete(ticket);
    }

	public boolean belongsToMe(Ticket ticket) {
        long id = userService.getMe().getId();
        System.out.println(id);
        return userService.getIfIAdmin() || ticket.getCustomer().getId().equals(id);
	}

}
