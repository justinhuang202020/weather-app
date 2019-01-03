package klaviyo.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Justin Huang
 * Updates and queries the weather database for the weather application.
 */
public class WeatherDatabase {
	private static Connection conn;
	/**
	 * takes in a file location to the database
	 * @param string
	 */
	public WeatherDatabase(String string) {
		try {
		      Class.forName("org.sqlite.JDBC");
		    
		    String urlToDB = string;
		    
		      conn = DriverManager.getConnection(urlToDB);
		      Statement stat = conn.createStatement();
		      stat.executeUpdate("PRAGMA foreign_keys = ON;");
		      String sql = "CREATE TABLE IF NOT EXISTS top_100_cities (\n"
		                + "	id integer PRIMARY KEY,\n"
		                + "	city text NOT NULL,\n"
		                + "	state text NOT NULL\n"
		                + ");";
		      PreparedStatement prep = conn.prepareStatement(sql);
		      prep.executeUpdate();
		      prep.close();
		      sql = "CREATE TABLE IF NOT EXISTS users (\n"
		                + "	email text PRIMARY KEY,\n"
		                + "	city text NOT NULL,\n"
		                + "	state text NOT NULL\n"
		                + ");";
		      prep = conn.prepareStatement(sql);
		      prep.executeUpdate();
		      prep.close();
		      
		    } catch (SQLException | ClassNotFoundException e) {
		      e.printStackTrace();
		    }
		//if the database has just been created, add the top 100 cities
		if (!citiesTableFilled()) {
			updateAllCities();
		}
			
	}
	/**
	 * Adds a user to the users table.
	 * Returns true if successful.
	 * @param email
	 * @param city
	 * @param state
	 * @return
	 */
		public boolean updateUser(String email, String city, String state) {
		if (userExists(email) || !containsCity(city, state)) {
			return false;
		}
		PreparedStatement prep;
		String sql = "Insert into users(email, city, state) VALUES (?,?,?)";
		
		try {
			prep = conn.prepareStatement(sql);
			prep.setString(1, email);
		
		prep.setString(2, city);
		prep.setString(3, state);
		prep.executeUpdate();
		prep.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
		/**
		 * Adds a city and a state to the table top_100_cities.
		 * Returns true if insertion into database is successful.
		 * @param city
		 * @param state
		 * @return
		 */
	private boolean insertCity(String city, String state) {
		String sql = "Insert into top_100_cities(id,city, state) VALUES (?,?,?)";
		PreparedStatement prep;
		try {
		prep = conn.prepareStatement(sql);
		prep.setString(1, null);
		prep.setString(2, city);
		prep.setString(3, state);
		
			prep.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Deletes a user with the corresponding email.
	 * Returns true if delete is successful
	 * @param email
	 * @return
	 */
	public boolean deleteUser(String email) {
		PreparedStatement prep;
		String sql = "DELETE from users where email = ?;";
		try {
			prep = conn.prepareStatement(sql);
			prep.setString(1, email);
			prep.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Checks if the cities table is filled with 100 entries.
	 * Returns true if this is the case.
	 * @return
	 */
	protected boolean citiesTableFilled() {
		String sql = "select Count(*)  from top_100_cities;";
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement(sql);
		
			
			
			ResultSet rs  = prep.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1)!=100) {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * fills in the top_100_cities with the city and state from the 
	 * file "top_100_cities.txt"
	 * returns true if successful
	 * @return
	 */
	private boolean updateAllCities() {
		PreparedStatement prep;
		String sql = "DELETE from top_100_cities;";
		try {
			prep = conn.prepareStatement(sql);
		
			prep.executeUpdate();
			prep.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		File file = new File("./top_100_cities.txt"); 
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			
		   String st; 
		   while ((st = br.readLine()) != null) {
			   //splits the line into the corresponding city and state parts
			   //i.e. Boston,MA
			   String[] arr = st.split(",");
			   if (!insertCity(arr[0], arr[1])) {
				   return false;
			   }  
		   }
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true; 
	}
	/**
	 * Checks if the user exists based on the corresponding email.
	 * Returns true if this is the case.
	 * @param email
	 * @return
	 */
	public boolean userExists(String email) {
		String sql = "Select exists (select * from users where email = ?);";
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement(sql);
		
			prep.setString(1, email);
			
			ResultSet rs  = prep.executeQuery();
			if (rs.next()) {
				if (!rs.getBoolean(1)) {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * returns a list with all the locations (the concatenated city and state strings)
	 * @return
	 */
	public List<String> getAllLocations() {
		List<String> list = new ArrayList<>();
		String sql = "select city, state from top_100_cities;";
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement(sql);
		
			
			ResultSet rs  = prep.executeQuery();
			while (rs.next()) {
				StringBuilder builder = new StringBuilder();
				builder.append(rs.getString(1));
				builder.append(", ");
				builder.append(rs.getString(2));
				list.add(builder.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * Checks if the city and state exists in the top_100_cities table.
	 * Returns true if this is the case.
	 * @param city
	 * @param state
	 * @return
	 */
	public boolean containsCity(String city, String state) {
		String sql = "Select exists (select * from top_100_cities where city = ? and state = ?)";
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement(sql);
		
			prep.setString(1, city);
			prep.setString(2, state);
			
			ResultSet rs  = prep.executeQuery();
			if (rs.next()) {
				if (!rs.getBoolean(1)) {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	

}
