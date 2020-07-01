package com.techelevator.tenmo.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {

	private UserDAO userDao;
	private AccountDAO accountDao;
	private TransferDAO transferDao;

	public UserController(UserDAO userDao, AccountDAO accountDao, TransferDAO transferDao) {
		this.userDao = userDao;
		this.accountDao = accountDao;
		this.transferDao = transferDao;
	}

	// @PreAuthorize("permitAll")
	@RequestMapping(path = "/users", method = RequestMethod.GET)
	public List<User> getAllUsers() {

		return userDao.findAll();

	}

	@RequestMapping(path = "/transfers", method = RequestMethod.POST)
	public Transfer createTransfer(@Valid @RequestBody Transfer transfer) {

		if (transfer.getTransferType().equals("Send")) {

			Account fromAccount = accountDao.getAccountById(transfer.getAccountFrom());
			if (transfer.getAmount() > fromAccount.getBalance()) {
				return null; // tell client cannot complete transfer
			} else {

				fromAccount.setBalance(fromAccount.getBalance() - transfer.getAmount());
				accountDao.updateAccount(fromAccount);

				Account toAccount = accountDao.getAccountById(transfer.getAccountTo());
				toAccount.setBalance(toAccount.getBalance() + transfer.getAmount());
				accountDao.updateAccount(toAccount);

				return transferDao.createTransfer(transfer);
			}
		} else {
			return transferDao.createTransfer(transfer);
		}
	}
}
