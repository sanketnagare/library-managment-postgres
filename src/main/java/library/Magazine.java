package library;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class Magazine {

	private int magazine_id;
    private String title;
    private String publisher;
    private boolean availability;

    public Magazine(String title, String author,boolean availability) {
        this.setMagazine_id(magazine_id);
        this.setTitle(title);
        this.setPublisher(author);
        this.setAvailability(availability);
    }

    
    public static void addMagazine(Connection connection, Magazine magazine) {
		
		String newMagazineQuery = "insert into magazines(title, publisher, availability) values (? , ?, ?)";
		
		try(PreparedStatement newMagazineStatement = connection.prepareStatement(newMagazineQuery)) {
			
			newMagazineStatement.setString(1, magazine.getTitle());
			newMagazineStatement.setString(2, magazine.getPublisher());
			newMagazineStatement.setBoolean(3, magazine.isAvailability());
			
			int affectedRows = newMagazineStatement.executeUpdate();
			
			if(affectedRows > 0) {
				System.out.println("Magazine Added Successfully");
			}
			else {
				System.out.println("Failed to add new Magazine.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error while adding the Magazine.");
		}
		
		
	}

    public static void showAvailableMagazines(Connection connection) {
        String query = "SELECT * FROM magazines WHERE availability = true order by magazine_id ";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            System.out.println("+-------------+---------------------+----------------------+");
            System.out.println("| Magazine ID | Title               | Publisher            |");
            System.out.println("+-------------+---------------------+----------------------+");

            while (resultSet.next()) {
                int magazineID = resultSet.getInt("magazine_id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("publisher");

                String formattedOutput = String.format("| %-11d | %-19s | %-20s |%n", magazineID, title, author);
                System.out.print(formattedOutput);
            }

            System.out.println("+-------------+---------------------+----------------------+");  
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void issueMagazine(Connection connection, int userID, int magazineID) {
        
//    	for student
    	int maxMagazineAllowed = 1;
		int maxTotalItemsAllowed = 2;
		
		if(Validation.isTeacher(connection, userID)) {
//			for teacher
			maxMagazineAllowed = 2;
			maxTotalItemsAllowed=6;
		}
		
		if(Validation.hasExceededMagazinesLimit(connection, userID, maxMagazineAllowed) ||
			Validation.hasExceededTotalItemsLimit(connection, userID, maxTotalItemsAllowed)) {
			System.out.println("You have reached maximum limit to issue..");
			return;
		}
		
		
        // Check if the Magazine is available return true or false
        String availabilityQuery = "SELECT availability FROM magazines WHERE magazine_id = ?";
        
        try (PreparedStatement availabilityStatement = connection.prepareStatement(availabilityQuery)) {
        	
            availabilityStatement.setInt(1, magazineID);
            ResultSet availabilityResult = availabilityStatement.executeQuery();

            if (availabilityResult.next() && availabilityResult.getBoolean("availability")) {
                // Update the Magazines availability to false
                String updateQuery = "UPDATE magazines SET availability = false WHERE magazine_id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setInt(1, magazineID);
                    updateStatement.executeUpdate();
                }

                // Insert the record into issued_items_magazines
                String insertQuery = "INSERT INTO issue_items_magazines (member_id, magazine_id, issue_date) VALUES (?, ?, ?)";
                Date issue_date = new Date(Calendar.getInstance().getTime().getTime());

                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, userID);
                    insertStatement.setInt(2, magazineID);
                    insertStatement.setDate(3,  issue_date);
                    insertStatement.executeUpdate();
                }

                // Update the count of issued magazines for the member
                String countUpdateQuery ="UPDATE members SET total_issued = total_issued + 1, issued_magazines_count = issued_magazines_count + 1 WHERE member_id = ?";

                try (PreparedStatement countUpdateStatement = connection.prepareStatement(countUpdateQuery)) {
                    countUpdateStatement.setInt(1, userID);
                    countUpdateStatement.executeUpdate();
                }

                System.out.println("Magazine issued successfully.");
            } else {
                System.out.println("The selected Magazine is not available.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void returnMagazine(Connection connection, int userID, int magazineID) {
        String issuedMagazineQuery = "select issue_magazine_id from issue_items_magazines where member_id=? and magazine_id= ?;";
        
        try(PreparedStatement issuedMagazineStatement = connection.prepareStatement(issuedMagazineQuery)){
        	
        	issuedMagazineStatement.setInt(1, userID);
        	issuedMagazineStatement.setInt(2, magazineID);
        	
        	ResultSet issuedMagazineResultSet = issuedMagazineStatement.executeQuery();
        	
 	
    		if(issuedMagazineResultSet.next()) {
    			
        		int issuedMagazineID = issuedMagazineResultSet.getInt("issue_magazine_id");
        	       
//        		update magazine to available to other users
        		String updateQuery = "update magazines set availability = true where magazine_id = ?";
        		try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)){
        			updateStatement.setInt(1, magazineID);
        			updateStatement.executeUpdate();
				} 
        		
//        		update return date in issue_items_magazines table
        		String returnDateMagazine = "update issue_items_magazines set return_date = ? where issue_magazine_id = ?";
        		Date return_date = new Date(Calendar.getInstance().getTime().getTime());
        		try(PreparedStatement returnDateStatement = connection.prepareStatement(returnDateMagazine)) {
        			returnDateStatement.setDate(1, return_date);
        			returnDateStatement.setInt(2, issuedMagazineID);
        			returnDateStatement.executeUpdate();
        		}
        		
//        		update count of the magazines and total
        		String countUpdateString = "UPDATE members SET total_issued = total_issued - 1, issued_magazines_count = issued_magazines_count - 1 WHERE member_id = ?";
        		try(PreparedStatement countUpdateStatement = connection.prepareStatement(countUpdateString)){
        			countUpdateStatement.setInt(1, userID);
        			countUpdateStatement.executeUpdate();
        		}
        		
        		
        		System.out.println("Magazine Returned Successfully.");
        	}
        	else {
        		System.out.println("The selected magazine in not issued to you.");
			}
        	
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    
//    getters and setters
	public int getMagazine_id() {
		return magazine_id;
	}

	public void setMagazine_id(int magazine_id) {
		this.magazine_id = magazine_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public boolean isAvailability() {
		return availability;
	}

	public void setAvailability(boolean availability) {
		this.availability = availability;
	}
}
