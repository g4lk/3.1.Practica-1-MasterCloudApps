package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event.Event;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.ticket.Ticket;

@DisplayName("REST tests - Ticket Controller")
public class TicketControllerRestTest extends ControllerRestTest{
    
    @Test
    @DisplayName("Create new ticket as Customer")
    public void createTicket() throws Exception {

        // GET FIRST EVENT
        
        Event event = createSampleEvent();

        int eventCurrentCapacity = event.getCurrent_capacity();

        // CREATE A TICKET FOR EVENT

        given()
            .auth()
                .basic("Nico", "pass")
        .when()
            .post("/api/tickets/?eventId={eventId}", event.getId())
        .then()
            .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("event.id", equalTo(event.getId().intValue()));
        
        // CHECK THAT EVENT CURRENT CAPACITY WAS REDUCED

        when()
            .get("/api/events/{id}", event.getId())
        .then()
            .assertThat()
                .body("current_capacity", greaterThan(eventCurrentCapacity));
    }

    @Test
    @DisplayName("Delete ticket as Customer")
    public void deleteTicket() throws Exception {

        // GET FIRST EVENT

        Event event = createSampleEvent();

        // CREATE A TICKET FOR EVENT

        Ticket ticket =
            given()
                .auth()
                    .basic("Nico", "pass")
            .when()
                .post("/api/tickets/?eventId={eventId}", event.getId())
            .then()
                .extract().as(Ticket.class);

        int eventCurrentCapacity = ticket.getEvent().getCurrent_capacity();
        
        // DELETE TICKET

        given()
            .auth()
                .basic("Nico", "pass")
        .when()
            .delete("/api/tickets/{id}", ticket.getId())
        .then()
            .assertThat()
                .statusCode(HttpStatus.SC_OK);

        // CHECK THAT EVENT CURRENT CAPACITY WAS REDUCED

        when()
            .get("/api/events/{id}", event.getId())
        .then()
            .assertThat()
                .body("current_capacity", lessThanOrEqualTo(eventCurrentCapacity));

    }
}
