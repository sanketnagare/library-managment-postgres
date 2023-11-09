package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Validation {

	//  to check if the member is teacher or not
	 public static boolean isTeacher(Connection connection, int userID) {
	  	
	  	String typeQuery = "select type from members where member_id = ?";
	  	
	  	try(PreparedStatement typeStatement = connection.prepareStatement(typeQuery)) {
				
	  		typeStatement.setInt(1, userID);
	  		
	  		ResultSet typeResult = typeStatement.executeQuery();
	  		
	  		return typeResult.next() && typeResult.getString("type").equals("teacher");	
			} 
	  	catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			
	  }
	  
	  
	//  to check if book limit is exceeded or not
	  public static boolean hasExceededBooksLimit(Connection connection, int userID, int maxBooksAllowed) {
	  	
	  	String bookLimitQuery = "select issued_books_count from members where member_id = ?";
	  	
	  	try (PreparedStatement bookCountStatement = connection.prepareStatement(bookLimitQuery)){
	  		
	  		bookCountStatement.setInt(1, userID);
	  		
	  		ResultSet bookLimitResultSet = bookCountStatement.executeQuery();
	  		
	  		return bookLimitResultSet.next() && bookLimitResultSet.getInt("issued_books_count") >= maxBooksAllowed;
				
			} 
	  	catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
	  }
	  
	  
//	  to check if magazine limit is exceed or not
	  public static boolean hasExceededMagazinesLimit(Connection connection, int UserID,int maxMagazinesAllowed) {
		  
		  String magazineLimitQueryString = "select issued_magazines_count from members where member_id = ?";
		  
		try (PreparedStatement magazineCountStatement = connection.prepareStatement(magazineLimitQueryString)){
			
			magazineCountStatement.setInt(1, UserID);
			
			ResultSet magazineLimitResultSet = magazineCountStatement.executeQuery();
			
			return magazineLimitResultSet.next() && magazineLimitResultSet.getInt("issued_magazines_count") >= maxMagazinesAllowed;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	  }
	  
	  
//	  to check id total limit is exceed or not
	  public static boolean hasExceededTotalItemsLimit(Connection connection, int userID, int maxTotalItemsAllowed) {
	  	
	  	String totalLimitQuery = "select total_issued from members where member_id = ?";
	  	
	  	try(PreparedStatement totalCountStatement = connection.prepareStatement(totalLimitQuery)) {
	  		
	  		totalCountStatement.setInt(1, userID);
	  		
	  		ResultSet totalLimitResultSet = totalCountStatement.executeQuery();
	  		
	  		return totalLimitResultSet.next() && totalLimitResultSet.getInt("total_issued") >= maxTotalItemsAllowed;
				
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	  }
	  
	
}
