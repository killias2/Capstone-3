package com.techelevator.tenmo.dao;


import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

@Service
public class TransferSqlDAO implements TransferDAO {

	private JdbcTemplate jdbcTemplate;
	private AccountDAO accountDao;
	
	public TransferSqlDAO(JdbcTemplate jdbcTemplate, AccountDAO accountDao) {
		this.jdbcTemplate = jdbcTemplate;
		this.accountDao = accountDao;
	}
	
	public List<Transfer> getAllUserTransfer(User user) {
		Account thisAccount = accountDao.getAccount(user);
		
		String sqlQuery = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, " +
						  "t.account_from, t.account_to, t.amount, s.transfer_status_desc, ty.transfer_type_desc " +
						  "FROM transfers t " +
						  "JOIN transfer_statuses s ON s.transfer_status_id = t.transfer_status_id " +
						  "JOIN transfer_types ty ON ty.transfer_type_id = t.transfer_type_id " +
						  "WHERE t.account_from = ? OR t.account_to = ? ";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, thisAccount.getAccountId(), thisAccount.getAccountId());
		List<Transfer> allTransfers = new ArrayList<>();
		while(results.next()) {
			Transfer transfer = new Transfer();
			transfer = mapRowToTransfer(results);
			allTransfers.add(transfer);
		}
		return allTransfers;
	}

	public Transfer mapRowToTransfer(SqlRowSet results) {
		
		Transfer transfer = new Transfer();
		transfer.setAccountFrom(results.getLong("account_from"));
		transfer.setAccountTo(results.getLong("account_to"));
		double moneyDub = results.getDouble("amount");
		transfer.setAmount((int) (moneyDub * 100));
		transfer.setTransferId(results.getLong("transfer_id"));
		transfer.setTransferStatus(results.getString("transfer_status_desc"));
		transfer.setTransferType(results.getString("transfer_type_desc"));
		return transfer;
	}
	
	
}
