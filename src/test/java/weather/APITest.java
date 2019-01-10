package weather;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class APITest {
/**
 * Tests if reading from the file top_100_cities.txt and feeding the string of each 
 * city into the constructor would give the correct call to the weatherbit.io API and not 
 * throw errors.
 */
//	@Test
//	public void testValidFile() throws IOException {
//		Path currentRelativePath = Paths.get("");
//		String s = currentRelativePath.toAbsolutePath().toString();
//		System.out.println("Current relative path is: " + s);
//		File file = new File("./top_100_cities.txt"); 
//		  
//		  BufferedReader br = new BufferedReader(new FileReader(file)); 
//		  
//		  String st; 
//		  while ((st = br.readLine()) != null) {
//			  System.out.println(st);
//			  String[] arr = st.split(" ");
//			  StringBuilder builder = new StringBuilder();
//			  for (int i=0; i<arr.length; i++) {
//				  builder.append(arr[i]);
//				  if (i!=arr.length-1) {
//					  builder.append("%20");
//				  }
//			  }
//			  WeatherNewsletter nl = new WeatherNewsletter(builder.toString());
//		  }
//		  
//	} 
	/**
	 * tests if giving the Weather Newsletter constructor separate city and state arguments would work
	 */
//	@Test
//	public void testWithCityStateSeparateArguments() {
//		WeatherNewsletter nl = new WeatherNewsletter("Boston", "MA");
//	}

}
