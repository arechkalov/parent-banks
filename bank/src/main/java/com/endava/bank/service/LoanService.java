package com.endava.bank.service;

import com.endava.bank.dto.LoanResult;
import com.endava.bank.dto.LoanStatus;
import com.endava.bank.dto.Loan;
import com.endava.bank.dto.fraud.FraudCheckStatus;
import com.endava.bank.dto.fraud.FraudServiceRequest;
import com.endava.bank.dto.fraud.FraudServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoanService {

    private final RestTemplate restTemplate;
    private final String url;

    public LoanService(String url) {
        this.url = url;
        this.restTemplate = new RestTemplate();
    }

    public LoanResult apply(Loan loan) {
        FraudServiceRequest request = new FraudServiceRequest(loan);

        FraudServiceResponse response = sendRequestToFraudDetectionService(request);

        return buildResponseFromFraudResult(response);
    }

    private FraudServiceResponse sendRequestToFraudDetectionService(FraudServiceRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // tag::client_call_server[]
        ResponseEntity<FraudServiceResponse> response = restTemplate.exchange(
                url + "/fraudcheck", HttpMethod.PUT,
                new HttpEntity<>(request, httpHeaders),
                FraudServiceResponse.class);
        // end::client_call_server[]

        return response.getBody();
    }

    private LoanResult buildResponseFromFraudResult(FraudServiceResponse response) {
        LoanStatus loanStatus = null;
        if (FraudCheckStatus.OK == response.getFraudCheckStatus()) {
            loanStatus = LoanStatus.APPLIED;
        } else if (FraudCheckStatus.FRAUD == response.getFraudCheckStatus()) {
            loanStatus = LoanStatus.REJECTED;
        }

        return new LoanResult(loanStatus);
    }

}
