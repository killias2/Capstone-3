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
	
	@Override
	public Transfer getTransferById(Long transferId) {
		Transfer foundTransfer = new Transfer();
		String sqlQuery = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, " +
				  "t.account_from, t.account_to, t.amount, s.transfer_status_desc, ty.transfer_type_desc " +
				  "FROM transfers t " +
				  "JOIN transfer_statuses s ON s.transfer_status_id = t.transfer_status_id " +
				  "JOIN transfer_types ty ON ty.transfer_type_id = t.transfer_type_id " +
				  "WHERE t.transfer_id = ?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, transferId);
		if(results.next()) {
			foundTransfer = mapRowToTransfer(results);
		}
		return foundTransfer;
	}

	@Override
	public Transfer createTransfer(Transfer createdTransfer) {
		String sqlInsert = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, " +
				"account_from, account_to, amount) " +
				"VALUES(?,?,?,?,?,?)";
		createdTransfer.setTransferId(getNextTransferId());
		double dubMoney = ((double)createdTransfer.getAmount()/ 100);
		jdbcTemplate.update(sqlInsert, createdTransfer.getTransferId(), createdTransfer.getTransferTypeId(), 
				createdTransfer.getTransferStatusId(), createdTransfer.getAccountFrom(), 
				createdTransfer.getAccountTo(), dubMoney);
		
		if (createdTransfer.getTransferType().equals("Send")) {
			Account fromAccount = accountDao.getAccountById(createdTransfer.getAccountFrom());
			Account toAccount = accountDao.getAccountById(createdTransfer.getAccountTo());
			accountDao.updateAccount(fromAccount, createdTransfer);
			accountDao.updateAccount(toAccount, createdTransfer);
		}
		
		return createdTransfer;
	}

	@Override
	public Transfer updateTransfer(Transfer updatedTransfer) {
		String sqlUpdate = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";
		jdbcTemplate.update(sqlUpdate, updatedTransfer.getTransferStatusId(), updatedTransfer.getTransferId());
		
		if (updatedTransfer.getTransferStatus().equals("Approved")) {
			Account fromAccount = accountDao.getAccountById(updatedTransfer.getAccountFrom());
			Account toAccount = accountDao.getAccountById(updatedTransfer.getAccountTo());
			accountDao.updateAccount(fromAccount, updatedTransfer);
			accountDao.updateAccount(toAccount, updatedTransfer);
		}
		
		return updatedTransfer;
	}

	@Override
	public List<Transfer> getAllPendingTransfers(User user) {
		Account thisAccount = accountDao.getAccount(user);
		
		String sqlQuery = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, " +
						  "t.account_from, t.account_to, t.amount, s.transfer_status_desc, ty.transfer_type_desc " +
						  "FROM transfers t " +
						  "JOIN transfer_statuses s ON s.transfer_status_id = t.transfer_status_id " +
						  "JOIN transfer_types ty ON ty.transfer_type_id = t.transfer_type_id " +
						  "WHERE (t.account_from = ? OR t.account_to = ?) " +
						  "AND s.transfer_status_desc = 'Pending'";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, thisAccount.getAccountId(), thisAccount.getAccountId());
		List<Transfer> allPendingTransfers = new ArrayList<>();
		while(results.next()) {
			Transfer transfer = new Transfer();
			transfer = mapRowToTransfer(results);
			allPendingTransfers.add(transfer);
		}
		return allPendingTransfers;
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
		transfer.setTransferTypeId(results.getLong("transfer_type_id"));
		transfer.setTransferStatusId(results.getLong("transfer_status_id"));
		return transfer;
	}
	
	private Long getNextTransferId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_transfer_id')");
			if(nextIdResult.next() ) {
				return nextIdResult.getLong(1);
			} else {
				throw new RuntimeException ("Something went wrong while getting a new ID");
			}
	}

	
	
}
