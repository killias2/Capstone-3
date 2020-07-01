package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Transfer {

	private Long transferId;
	@NotBlank
	private String transferType;
	@NotBlank
	private String transferStatus;
	@NotNull
	private Long accountFrom;
	@NotNull
	private Long accountTo;
	@Min(value = 1)
	private int amount;
	@Min(value = 1)
	private Long transferTypeId;
	@Min(value = 1)
	private Long transferStatusId;
	
	public Long getTransferTypeId() {
		return transferTypeId;
	}
	public void setTransferTypeId(Long transferTypeId) {
		this.transferTypeId = transferTypeId;
	}
	public Long getTransferStatusId() {
		return transferStatusId;
	}
	public void setTransferStatusId(Long transferStatusId) {
		this.transferStatusId = transferStatusId;
	}
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
