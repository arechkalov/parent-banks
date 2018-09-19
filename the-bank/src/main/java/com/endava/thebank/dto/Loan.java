package com.endava.thebank.dto;

import java.math.BigDecimal;

public class Loan {

	private Participant participant;

	private BigDecimal amount;

	private String loanId;

	public Loan() {
	}

	public Loan(Participant participant, double amount) {
		this.participant = participant;
		this.amount = BigDecimal.valueOf(amount);
	}

	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}
}
