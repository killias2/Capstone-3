package com.techelevator.tenmo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

public class UserService {

	private String BASE_URL;
	private RestTemplate restTemplate = new RestTemplate();

	public UserService(String url) {
		this.BASE_URL = url;
	}

	private HttpEntity makeAuthEntity(AuthenticatedUser authUser) {

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authUser.getToken());
		HttpEntity entity = new HttpEntity<>(headers);

		return entity;
	}
	
	private HttpEntity<Transfer> makeTransferEntity(AuthenticatedUser authUser, Transfer menuTransfer) {

		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(authUser.getToken());
		HttpEntity<Transfer> entity = new HttpEntity<>(menuTransfer,headers);

		return entity;
	}

	public List<User> getUserList(AuthenticatedUser authUser) throws UserServiceException {

		List<User> userList = new ArrayList<>();
//	    	HttpHeaders headers = new HttpHeaders();
//	    	headers.setBearerAuth(authUser.getToken());
//	    	HttpEntity entity = new HttpEntity<>(headers);

		try {
			userList = restTemplate.exchange(BASE_URL + "users", HttpMethod.GET, makeAuthEntity(authUser),
					new ParameterizedTypeReference<List<User>>() {
					}).getBody();
		} catch (RestClientResponseException e) {
			throw new UserServiceException("There was an issue with your request.");
		}

		return userList;
	}

	public Account getAccount(AuthenticatedUser authUser) throws UserServiceException {

		Account thisAccount = new Account();
		try {
			thisAccount = restTemplate.exchange(BASE_URL + "users/" + authUser.getUser().getId() + "/balance",
					HttpMethod.GET, makeAuthEntity(authUser), Account.class).getBody();
		} catch (RestClientResponseException e) {
			throw new UserServiceException("There was an issue with your request.");
		}

		return thisAccount;
	}

	public Long getAccountId(AuthenticatedUser authUser, User user) throws UserServiceException {

		Long accountId = 0L;
		try {
			accountId = restTemplate.exchange(BASE_URL + "users/" + user.getId() + "/accountid", HttpMethod.GET,
					makeAuthEntity(authUser), Long.class).getBody();
		} catch (RestClientResponseException e) {
			throw new UserServiceException("There was an issue with your request.");
		}

		return accountId;
	}

	public Transfer getTransferById(Long transferId, AuthenticatedUser authUser) throws UserServiceException {
		Transfer thisTransfer = new Transfer();
		try {
			thisTransfer = restTemplate.exchange(BASE_URL + "/transfers/" + transferId, HttpMethod.GET,
					makeAuthEntity(authUser), Transfer.class).getBody();

		} catch (RestClientResponseException e) {
			throw new UserServiceException("There was an issue with your request.");
		}
		return thisTransfer;
	}

	public Transfer createTransfer(Transfer menuTransfer, AuthenticatedUser authUser) throws UserServiceException {
		try {
			menuTransfer = restTemplate.postForObject(BASE_URL + "/transfers", makeTransferEntity(authUser, menuTransfer),
					Transfer.class);
		} catch (RestClientResponseException e) {
			throw new UserServiceException("There was an issue with your request.");
		}
		return menuTransfer;
	}
	
	public List<Transfer> getTransferList(AuthenticatedUser authUser) throws UserServiceException {
		List<Transfer> transferList = new ArrayList<>();
		try {
			transferList = restTemplate.exchange(BASE_URL + "/transfers?username=" + authUser.getUser().getUsername(), HttpMethod.GET,
					makeAuthEntity(authUser), new ParameterizedTypeReference<List<Transfer>>() {
			}).getBody();
		} catch (RestClientResponseException e) {
			throw new UserServiceException("There was an issue with your request.");
		}
		return transferList;
	}

	public List<Transfer> getPendingTransferList(AuthenticatedUser authUser) throws UserServiceException {
		List<Transfer> transferList = new ArrayList<>();
		try {
			transferList = restTemplate.exchange(BASE_URL + "/transfers?username=" + authUser.getUser().getUsername() + "&status=Pending", HttpMethod.GET,
					makeAuthEntity(authUser), new ParameterizedTypeReference<List<Transfer>>() {
			}).getBody();
		} catch (RestClientResponseException e) {
			throw new UserServiceException("There was an issue with your request.");
		}
		return transferList;
	}
	
	public void updateTransfer(Transfer menuTransfer, AuthenticatedUser authUser) throws UserServiceException {
		try {
			restTemplate.put(BASE_URL + "/transfers/" + menuTransfer.getTransferId(), makeTransferEntity(authUser, menuTransfer));
		} catch (RestClientResponseException e) {
			throw new UserServiceException("There was an issue with your request.");
		}
		
	}
	
}
