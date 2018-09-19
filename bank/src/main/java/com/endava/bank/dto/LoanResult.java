package com.endava.bank.dto;

public class LoanResult {

	private LoanStatus loanStatus;

	public LoanResult() {
	}

	public LoanResult(LoanStatus loanStatus) {
		this.loanStatus = loanStatus;
	}

	public LoanStatus getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(LoanStatus loanStatus) {
		this.loanStatus = loanStatus;
	}

}
