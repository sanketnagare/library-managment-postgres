package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Date;

import java.util.Calendar;

public class Book {
	private int book_id;
	private String title;
	private String author;
	private boolean availabality;
	
	
	public Book(String title, String author, boolean availabality) {
        this.setBook_id(book_id);
        this.setTitle(title);
        this.setAuthor(author);
        this.setAvailabality(availabality);
    }
	
	public static void addBook(Connection connection, Book book) {
		
		String newBookQuery = "insert into books(title, author, availability) values (? , ?, ?)";
		
		try(PreparedStatement newBookStatement = connection.prepareStatement(newBookQuery)) {
			
			newBookStatement.setString(1, book.getTitle());
			newBookStatement.setString(2, book.getAuthor());
			newBookStatement.setBoolean(3, book.isAvailabality());
			
			int affectedRows = newBookStatement.executeUpdate();
			
			if(affectedRows > 0) {
				System.out.println("Book Added Successfully");
			}
			else {
				System.out.println("Failed to add new book.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error while adding the book.");
		}
	}
	
	
	public static void deleteBook(Connection connection, int bookID) {
	    //check if the book is issued before deleting
	    String issuedCheckQuery = "SELECT * FROM issue_items_book WHERE book_id = ?";
	    try (PreparedStatement issuedCheckStatement = connection.prepareStatement(issuedCheckQuery)) {
	        issuedCheckStatement.setInt(1, bookID);
	        ResultSet issuedCheckResult = issuedCheckStatement.executeQuery();

	        if (issuedCheckResult.next()) {
	            System.out.println("Cannot delete the book. It is currently issued.");
	        } else {
	            // Delete the book if not issued
	            String deleteQuery = "DELETE FROM books WHERE book_id = ?";
	            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
	                deleteStatement.setInt(1, bookID);
	                int affectedRows = deleteStatement.executeUpdate();

	                if (affectedRows > 0) {
	                    System.out.println("Book deleted from the library.");
	                } else {
	                    System.out.println("Failed to delete the book. Book ID not exist.");
	                }
	            }
	        }

	    } catch (SQLException e) {
	        System.out.println("Error while deleting the book.");
	        e.printStackTrace();
	    }
	}
	
	public static void showAvailableBooks(Connection connection) {
        String query = "SELECT * FROM books WHERE availability = true order by book_id";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            System.out.println("+---------+---------------------+----------------------+");
            System.out.println("| Book ID | Title               | Author               |");
            System.out.println("+---------+---------------------+----------------------+");

            while (resultSet.next()) {
                int bookID = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");

                String formattedOutput = String.format("| %-7d | %-19s | %-20s |%n", bookID, title, author);
                System.out.print(formattedOutput);
            }

            System.out.println("+---------+---------------------+----------------------+");  

        } 
        catch (SQLException e) {
            e.printStackTrace();
            
        }
    }


	public static void issueBook(Connection connection, int userID, int bookID) {
//		for student
		int maxBooksAllowed = 2;
		int maxTotalItemsAllowed = 2;
		
		if(Validation.isTeacher(connection, userID)) {
//			for teacher
			maxBooksAllowed = 5;
			maxTotalItemsAllowed=6;
		}
		
		if(Validation.hasExceededBooksLimit(connection, userID, maxBooksAllowed) ||
			Validation.hasExceededTotalItemsLimit(connection, userID, maxTotalItemsAllowed)) {
			System.out.println("You have reached maximum limit to issue..");
			return;
		}
		
		
        // Check if the book is available return true or false
        String availabilityQuery = "SELECT availability FROM books WHERE book_id = ?";
        
        try (PreparedStatement availabilityStatement = connection.prepareStatement(availabilityQuery)) {
        	
            availabilityStatement.setInt(1, bookID);
            ResultSet availabilityResult = availabilityStatement.executeQuery();

            if (availabilityResult.next() && availabilityResult.getBoolean("availability")) {
                // Update the book's availability to false
                String updateQuery = "UPDATE books SET availability = false WHERE book_id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setInt(1, bookID);
                    updateStatement.executeUpdate();
                }

                // Insert the record into issued_items_book
                String insertQuery = "INSERT INTO issue_items_book (member_id, book_id, issue_date) VALUES (?, ?, ?)";
                Date issue_date = new Date(Calendar.getInstance().getTime().getTime());

                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, userID);
                    insertStatement.setInt(2, bookID);
                    insertStatement.setDate(3,  issue_date);
                    insertStatement.executeUpdate();
                }

                // Update the count of issued books for the member
                String countUpdateQuery ="UPDATE members SET total_issued = total_issued + 1, issued_books_count = issued_books_count + 1 WHERE member_id = ?";

                try (PreparedStatement countUpdateStatement = connection.prepareStatement(countUpdateQuery)) {
                    countUpdateStatement.setInt(1, userID);
                    countUpdateStatement.executeUpdate();
                }

                System.out.println("Book issued successfully.");
            } else {
                System.out.println("The selected book is not available.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void returnBook(Connection connection, int userId, int bookID) {
        
    	//get the id of issue_items_book so that we can set return date later to that record
    	String issuedQuery = "select issue_book_id from issue_items_book where member_id=? and book_id=?;";
    	try(PreparedStatement issuedStatement = connection.prepareStatement(issuedQuery)) {
    		
    		issuedStatement.setInt(1, userId);
    		issuedStatement.setInt(2, bookID);
    		
    		ResultSet issuedResultSet = issuedStatement.executeQuery();
    		
    		if(issuedResultSet.next()) {
    			
    			int issuedID = issuedResultSet.getInt("issue_book_id");
    			
//    			update book available to all user mark it true
    			String updateQuery = "update books set availability = true where book_id = ?";
    			try(PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
    				updateStatement.setInt(1, bookID);
    				updateStatement.executeUpdate();
				} 
    			
    			
//    			update the return date in issue_items_book on that issueID
    			String returnDateQueryString = "update issue_items_book set return_date = ? where issue_book_id = ?";
    			Date return_date = new Date(Calendar.getInstance().getTime().getTime());
    			try(PreparedStatement returnDateStatement = connection.prepareStatement(returnDateQueryString)) {
    				returnDateStatement.setDate(1, return_date);
    				returnDateStatement.setInt(2, issuedID);
    				returnDateStatement.executeUpdate();
				}
    			
//    			update the total count and issued books count in members table
    			String countUpdateQuery = "UPDATE members SET total_issued = total_issued - 1, issued_books_count = issued_books_count - 1 WHERE member_id = ?";
    			try(PreparedStatement countUpdateStatement = connection.prepareStatement(countUpdateQuery)) {
    				countUpdateStatement.setInt(1, userId);
    				countUpdateStatement.executeUpdate();
				} 
    			
    			
    			System.out.println("Book Returned Successfully");
    		}
    		else {
    			System.out.println("The selected book is not issued to you");
    		}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
 
    
    
//    Getters and setters
	public boolean isAvailabality() {
		return availabality;
	}

	public void setAvailabality(boolean availabality) {
		this.availabality = availabality;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getBook_id() {
		return book_id;
	}

	public void setBook_id(int book_id) {
		this.book_id = book_id;
	}
}
