package com.techelevator.tenmo.model;

public class Transfer {

	private Long transferId;
	private String transferType;
	private String transferStatus;
	private Long accountFrom;
	private Long accountTo;
	private int amount;
	public Long getTransferId() {
		return transferId;
	}
	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public String getTransferStatus() {
		return transferStatus;
	}
	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}
	public Long getAccountFrom() {
		return accountFrom;
	}
	public void setAccountFrom(Long accountFrom) {
		this.accountFrom = accountFrom;
	}
	public Long getAccountTo() {
		return accountTo;
	}
	public void setAccountTo(Long accountTo) {
		this.accountTo = accountTo;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
}
