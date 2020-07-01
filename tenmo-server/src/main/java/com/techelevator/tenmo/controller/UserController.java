package com.techelevator.tenmo.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AccountNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferNotFoundException;
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

	//@PreAuthorize("permitAll")
	@RequestMapping(path = "/users", method = RequestMethod.GET)
	public List<User> getAllUsers() {

		return userDao.findAll();

	}

	@ResponseStatus(code = HttpStatus.CREATED)
	@RequestMapping(path = "/transfers", method = RequestMethod.POST)
	public Transfer createTransfer(@Valid @RequestBody Transfer transfer, Principal p) throws TransferNotFoundException {

		if (transfer.getTransferType().equals("Send")) {
			
			User thisUser = userDao.findByUsername(p.getName());
			Account fromAccount = accountDao.getAccount(thisUser);
			if(fromAccount.getAccountId() != transfer.getAccountFrom()) {
				return transferDao.getTransferById(null);
			}
			else {
				if (transfer.getAmount() > fromAccount.getBalance()) {
					return transferDao.getTransferById(null);
				} 	else {
				
//						fromAccount.setBalance(fromAccount.getBalance() - transfer.getAmount());
//						accountDao.updateAccount(fromAccount);
//						
//						Account toAccount = accountDao.getAccountById(transfer.getAccountTo());
//						toAccount.setBalance(toAccount.getBalance() + transfer.getAmount());
//						accountDao.updateAccount(toAccount);
						
						return transferDao.createTransfer(transfer);
					}
			}
		}
		else {
			User thisUser = userDao.findByUsername(p.getName());
			Account toAccount = accountDao.getAccount(thisUser);
			if(toAccount.getAccountId() != transfer.getAccountTo()) {
				return transferDao.getTransferById(null);
			}
			else {
			
				transfer.setAccountTo(toAccount.getAccountId());
				return transferDao.createTransfer(transfer);
			}
		}
	}
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@RequestMapping(path = "/transfers/{id}", method = RequestMethod.PUT)
	public Transfer updateTransfer(@Valid @RequestBody Transfer transfer, @PathVariable long id, Principal p)  throws TransferNotFoundException {
		
		User thisUser = userDao.findByUsername(p.getName());
		Account fromAccount = accountDao.getAccount(thisUser);
		if(fromAccount.getAccountId() != transfer.getAccountFrom()) {
			return transferDao.getTransferById(null);
		}
		
		
		return transferDao.updateTransfer(transfer);
	}
	
	@RequestMapping(path = "/users/{id}/balance", method = RequestMethod.GET)
	public Account getAccountBalance(@PathVariable long id, Principal p) throws AccountNotFoundException {
		
		User thisUser = userDao.findByUsername(p.getName());
		
		if(id != thisUser.getId()) {
			return accountDao.getAccountById(null);
		}
		
		return accountDao.getAccountById(id);
	}
	
	@RequestMapping(path = "/transfers", method = RequestMethod.GET)
	public List<Transfer> getTransferList(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String status, Principal p) throws TransferNotFoundException {
		if (!username.equals(p.getName())) {
			User nullUser = new User();
			return transferDao.getAllUserTransfer(nullUser);
		}
		
		if (!status.equals("Pending")) {
			return transferDao.getAllUserTransfer(userDao.findByUsername(username));
		}
		
		return transferDao.getAllPendingTransfers(userDao.findByUsername(username));
	}
	
	@RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
	public Transfer getTransferById(@PathVariable Long id, Principal p) throws TransferNotFoundException {
		User thisUser = userDao.findByUsername(p.getName());
		Account thisAccount = accountDao.getAccount(thisUser);
		if (thisAccount.getAccountId() != id) {
			return transferDao.getTransferById(null);
		}
		
		return transferDao.getTransferById(id);
	}
	
	
	
}
