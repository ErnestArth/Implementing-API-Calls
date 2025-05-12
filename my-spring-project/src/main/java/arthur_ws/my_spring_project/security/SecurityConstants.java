package arthur_ws.my_spring_project.security;

import arthur_ws.my_spring_project.SpringApplicationContext;
import org.springframework.core.env.Environment;

public class SecurityConstants {

    public static final long Expiration_Time_In_Seconds = 864000000;    // 10 days
    public static final String Token_Prefix = "Bearer ";
    public static final String Token_Header = "Authorization";
    public static final String Sign_Up_URL = "/users";
    public static final String Token_Secret = "bvgshg73hue7739349nfewywfw9wldsa73waada13948uewjew2d4f5z0s6xv";
    public static final String Verification_Email_URL = "/users/email-verification";

    public  static String getTokenSecret() {
        Environment environment = (Environment) SpringApplicationContext.getBean("environment");
        return environment.getProperty("tokenSecret");
    }

}