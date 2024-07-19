package com.kamis.financemanager.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.kamis.financemanager.FinancemanagerApplicationTests;

import io.restassured.http.ContentType;

@ActiveProfiles(profiles = "test")
@ExtendWith(SpringExtension.class) 
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestLoanController extends FinancemanagerApplicationTests {

	@Test
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void TestGetLoanById_admin() {
		
		given().
		contentType(ContentType.JSON)
		.when().get("/api/users/1/loans/1")
		.then().statusCode(200)
		.body(".", hasSize(1));
	}

}
