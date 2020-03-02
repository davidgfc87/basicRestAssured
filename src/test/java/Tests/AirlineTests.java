package Tests;

import org.testng.annotations.Test;
import io.restassured.RestAssured.*;
import static io.restassured.response.ValidatableResponseOptions.*;

public class AirlineTests {
	
	@Test
	public void getFlight() {
		io.restassured.RestAssured.given()
			.header("Content-Type", "application/json")
			.auth().basic("admin", "admin")
		.when()
			.get("http://localhost:5000/api/flights/1")
		.then()
			.statusCode(200);	
	}
	
	@Test
	public void createFlight() {
		int flight_id = 
		io.restassured.RestAssured.given()
			.header("Content-Type", "application/json")
			.body("{" + 
					"	\"origin\":\"CDMX\"," + 
					"	\"destination\":\"Buenos Aires\"," + 
					"	\"duration\":\"300\"" + 
					"}")
		.when()
			.post("http://localhost:5000/api/flight/new")
		.then()
			.statusCode(200)
			.extract().path("flight_id");			
			
		System.out.println(flight_id);
		
		String respuesta =
		io.restassured.RestAssured.given()
		.header("Content-Type", "application/json")
		.auth().basic("admin", "admin")
	.when()
		.get("http://localhost:5000/api/flights/" + flight_id)
	.then()
		.statusCode(200)
		.extract().body().asString();	
		
		System.out.println(respuesta);
	}

}
