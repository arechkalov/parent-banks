package com.endava.thebank;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import com.endava.thebank.dto.Loan;
import com.endava.thebank.dto.LoanResult;
import com.endava.thebank.dto.LoanStatus;
import com.endava.thebank.dto.Participant;
import com.endava.thebank.service.LoanService;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class LoanServiceTests {

    /**
     * A junit rule that wraps every test annotated with PactVerification.
     * Before each test, a mock server will be setup at given port/host that will
     * provide mocked responses for the given provider. After each test, it will be teared down.
     */
    @Rule
    public PactHttpsProviderRuleMk2 mockProvider = new PactHttpsProviderRuleMk2("fraud-service", PactSpecVersion.V3, this);

    @Pact(consumer = "thebank", provider = "fraud-service")
    public RequestResponsePact shouldNotBeFraud(PactDslWithProvider builder) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        PactDslJsonBody requestBody = new PactDslJsonBody()
                .stringMatcher("participant.id", "[0-9]{10}", "1234567891")
                .decimalType("loanAmount", 123.123d);


        PactDslJsonBody responseBody = new PactDslJsonBody()
                .stringValue("fraudCheckStatus", "OK")
                .stringValue("rejection.reason", null);

        return builder
                .given("should mark participant as not fraud")
                   .uponReceiving("A NON-Fraud Loan with PUT request")
                   .path("/fraudcheck")
                   .method(HttpMethod.PUT.name())
                   .headers(httpHeaders.toSingleValueMap())
                   .body(requestBody)
                .willRespondWith()
                   .status(200)
                   .headers(httpHeaders.toSingleValueMap())
                   .body(responseBody)
                .toPact();
    }

    @Pact(consumer = "thebank", provider = "fraud-service")
    public RequestResponsePact shouldBeFraud(PactDslWithProvider builder) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        PactDslJsonBody requestBody = new PactDslJsonBody()
                .stringMatcher("participant.id", "[0-9]{10}", "1234567891")
                .decimalType("loanAmount", 99999d);

        PactDslJsonBody responseBody = new PactDslJsonBody()
                .stringValue("fraudCheckStatus", "FRAUD")
                .stringValue("rejection.reason", "Amount too high");

        return builder
                .given("should mark participant as fraud")
                   .uponReceiving("A Fraud Loan with PUT request")
                   .path("/fraudcheck")
                   .method(HttpMethod.PUT.name())
                   .headers(httpHeaders.toSingleValueMap())
                   .body(requestBody)
                .willRespondWith()
                   .status(200)
                   .headers(httpHeaders.toSingleValueMap())
                   .body(responseBody)
                .toPact();
    }

    /**
     * Before each test, a mock server will be setup at given port/host
     * that will provide mocked responses. after each test, it will be teared down.
     */
    @Test
    @PactVerification(value = "fraud-service", fragment = "shouldBeFraud")
    public void shouldBeFraud() {

        LoanResult result = new LoanService(mockProvider.getUrl()).apply(new Loan(new Participant("1111111111"), 99999d));

        assertThat(result.getLoanStatus()).isEqualTo(LoanStatus.REJECTED);
        assertThat(result.getRejectionReason()).isEqualTo("Amount too high");
    }

    @Test
    @PactVerification(value = "fraud-service", fragment = "shouldNotBeFraud")
    public void shouldBeNotFraud() {
        LoanResult result = new LoanService(mockProvider.getUrl()).apply(new Loan(new Participant("1234567890"), 123.123d));

        assertThat(result.getLoanStatus()).isEqualTo(LoanStatus.APPLIED);
        assertThat(result.getRejectionReason()).isEqualTo(null);
    }

}
