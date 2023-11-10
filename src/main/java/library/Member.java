package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Member {

public static boolean checkMember(Connection connection, int userID) {
	
	String checkString = "select member_id from members m where member_id = ?";
	try (PreparedStatement checkStatement = connection.prepareStatement(checkString)){
		
		checkStatement.setInt(1, userID);
		
		ResultSet checkSet = checkStatement.executeQuery();
		
		if (checkSet.next()) {
            return true;  // User found in the table
        } else {
            System.out.println("User not found, Enter valid ID again");
            return false; // User not found
        }
	} catch (SQLException e) {
		e.printStackTrace();
		System.out.println("Failed to check member in database..");
		return false;
	}
}
	

public static int addMemeber(Connection connection, String name, String group) {

		String insertSql = "Insert into members (name, type) values (?, ?) returning member_id";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)){
			
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, group);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				int newMemberID = resultSet.getInt("member_id");
				System.out.println("New member added in Database.");
				System.out.println("Your ID is: "+ newMemberID);
				return newMemberID;
			}
			else {
				System.out.println("Failed to add new member..");
				return -1;
			}
			
		} catch (SQLException e) {
			System.out.println("Error while adding new member");
			e.printStackTrace();
			return -1;
		}
		
	}



public void retrieveIssuedBooks(Connection connection) {
    // Method to retrieve issued books for the member
}


}
