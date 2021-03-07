package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.ticket;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event.Event;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.User;

@RestController
@RequestMapping("/api/tickets")
public class TicketRestController {

    @Autowired
    private TicketService ticketService;

    public interface AdminView extends Ticket.BasicAtt, Ticket.CustomerAtt, User.BasicAtt, Event.BasicAtt{}
    
    public interface CustomerView extends Ticket.BasicAtt, Ticket.CustomerAtt, Event.BasicAtt {}

    @GetMapping("/{id}")
    @JsonView(AdminView.class)
    public ResponseEntity<Ticket> getOne(@PathVariable long id) {
        Optional<Ticket> op = ticketService.getTicket(id);
        if (op.isPresent()) {
            return new ResponseEntity<>(op.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    @JsonView(CustomerView.class)
    public ResponseEntity<Ticket> createTicket(@RequestParam Long eventId) {

        Ticket createdTicket = ticketService.createTicket(eventId);

        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable long id) {
        Optional<Ticket> toDelete = ticketService.getTicket(id);
        if (toDelete.isPresent()) {
            Ticket ticketToDelete = toDelete.get();
            if (ticketService.belongsToMe(ticketToDelete)) {
                ticketService.deleteTicket(ticketToDelete);
                return new ResponseEntity<>("Ticket deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Only the owner can delete the ticket", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
}
