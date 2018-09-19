package com.endava.bank;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import com.endava.bank.dto.Loan;
import com.endava.bank.dto.LoanResult;
import com.endava.bank.dto.LoanStatus;
import com.endava.bank.dto.Participant;
import com.endava.bank.service.LoanService;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by arecicalov on 10/9/2017.
 */
public class LoanServiceTest {

    @Rule
    public PactProviderRuleMk2 mockProvider
            = new PactProviderRuleMk2("fraud-service", PactSpecVersion.V3, this);

    @Pact(consumer = "bank")
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
                .uponReceiving("PUT REQUEST")
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

    @Pact(consumer = "bank")
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
                .uponReceiving("PUT REQUEST")
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


    @Test
    @PactVerification(value = "fraud-service", fragment = "shouldBeFraud")
    public void shouldBeFraud() {

        LoanResult result = new LoanService(mockProvider.getUrl()).apply(new Loan(new Participant("1234567890"), 99999d));

        Assertions.assertThat(result.getLoanStatus()).isEqualTo(LoanStatus.REJECTED);
    }

    @Test
    @PactVerification(value = "fraud-service", fragment = "shouldNotBeFraud")
    public void shouldBeNotFraud() {
        LoanResult result = new LoanService(mockProvider.getUrl()).apply(new Loan(new Participant("1234567890"), 123.123d));

        Assertions.assertThat(result.getLoanStatus()).isEqualTo(LoanStatus.APPLIED);
    }


}
