package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;

import java.io.File;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event.Event;

@DisplayName("REST tests - Event Controller")
public class EventControllerRestTest extends ControllerRestTest{

    @Test
    @DisplayName("Create new event as Organizer")
    public void createEventTest() throws Exception {

        // CREATE NEW EVENT

        Event createdEvent = 
            given()
                .auth()
                    .basic("Patxi", "pass")
                .request()
                    .contentType("multipart/form-data")
                    .multiPart("name","Obra de teatro")
                    .multiPart("description", "Obra ofrecido por ...")
                    .multiPart("date", "2020-11-22T19:00:00+0000")
                    .multiPart("price", 19.99)
                    .multiPart("max_capacity", 5)
                    .multiPart("multiparImage", new File("files/example.png"))
            .when()
                .post("/api/events/")
            .then()
                .assertThat().statusCode(HttpStatus.SC_CREATED)
                .body("name", equalTo("Obra de teatro"))
            .extract().as(Event.class);


        // CHECK THAT EVENT PERSIST
        given()
            .auth()
                .basic("Patxi", "pass")
        .when()
            .get("/api/events/"+ createdEvent.getId())
        .then()
            .assertThat().statusCode(HttpStatus.SC_OK)
            .body("name", equalTo("Obra de teatro"))
            .body("image", matchesRegex(".*/image_([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})_example.png"))
        ;

    }

   
    
}
