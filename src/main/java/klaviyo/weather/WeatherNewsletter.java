package klaviyo.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import klaviyo.email.HtmlEmail;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
 * 
 * @author Justin Huang
 * This class takes in a city and state and caches the resulting data from 
 * weatherbit.io.
 * Take that cached data, it is able to send the email out with the corresponding information
 *
 */
public class WeatherNewsletter {
	//please fill out this part with your own key.
	private final String KEY = "64c67fbeeecd4c9db0ba05a330a286b8";
	private JSONObject currData;
	private String emailAddress;
	//name of city
	private String c;
	//name of state
	private String s;
	/**
	 * 
	 * @param location
	 * location is a single string containing the city and state
	 * i.e. "Boston,MA"
	 * This was used only for testing purposes to make sure the api calls were working
	 */
	protected WeatherNewsletter(String location) {

		StringBuilder url = new StringBuilder();
		url.append("https://api.weatherbit.io/v2.0/current?city=");
		url.append(location);
		url.append("&units=I");
		url.append("&key=");
		url.append(KEY);
		
		try {
			System.out.println(readJsonFromUrl(url.toString()));
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param city
	 * @param state
	 * This constructor takes in a city and state separately, then combines to strings to make a call to the API
	 * This constructor is also only used for testing purposes
	 */
	protected WeatherNewsletter(String city, String state) {
		StringBuilder url = new StringBuilder();
		url.append("https://api.weatherbit.io/v2.0/current?city=");
		//split the city by empty spaces
		String[] parts = city.split(" ");
		//spaces in cities need to be replaced by %20
		for (int i = 0; i<parts.length; i++) {
			url.append(parts[i]);
			  if (i!=parts.length-1) {
				  url.append("%20");
			  }
		}
		url.append(",");
		url.append(state);
		//"units=I" makes sure that data is returned in fahrenheit
		url.append("&units=I");
		url.append("&key=");
		url.append(KEY);
	
		try {
			//gets the data from the api call 
			currData = readJsonFromUrl(url.toString());
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	/**
	 * 
	 * @param email
	 * @param city
	 * @param state
	 * 
	 * Same as constructor above except there's an extra argument for the email
	 */
	public WeatherNewsletter(String email, String city, String state) {
		s = state;
		c = city;
		emailAddress = email;
		StringBuilder url = new StringBuilder();
		url.append("https://api.weatherbit.io/v2.0/current?city=");
		String[] parts = city.split(" ");
		
		for (int i = 0; i<parts.length; i++) {
			url.append(parts[i]);
			  if (i!=parts.length-1) {
				  url.append("%20");
			  }
		}
		url.append(",");
		url.append(state);
		url.append("&units=I");
		url.append("&key=");
		url.append(KEY);
	
		
		try {
			currData = readJsonFromUrl(url.toString());
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Returns the average temperature of the last week at the given location
	 * by using historical daily weather data
	 * @return
	 */
	protected int getAverageTemp() {
		StringBuilder part1 = new StringBuilder();
		part1.append("https://api.weatherbit.io/v2.0/history/daily?city=");
		String[] parts = c.split(" ");
		//the empty spaces in a city string need to be replaced by "%20"
		for (int i = 0; i<parts.length; i++) {
			part1.append(parts[i]);
			  if (i!=parts.length-1) {
				  part1.append("%20");
			  }
		}
		part1.append(",");
		part1.append(s);
		part1.append("&units=I&");
		//changes the date time format for the API call
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//splits the date string to not include time data
		String date = currData.getJSONArray("data").getJSONObject(0).getString("ob_time").split(" ")[0];
		
		LocalDate localDate = LocalDate.parse(date, dtf);
		int total = 0;
		//gets data for the last week
		//free API key only allows for end_date and start_date to be within one date
		//hence we need to call the API 7 times to get the average over the last week
		for (int i = 0; i<7; i++) {
			StringBuilder curr = new StringBuilder();
			curr.append(part1.toString());
			
			curr.append("end_date=");
			curr.append(dtf.format(localDate));
			//subtracts the date by 1
			localDate = localDate.minusDays(1);
			curr.append("&start_date=");
			curr.append(dtf.format(localDate));
			curr.append("&key=");
			curr.append(KEY);
			try {
				JSONObject currObject = readJsonFromUrl(curr.toString());
				//gets the temperature of the API call and adds it to the total
				total +=getHistoricalData(currObject);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		//divides the total by 7 to get the average
		return total/7;
	}
	/**
	 * takes a JSONObject from an api call to get the temp of a previous day
	 * @param obj
	 * @return
	 */
	protected int getHistoricalData(JSONObject obj) {
		return obj.getJSONArray("data").getJSONObject(0).getInt("temp");
	}
	private String getSubject() {
		JSONObject data = currData.getJSONArray("data").getJSONObject(0);
		int temp = data.getInt("temp");
		int description = data.getJSONObject("weather").getInt("code");
		int avgTemp = getAverageTemp();
		//if the weather code is between 800 and 802 (i.e. it's sunny)
		//or the temperature is at least 5 degrees cooler than average
		if ((description<803 && description>=800)|| temp - avgTemp>=5) {
			return "It's nice out! Enjoy a discount on us.";
		}
		// //if the weather code is anything less than 800 and 804 (raining, snowing, fog, etc.)
		//or the 
		if ((description<800 && description>804)|| temp-avgTemp <= -5) {
			return "Not so nice out? That's okay, enjoy a discount on us.";
		}
		return "Enjoy a discount on us";
	}
	/**
	 * builds the html email
	 * @return
	 */
	private String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html>\r\n" + 
				"  <head>\r\n" + 
				"    <meta charset=\"utf-8\">\r\n" + 
				"    <title>Weather Newsletter</title>\r\n" + 
				"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n" + 
				"  </head>\r\n" + 
				"\r\n" + 
				"<body>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"  <div class=\"container\">\r\n" + 
				"    <h1>Your Weather Newsletter for ");

		String datetime = currData.getJSONArray("data").getJSONObject(0).getString("datetime");
		String [] split = datetime.split("-");
		String[] split2 = split[2].split(":");
		builder.append(new DateFormatSymbols().getMonths()[Integer.parseInt(split[1])-1]);
		builder.append(" ");
		builder.append(split2[0]);
		builder.append(", ");
		builder.append(split[0]);
		builder.append("</h1>\r\n" + 
				"    \r\n" + 
				"    <h2>Current Weather in ");
		builder.append(c);
		builder.append(", ");
		builder.append(s);
		builder.append(": ");
		builder.append(currData.getJSONArray("data").getJSONObject(0).getInt("temp"));
		builder.append("°F, ");
		builder.append(currData.getJSONArray("data").getJSONObject(0).getJSONObject("weather").getString("description"));
		builder.append("</h2>\r\n" + 
				"    <p>Have a nice day!</p>\r\n" + 
				"    <p>Sincerely,</p>\r\n" + 
				"    \r\n" + 
				"    <p>The Weather Newsletter Team</p>\r\n" + 
				"    \r\n" + 
				"    </div>\r\n" + 
				"  </div>\r\n" + 
				"\r\n" + 
				"</body>\r\n" + 
				" </html>");
	
		return builder.toString();
	}
	/**
	 * sends the email
	 * @return
	 */
	public boolean send() {
		List<String> list = new ArrayList<>();
		list.add(emailAddress);
		HtmlEmail email = new HtmlEmail(getMessage(),getSubject(),list);
		return email.sendEmail();
	}
	private  String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
	/**
	 * returns the JSON data from a url call
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }
}
