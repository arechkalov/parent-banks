package com.endava.thebank.dto.fraud;

import com.endava.thebank.dto.Loan;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class FraudServiceRequest {

	@JsonProperty("participant.id")
	private String participantId;

	private BigDecimal loanAmount;

	public FraudServiceRequest() {
	}

	public FraudServiceRequest(Loan loanApplication) {
		this.participantId = loanApplication.getParticipant().getId();
		this.loanAmount = loanApplication.getAmount();
	}

	public String getParticipantId() {
		return participantId;
	}

	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}
}
