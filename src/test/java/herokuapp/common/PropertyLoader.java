package herokuapp.common;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author pratibhas
 */
public final class PropertyLoader {
	public static String HEROKUAPP_AUTH_USER_LOGIN = "";
	public static String HEROKUAPP_AUTH_USER_REGISTER = "";
	public static String HEROKUAPP_LEADERBOARD_V1_USER = "";
	public static String AUTH0_KEY = "";
	public static String AUTH0_EMAILADDRESS = "";
	public static String AUTH0_GENERATE_TOKEN = "";
	public static String AUTH0_VERIFY_TOKEN = "";
	public static String AUTH0_TOKEN = "";
	public static String EXEC_ENVIRONMENT = "";
	private static Logger log = Logger.getLogger(PropertyLoader.class.getName());


	static {
		//Get the execution environment from a system variable
		EXEC_ENVIRONMENT = System.getProperty("env", "local").toLowerCase();
		log.info("Environment: " + EXEC_ENVIRONMENT);

		Properties properties;
		try {
			InputStream is = PropertyLoader.class.getClassLoader().getResourceAsStream("application.properties");
			properties = new Properties();
			properties.load(is);
			is.close();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}

		/* common headers */
		HEROKUAPP_AUTH_USER_LOGIN = properties.getProperty("herokuapp.auth.user.login");
		HEROKUAPP_AUTH_USER_REGISTER = properties.getProperty("herokuapp.auth.user.register");
		HEROKUAPP_LEADERBOARD_V1_USER = properties.getProperty("herokuapp.leaderboard.v1.user");
		AUTH0_KEY = properties.getProperty("auth0.key");
		AUTH0_EMAILADDRESS = properties.getProperty("auth0.emailaddress");
		AUTH0_GENERATE_TOKEN = properties.getProperty("auth0.gentoken");
		AUTH0_VERIFY_TOKEN = properties.getProperty("auth0.verifytoken");
		AUTH0_TOKEN = properties.getProperty("auth0.token");
    }
}
