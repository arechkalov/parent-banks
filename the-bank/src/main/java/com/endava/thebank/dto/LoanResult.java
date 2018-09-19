package com.endava.thebank.dto;

public class LoanResult {

	private LoanStatus loanStatus;
	private String rejectionReason;

	public LoanResult() {
	}

	public LoanResult(LoanStatus loanStatus, String rejectionReason) {
		this.loanStatus = loanStatus;
		this.rejectionReason = rejectionReason;
	}

	public LoanStatus getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(LoanStatus loanStatus) {
		this.loanStatus = loanStatus;
	}

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
