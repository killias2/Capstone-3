package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

public interface TransferDAO {

	List<Transfer> getAllUserTransfer(User user);
	
	Transfer getTransferById(Long transferId);
	
	Transfer createTransfer(Transfer createdTransfer);
	
	void updateTransfer(Transfer updatedTransfer);
	
	List<Transfer> getAllPendingTransfers(User user);
	
}
