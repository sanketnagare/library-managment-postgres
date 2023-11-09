package library;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IssuedItems {
    
    public static void showIssuedBooks(Connection connection, int userID) {
        String query = "select b.book_id, b.title, b.author, iib.issue_date, iib.return_date " +
                       "from books b " +
                       "join issue_items_book iib " +
                       "on b.book_id = iib.book_id " +
                       "where iib.member_id = ? and iib.return_date is null " +
                       "order by b.book_id";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("+----------------------------------------------------+");
            System.out.println("|                  ISSUED BOOKS                      |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("| Book ID | Title        | Author      | Issue Date  |");
            System.out.println("+---------+--------------+-------------+-------------+");

            while (resultSet.next()) {
                int bookID = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                Date issueDate = resultSet.getDate("issue_date");
//                Date returnDate = resultSet.getDate("return_date");
                
                String formattedOutput = String.format("| %-7d | %-12s | %-11s | %-11s |%n", bookID, title, author, issueDate);
                System.out.print(formattedOutput);
            }

            System.out.println("+---------+--------------+-------------+-------------+");
            System.out.println("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void showIssuedMagazines(Connection connection, int userID) {
    	
    	String query = "select m.magazine_id, m.title, m.publisher, iim.issue_date, iim.return_date from magazines m "
    			+ "join issue_items_magazines iim "
    			+ "on m.magazine_id = iim.magazine_id "
    			+ "where member_id = ? and iim.return_date is null "
    			+ "order by member_id ";
    	
    	try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("+-----------------------------------------------------------+");
            System.out.println("|                      ISSUED MAGAZINES                     |");
            System.out.println("+-----------------------------------------------------------+");
            System.out.println("| Magazine ID | Title        | Publisher      | Issue Date  |");
            System.out.println("+-------------+--------------+----------------+-------------+");

            while (resultSet.next()) {
                int magazineID = resultSet.getInt("magazine_id");
                String title = resultSet.getString("title");
                String publisher = resultSet.getString("publisher");
                Date issueDate = resultSet.getDate("issue_date");
//                Date returnDate = resultSet.getDate("return_date");
                
                String formattedOutput = String.format("| %-11d | %-12s | %-14s | %-11s |%n", magazineID, title, publisher, issueDate);
                System.out.print(formattedOutput);
            }

            System.out.println("+-------------+--------------+----------------+-------------+");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void pastHistory(Connection connection, int userID) {
    	
     String query = "select b.book_id, b.title, b.author, iib.issue_date, iib.return_date " +
                "from books b " +
                "join issue_items_book iib " +
                "on b.book_id = iib.book_id " +
                "where iib.member_id = ? and iib.return_date is not null " +
                "order by b.book_id";
     
     String query2 = "select m.magazine_id, m.title, m.publisher, iim.issue_date, iim.return_date from magazines m "
 			+ "join issue_items_magazines iim "
 			+ "on m.magazine_id = iim.magazine_id "
 			+ "where member_id = ? and iim.return_date is not null "
 			+ "order by member_id ";

	 try (PreparedStatement statement = connection.prepareStatement(query)) {
	     statement.setInt(1, userID);
	     ResultSet resultSet = statement.executeQuery();
	     System.out.println("+-------------------------------------------------------------------+");
	     System.out.println("|                           BOOKS HISTORY                           |");
	     System.out.println("+----------------------------------------------------+--------------+");
	     System.out.println("| Book ID | Title        | Author      | Issue Date  | Return Date  |");
	     System.out.println("+---------+--------------+-------------+-------------+--------------+");
	
	     while (resultSet.next()) {
	         int bookID = resultSet.getInt("book_id");
	         String title = resultSet.getString("title");
	         String author = resultSet.getString("author");
	         Date issueDate = resultSet.getDate("issue_date");
	         Date returnDate = resultSet.getDate("return_date");
	         
	         String formattedOutput = String.format("| %-7d | %-12s | %-11s | %-11s | %-12s |%n", bookID, title, author, issueDate, returnDate);
	         System.out.print(formattedOutput);
	     }
//	     System.out.println("+---------+--------------+-------------+-------------+--------------+");
	 }
	     catch (SQLException e) {
	            e.printStackTrace();
	        }

	    	
	    	try (PreparedStatement statement2 = connection.prepareStatement(query2)) {
	            statement2.setInt(1, userID);
	            ResultSet resultSet2 = statement2.executeQuery();
	            System.out.println("+--------------------------------------------------------------------------+");
	            System.out.println("|                       MAGAZINES HISTORY                                  |");
	            System.out.println("+--------------------------------------------------------------------------+");
	            System.out.println("| Magazine ID | Title        | Publisher      | Issue Date  | Return Date  |");
	            System.out.println("+-------------+--------------+----------------+-------------+--------------+");

	            while (resultSet2.next()) {
	                int magazineID = resultSet2.getInt("magazine_id");
	                String title = resultSet2.getString("title");
	                String publisher = resultSet2.getString("publisher");
	                Date issueDate = resultSet2.getDate("issue_date");
	                Date returnDate = resultSet2.getDate("return_date");
	                
	                String formattedOutput = String.format("| %-11d | %-12s | %-14s | %-11s | %-11s  |%n", magazineID, title, publisher, issueDate, returnDate);
	                System.out.print(formattedOutput);
	            }
	            System.out.println("+------------+---------------+----------------+-------------+--------------+");
     
     
     
     
     
     
 } catch (SQLException e) {
     e.printStackTrace();
 }
    	
    

    }
}
