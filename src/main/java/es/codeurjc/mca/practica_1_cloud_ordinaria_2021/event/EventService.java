package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event;

import java.util.Collection;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.image.ImageService;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.UserService;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ModelMapper modelMapper;

    public Collection<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event getEvent(Long id) {
        return checkEventExistAndGet(id);
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
        imageService.deleteImage(event.getImage());
    }

    public boolean belongsToMe(Event event) {
        return userService.getIfIAdmin() || event.getCreator().getId() == userService.getMe().getId();
    }

    public Event createEvent(EventDto eventDto) {

        Event event = modelMapper.map(eventDto, Event.class);

        if (eventDto.getMultiparImage() != null) {
            String image = imageService.createImage(eventDto.getMultiparImage());
            event.setImage(image);
        }

        event.setCurrent_capacity(0);
        event.setCreator(userService.getMe());

        return eventRepository.save(event);
    }
    
    @Transactional
    public Event bookTicket(Long eventId) {

        // try {
        //     Thread.sleep(2000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        Event event = checkEventExistAndGet(eventId);

        boolean transactional = true;

        if(transactional){

            // TRANSACTIONAL

            int i = eventRepository.incrementCurrentCapacity(eventId);
            if (i == 1) {
                return event;
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The seating capacity of the event is full");
            }
        }else{

            // NOT TRANSACTIONAL

            if (event.getCurrent_capacity() < event.getMax_capacity()) {
                event.setCurrent_capacity(event.getCurrent_capacity() + 1);
                eventRepository.saveAndFlush(event);
                return event;
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The seating capacity of the event is full");
            }
        }
    }

    public void releaseTicket(Long eventId) {
        Event event = checkEventExistAndGet(eventId);
        event.setCurrent_capacity(event.getCurrent_capacity() - 1);
        eventRepository.save(event);
    }

    public Event updateEvent(Event event){
        Event oldEvent = checkEventExistAndGet(event.getId());
        if(event.getMax_capacity() < oldEvent.getMax_capacity()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't reduce max capacity of the event");
        }

        // Update certain fields
        oldEvent.setName(event.getName());
        oldEvent.setDescription(event.getDescription());
        oldEvent.setDate(event.getDate());
        oldEvent.setPrice(event.getPrice());

        return this.eventRepository.save(event);
    }

    private Event checkEventExistAndGet(long eventId){
        Optional<Event> opEvent = eventRepository.findById(eventId);
        if (!opEvent.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event not longer exist");
        }
        return opEvent.get();
    }
    
}
