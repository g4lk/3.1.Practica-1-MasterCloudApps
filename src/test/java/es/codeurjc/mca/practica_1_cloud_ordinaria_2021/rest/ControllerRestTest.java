package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.rest;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import static io.restassured.RestAssured.given;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event.Event;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public abstract class ControllerRestTest {

    @LocalServerPort
    int port;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    protected void setUp() {
        RestAssured.port = port;
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:" + port;
    }

    protected Event createSampleEvent(){
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
                    .multiPart("max_capacity", 50)
                    .multiPart("multiparImage", new File("files/example.png"))
            .when()
                .post("/api/events/")
            .then()
                .assertThat().statusCode(HttpStatus.SC_CREATED)
            .extract().as(Event.class);
        return createdEvent;
    }
    
}
