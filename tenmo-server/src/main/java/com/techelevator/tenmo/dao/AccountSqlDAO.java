package com.techelevator.tenmo.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.*;

@Service
public class AccountSqlDAO implements AccountDAO{
	
	private JdbcTemplate jdbcTemplate;
	
	public AccountSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Account getAccount(User user) {
		
		Account foundAccount = new Account();
		
		String sqlQuery = "SELECT account_id, user_id, balance "
						+ "FROM accounts "
						+ "WHERE user_id = ?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, user.getId());
		
		if(results.next()) {
			foundAccount = mapRowToAccount(results);
		}
		
		return foundAccount;
		
	}

	@Override
	public void updateAccount(Account account, Transfer transfer) {
		
		String evalType = "+";
		
		if(account.getAccountId() == transfer.getAccountFrom()) {
			evalType = "-";
		}
		
		
		String sqlQuery = "UPDATE account "
						+ "SET balance = (? " + evalType + " ?) / 100 "
						+ "WHERE account_id = ?";
		
		//account.setBalance((double) account.getBalance() / 100);
		//transfer.setAmount((double) transfer.getAmount() / 100);
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, account.getBalance(), transfer.getAmount(), account.getAccountId());
		
	}

	@Override
	public Account getAccountById(Long accountId) {
		Account foundAccount = new Account();
		String sqlQuery = "SELECT account_id, user_id, balance "
						+ "FROM accounts "
						+ "WHERE account_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, accountId);
		
		if(results.next()) {
			foundAccount = mapRowToAccount(results);
		}
		return foundAccount;
	}
	
	public Account mapRowToAccount(SqlRowSet results) {
		
		Account account = new Account();
		
		account.setAccountId(results.getLong("account_id"));
		account.setUserId(results.getLong("user_id"));
		double moneyDub = results.getDouble("balance");
		account.setBalance((int) (moneyDub * 100));
		
		return account;
	}
	
}
