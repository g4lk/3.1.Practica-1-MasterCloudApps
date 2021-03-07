package es.codeurjc.mca.practica_1_cloud_ordinaria_2021;


import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event.Event;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.rest.ControllerRestTest;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.User;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.UserRepository;

@DisplayName("REST tests - Concurrent test")
public class ConcurrentTest extends ControllerRestTest{

    @Test
    @DisplayName("Create concurrent tickets")
    public void createConcurrentTickets() throws Exception {

        createFakeUsers();

        Event eventBefore = createSampleEvent();

        AtomicInteger statusCreated = new AtomicInteger();
        AtomicInteger statusConflict = new AtomicInteger();

        IntStream.range(0,100).parallel().forEach(i -> {

            int status =
                given()
                    .auth()
                        .basic("user_"+i, "pass")
                .when()
                    .post("/api/tickets/?eventId={eventId}", eventBefore.getId())
                .then()
                    .extract().statusCode();
            
            if(status == HttpStatus.SC_CREATED)
                statusCreated.incrementAndGet();
            else
                statusConflict.incrementAndGet();

        });

        Event eventAfter =
            when()
                .get("/api/events/{id}", eventBefore.getId())
            .then()
                .extract().as(Event.class);


        assertEquals(eventAfter.getMax_capacity(), eventAfter.getCurrent_capacity());
        assertEquals(50, statusCreated.get());
        assertEquals(50, statusConflict.get());
        
    }

    @Autowired
    private UserRepository userRepository;

    private void createFakeUsers(){
        IntStream.range(0,100).forEach(i -> {
            User u = new User("user_"+i, "user_"+i+"@urjc.es", "pass", User.ROLE_CUSTOMER);
            userRepository.save(u);
        });
    }
    
}
