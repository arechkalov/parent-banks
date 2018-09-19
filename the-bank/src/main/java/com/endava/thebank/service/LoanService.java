package com.endava.thebank.service;

import com.endava.thebank.dto.Loan;
import com.endava.thebank.dto.LoanResult;
import com.endava.thebank.dto.LoanStatus;
import com.endava.thebank.dto.fraud.FraudCheckStatus;
import com.endava.thebank.dto.fraud.FraudServiceRequest;
import com.endava.thebank.dto.fraud.FraudServiceResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

        return new LoanResult(loanStatus, response.getRejectionReason());
    }

}
