package com.techelevator.tenmo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
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
	    
	    public List<User> getUserList(AuthenticatedUser authUser) throws UserServiceException {
	    	
	    	List<User> userList = new ArrayList<>();
//	    	HttpHeaders headers = new HttpHeaders();
//	    	headers.setBearerAuth(authUser.getToken());
//	    	HttpEntity entity = new HttpEntity<>(headers);
	    	
	    	
	    	try {
	    		userList = restTemplate.exchange(BASE_URL + "users", HttpMethod.GET, makeAuthEntity(authUser), 
	    				new ParameterizedTypeReference<List<User>>(){}).getBody();
	    	}
	    	catch (RestClientResponseException e){
	    		throw new UserServiceException("There was an error, you stink!");
	    	}
	    	
	    	return userList;
	    }
	    
	    public Account getAccount(AuthenticatedUser authUser) throws UserServiceException {
	    	
	    	Account thisAccount = new Account();
	    	try {
	    		 thisAccount = restTemplate.exchange(BASE_URL + "users/" + authUser.getUser().getId() + "/balance", 
	    				HttpMethod.GET, makeAuthEntity(authUser),
	    				Account.class).getBody();
	    	}
	    	catch (RestClientResponseException e) {
	    		throw new UserServiceException("There was an error, you stink!");
	    	}
	    	
	    	return thisAccount;
	    }
	
}
