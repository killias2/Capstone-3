package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

public interface AccountDAO {

	Account getAccount(User user);
	
	void updateAccount(User user);
	
}
