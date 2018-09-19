package com.endava.bank.dto.fraud;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FraudServiceResponse {

	private FraudCheckStatus fraudCheckStatus;

	public FraudServiceResponse() {
	}

	public FraudCheckStatus getFraudCheckStatus() {
		return fraudCheckStatus;
	}

	public void setFraudCheckStatus(FraudCheckStatus fraudCheckStatus) {
		this.fraudCheckStatus = fraudCheckStatus;
	}

}
