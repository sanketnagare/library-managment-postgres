package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class main {

	public static void main(String[] args) {
		String jdbcURL = "jdbc:postgresql://localhost:5432/library";
		String username = "postgres";
		String password = "Sanket@123";
		
		Scanner scanner = new Scanner(System.in);
		String name = null;
		String group = null;
    	int userID = -1;
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection(jdbcURL, username, password);

			boolean isExit = false;
			
			while(!isExit) {
				
		        System.out.println("----------WELCOME------------");
		        System.out.println("1. ALREADY REIGSTERED USER");
		        System.out.println("2. NEW USER");
		        System.out.println("3. ADD or DELETE BOOK / MAGAZINE");
		        System.out.println("4. EXIT");
		        
		        int option = scanner.nextInt();
		        scanner.nextLine();
		        
		        
		        switch (option) {
				case 1:{
					System.out.println("Enter your user ID");
					userID = scanner.nextInt();
					scanner.nextLine();					
					if(!Member.checkMember(connection, userID)) {
						continue;
					}
	        		break;
				}
				case 2:{
					System.out.println("Hello new user let's add your information");
					System.out.println("Enter you name");
					name = scanner.nextLine();
					
					System.out.println("Select your group:");
					System.out.println("1. Student");
					System.out.println("2. Teacher");
					
					int groupChoice = scanner.nextInt();
					scanner.nextLine();
					
					group = (groupChoice==1) ? "student" : "teacher";
					
				    int newMemberID = Member.addMemeber(connection, name, group); // Capture the new member's ID
				    if (newMemberID != -1) {
				        System.out.println("Your member ID is: " + newMemberID);
				        userID = newMemberID; 
				    }
				    break;
				}
				case 3:{
					boolean bookOrMagazine = false;
					while(!bookOrMagazine) {
						 System.out.println("1. Add Book");
                         System.out.println("2. Add Magazine");
                         System.out.println("3. Delete Book");
                         System.out.println("4. Delete Magazine");
                         System.out.println("5. Back to Main Menu");

                         int addDeleteOption = scanner.nextInt();
                         scanner.nextLine();
                         
                         
                         switch (addDeleteOption) {
                         
                         case 1:{
                        	System.out.println("Enter the title of the new book.");
             				String newBookTitleString = scanner.nextLine();
             				System.out.println("Enter Author of the new Book.");
             				String newBookAuthorString = scanner.nextLine();
             				
             				Book newBook = new Book(newBookTitleString, newBookAuthorString, true);
             				Book.addBook(connection, newBook);
             				break;
                         }
                         case 2: {
                        	 System.out.println("Enter the title of the new magazine.");
                             String newMagazineTitle = scanner.nextLine();
                             System.out.println("Enter Publisher of the new Magazine.");
                             String newMagazinePublisher = scanner.nextLine();

                             Magazine newMagazine = new Magazine(newMagazineTitle, newMagazinePublisher, true);
                             Magazine.addMagazine(connection, newMagazine);
                             break;
                         }
                         case 3:{
                        	 System.out.println("Enter the ID of the book you want to delete.");
                        	 Book.showAvailableBooks(connection);
                             int bookIDToDelete = scanner.nextInt();
                             Book.deleteBook(connection, bookIDToDelete);
                             break;
                         }
                         case 4:{
                        	 System.out.println("Enter the ID of the magazine you want to delete.");
                             int magazineIDToDelete = scanner.nextInt();
                             Magazine.deleteMagazine(connection, magazineIDToDelete);
                             break;
                         }
                         case 5:{
                        	 bookOrMagazine = true;
                        	 break;
                         }
                         default:{
                        	 System.out.println("Invalid option..");
                        	 break;
                         }
                         }

						}
					break;
					}
				case 4:{
					isExit=true;
					break;
				}
				default:{
					System.out.println("Invalid option..");
					break;
				}
				
			}
			

			if(userID != -1)
			{
				Boolean isSubMenu = false;
				while(!isSubMenu)
				{
				System.out.println("-------BOOK OPTIONS------------");
				System.out.println("1. Issue book");
				System.out.println("2. Return book");
				System.out.println("3. Show issued books");
				System.out.println("4. Show available books");
				System.out.println("");
				System.out.println("-------MAGAZINE OPTIONS--------");
				System.out.println("5. Issue Magazine");
				System.out.println("6. Return Magazine");
				System.out.println("7. Show issued magazines");
				System.out.println("8. Show available magazines");
				System.out.println("");
				System.out.println("------------OTHER--------------");
				System.out.println("9. Show Past history");
				System.out.println("10. Exit");
				System.out.println("      SELECT AN OPTION         ");
				
				
				int choice = scanner.nextInt();
				scanner.nextLine();
				
				switch (choice) {
				case 1: {
					Book.showAvailableBooks(connection);
					System.out.println("Enter the ID book you want to issue");
					int bookId = scanner.nextInt();
					Book.issueBook(connection, userID, bookId);
					break;
				}
				case 2: {
					System.out.println("Enter the ID book you want to return");
					int bookId = scanner.nextInt();
					Book.returnBook(connection, userID, bookId);
					IssuedItems.showIssuedBooks(connection, userID);
					break;
				}
				
				case 3: {
					IssuedItems.showIssuedBooks(connection, userID);
					break;
				}
				
				case 4: {
					Book.showAvailableBooks(connection);
					break;
				}
				
				case 5: {
					Magazine.showAvailableMagazines(connection);
					System.out.println("Enter the ID of the magazine you want to issue.");
					int magazineId = scanner.nextInt();
					Magazine.issueMagazine(connection, userID, magazineId);
					break;
				}
				
				case 6: {
					System.out.println("Enter the ID of the magazine you want to return.");
					int magazineID = scanner.nextInt();
					Magazine.returnMagazine(connection, userID, magazineID);
					IssuedItems.showIssuedMagazines(connection, userID);
					break;
				}

				
				case 7: {
					IssuedItems.showIssuedMagazines(connection, userID);
					break;
				}
				
				case 8: {
					Magazine.showAvailableMagazines(connection);
					break;
				}
				
				case 9: {
					IssuedItems.pastHistory(connection, userID);
					break;
				}
				
				case 10: {
					isSubMenu = true;
					break;
				}
				case 11: {
					
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + choice);
				}
			}
			
			System.out.println("Exited out");
			}
		}
		}
	
		catch (Exception e) {
			System.out.println("Problem in connection");
			e.printStackTrace();
		}
		
		
		
		finally {
			if(connection!=null) {
				try {
					connection.close();
					scanner.close();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		}}


