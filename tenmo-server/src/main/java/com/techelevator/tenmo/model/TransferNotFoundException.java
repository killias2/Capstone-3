package com.techelevator.tenmo.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.NOT_FOUND, reason = "Transfer not found.")
public class TransferNotFoundException extends Exception {
	
	private static final long serialVersionUID = 13L;
	
	public TransferNotFoundException() {
		super("Transfer not found.");
	}

}
