package klaviyo.driver;

import java.io.File;
import java.io.IOException;

import java.nio.file.Paths;

import java.util.Map;


import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;


import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import klaviyo.database.WeatherDatabase;
import klaviyo.weather.WeatherNewsletter;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 *
 * @author Justin Huang
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();
  private static WeatherDatabase db;

  /**
   * The initial method called when execution begins.
   *
   * @param args
   *          An array of command line arguments
   */
  public static void main(String[] args) {
	  
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {

    this.args = args;
  }

  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    runSparkServer((int) options.valueOf("port"));
    //path to weatherapp database
    String url = "jdbc:sqlite:" + Paths.get("")+"weatherapp.db";
	db = new WeatherDatabase(url);  
   
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/signup", new FrontHandler(), freeMarker);
    Spark.post("/register", new SignUpHandler());
    Spark.post("/cityInfo", new CityInfoHandler());
    
  }

  /**
   * Handle requests to the signup page and renders the html
   *
   * @author Justin Huang
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
    	
      return new ModelAndView(null, "homepage.ftl");
    }
  }
  /**
   * 
   * @author Justin Huang
   * This class handles signing up after a user submits the form
   *
   */
private static class SignUpHandler implements Route {
	@Override
	public String handle(Request req, Response res) {
		  QueryParamsMap qm = req.queryMap();
		  String email = qm.value("email");
		  String city = qm.value("city");
		  String state = qm.value("state");
		  String errorMessage = "";
		  //variable tracking if signup is successful
		  boolean success = true;
		  //if the user exists
		  if (db.userExists(email)) {
			  errorMessage = "user exists";
			  success = false;
		  }
		  //if the city is invalid
		  if (!db.containsCity(city, state)) {
			  success = false;
			  errorMessage = "invalid city";
		  }
		  //if the first two conditions are passed
		  if (success) {
			  //insert the new user in the database
			  success = db.updateUser(email, city, state);
		  }
		 //if the database transaction is successful
		  if (success) {
			  //create a new weather newsletter
			  WeatherNewsletter curr = new WeatherNewsletter(email, city, state);
			  //if sending the newsletter is unsuccessful
			  if (!curr.send()) {
				  success = !success;
				  errorMessage = "email failed to send";
				  //delete the user from the database
				  db.deleteUser(email);
				  
			  }
		  }
		 
		  Map<String, Object> variables =
		          ImmutableMap.of("success", success, "errorMessage", errorMessage);
		  return GSON.toJson(variables);
		    
		}
	}
/**
 * 
 * @author Justin Huang
 * This class simply sends over a list of the top 100 cities to the front end
 */
	private static class CityInfoHandler implements Route {
		@Override
		public String handle(Request req, Response res) {
			  Map<String, Object> variables =
			          ImmutableMap.of("list", db.getAllLocations());
			  return GSON.toJson(variables);
		}
	}

}
