package com.techelevator.tenmo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.UserService;
import com.techelevator.tenmo.services.UserServiceException;
import com.techelevator.view.ConsoleService;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private UserService userService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new UserService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, UserService userService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.userService = userService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
		try {
			Account thisAccount = userService.getAccount(currentUser);
			System.out.println("Account: " + thisAccount.getAccountId() + "\n"
							  +"Balance: " + toMoney(thisAccount.getBalance()));
		} catch (UserServiceException e) {
			// TODO Auto-generated catch block
			System.out.println("There was an error, you stink!");
		}
		
		
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub

	}

	private void sendBucks() {

		try {
			List<User> userList = userService.getUserList(currentUser);
			Map<String, User> thisMap = makeUserList(userList);
			System.out.println("Send money to (enter user ID)");
			boolean inputChecker = false;
			while (inputChecker == false) {
				String idChoice = console.getUserInput("Enter user ID");
				if (thisMap.containsKey(idChoice)) {
					if(idChoice.equals(String.valueOf(currentUser.getUser().getId()))) {
						System.out.println("Please do not choose yourself for a transaction.");
					}
					else {
						System.out.println("You have selected: " + idChoice + " " + thisMap.get(idChoice).getUsername());
						Account userAccount = userService.getAccount(currentUser);
						String cancelChoice = console.getUserInput("Enter 0 to cancel or anything else to continue");
						if(cancelChoice.equals("0")) {
							inputChecker = true;
						}
						else {
							System.out.println("Your current balance: " + toMoney(userAccount.getBalance()));
							String moneyAmount = "";
							int transferAmount = 0;
							boolean inputChecker2 = false;
							while (inputChecker2 == false) {
								try {
									moneyAmount = console.getUserInput("Enter amount to transfer");
									if (Double.parseDouble(moneyAmount) <= 0) {
										System.out.println("Please enter an amount greater than 0.");
									} 
									else if (Double.parseDouble(moneyAmount) > ((double)userAccount.getBalance()/100)) {
										System.out.println("Transfers should not be larger than remaining account balance.");
									}
									
									else {
										transferAmount = (int) (Double.parseDouble(moneyAmount) * 100);
										inputChecker2 = true;
									}
	
								} catch (NumberFormatException e) {
									System.out.println("This is not a valid choice. Please try again.");
								}
							}
							Transfer thisTransfer = new Transfer();
							thisTransfer.setTransferType("Send");
							thisTransfer.setTransferTypeId(2L);
							thisTransfer.setTransferStatus("Approved");
							thisTransfer.setTransferStatusId(2L);
							thisTransfer.setAmount(transferAmount);
							thisTransfer.setAccountFrom(userAccount.getAccountId());
							thisTransfer.setAccountTo(userService.getAccountId(currentUser, thisMap.get(idChoice)));
							userService.createTransfer(thisTransfer, currentUser);
							System.out.println("Yay, you made a transfer!");
							inputChecker = true;
						}
					}
				} else if (idChoice.equals("0")) {
					inputChecker = true;
				}

				else {
					System.out.println("Your input is invalid. Please try again.");
				}
			}
		} catch (UserServiceException e) {
			System.out.println("There was an error, you stink!");
		}

	}


	private void requestBucks() {
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
	
	private String toMoney(int amount) {
		String money = "";
		money = money.valueOf(amount / 100);
		money = money + ".00";
		return money;
	}
	
	public Map<String, User> makeUserList(List<User> userList) {
		Map<String, User> userMap = new HashMap<>();
		for (User user : userList) {
			String userId = String.valueOf(user.getId());
			userMap.put(userId, user);
			System.out.println(userId + "\t" + user.getUsername());
		}
		return userMap;
	}
	
	
}
