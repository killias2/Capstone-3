package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

public interface AccountDAO {

	Account getAccount(User user);
	
	void updateAccount(Account account, Transfer transfer);
	
	Account getAccountById(Long accountId);
	
}
