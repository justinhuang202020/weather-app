package database;

import java.nio.file.Paths;

import org.junit.Test;

import database.WeatherDatabase;

public class DatabaseTest {
/**
 * Some basic tests on whether the database is working
 */
	@Test
	public void test() {
		String url = "jdbc:sqlite:" + Paths.get("")+"weatherapp.db";
		WeatherDatabase db = new WeatherDatabase(url);
		assert(db.citiesTableFilled());
		assert(db.containsCity("Boston", "MA"));
		assert(!db.containsCity("Boston", "RI"));
//		
	}

}
