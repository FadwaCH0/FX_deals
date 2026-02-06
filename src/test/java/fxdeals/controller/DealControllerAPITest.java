package fxdeals.controller;

import fxdeals.entity.Deal;
import fxdeals.repository.DealRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DealControllerAPITest {

    @LocalServerPort
    private int port;

    @Autowired
    private DealRepository dealRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        dealRepository.deleteAll();
    }



    @Test
    void whenValidDeal_thenReturnsCreated() {
        String json = """
            {
                "dealId": "D100",
                "fromCurrency": "USD",
                "toCurrency": "EUR",
                "amount": 150
            }
            """;

        given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("/deals")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(equalTo("Deal saved successfully"));
    }


    @Test
    void whenDealInvalid_thenReturnsBadRequest() {
        String json = """
        {
            "dealId": "",
            "fromCurrency": "",
            "toCurrency": "",
            "amount": 0
        }
        """;

        given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("/deals")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("dealId", equalTo("Le dealId est obligatoire"))
                .body("fromCurrency", equalTo("Source currency is required"))
                .body("toCurrency", equalTo("Target currency is required"))
                .body("amount", equalTo("Transaction amount must be greater than 0"));
    }


    @Test
    void whenDealAlreadyExists_thenReturnsConflict() {
        Deal deal = new Deal();
        deal.setDealId("D101");
        deal.setFromCurrency("USD");
        deal.setToCurrency("EUR");
        deal.setAmount(BigDecimal.valueOf(100));
        dealRepository.save(deal);

        String json = """
        {
            "dealId": "D101",
            "fromCurrency": "USD",
            "toCurrency": "EUR",
            "amount": 100
        }
        """;

        given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("/deals")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body(equalTo("Deal already exists"));
    }


}
