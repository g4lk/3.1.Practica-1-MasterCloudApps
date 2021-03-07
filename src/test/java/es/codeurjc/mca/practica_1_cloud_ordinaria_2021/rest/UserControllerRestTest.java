package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.User;
import io.restassured.http.ContentType;

@DisplayName("REST tests - User Controller")
public class UserControllerRestTest extends ControllerRestTest{

    @ParameterizedTest(name = "{index} {0}")
    @ValueSource(strings = { "Organizer", "Customer" })
    @DisplayName("Check that a non-register user can create an Organizer/Customer User")
    public void createUserTest(String type) throws Exception {

        // CREATE NEW USER

        ObjectNode user = objectMapper.createObjectNode()
            .put("name", "NewUser_"+ type)
            .put("email", type+"@urjc.es")
            .put("password", "pass");

        given()
            .request()
                .body(user)
                .contentType(ContentType.JSON)
        .when()
            .post("/api/users/?type="+type)
        .then()
            .assertThat().statusCode(HttpStatus.SC_CREATED)
            .body("name", equalTo(user.get("name").asText()));

        // CHECK THAT NEW USER EXIST (AS ME)

        given()
            .auth()
                .basic(user.get("name").asText(), user.get("password").asText())
        .when()
            .get("/api/users/"+type.toLowerCase()+"s/me")
        .then()
            .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(user.get("name").asText()))
        ;

    }

    @ParameterizedTest(name = "{index} {0}")
    @ValueSource(strings = { "Organizer", "Customer" })
    @DisplayName("Check that admin can delete a user")
    public void deleteUserTest(String type) throws Exception {

        // CREATE NEW USER

        ObjectNode user = objectMapper.createObjectNode()
                .put("name", "ToDeleteUser_" + type)
                .put("email", type + "_delete@urjc.es")
                .put("password", "pass");

        User createdUser = 
            given()
                .request()
                    .body(user)
                    .contentType(ContentType.JSON)
            .when()
                .post("/api/users/?type=" + type)
            .then()
                .assertThat()
                    .statusCode(HttpStatus.SC_CREATED)
                    .body("name", equalTo(user.get("name").asText()))
                .extract().as(User.class);

        // DELETE CREATED USER (AS ADMIN)

        given()
            .auth()
                .basic("admin", "pass")
        .when()
            .delete("/api/users/{id}", createdUser.getId())
        .then()
            .assertThat()
                .statusCode(HttpStatus.SC_NO_CONTENT);
        
        // CHECK THAT USER NOT LONGER EXIST (AS ADMIN)
        given()
            .auth()
                .basic("admin", "pass")
        .when()
            .get("/api/users/" + type.toLowerCase()+ "s/{id}", createdUser.getId())
        .then()
            .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND);
        
    }
    
}
