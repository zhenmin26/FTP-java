package server;

import java.util.HashMap;
import java.util.Map;

public class Repository {
    public static String validUser = "admin";
    public static String validPassword = "admin";
    public static boolean userLoginStatus = false;

    private Map<String, String> userDirectory = new HashMap<String, String>();

    public void setUserDirtory(Map userDirtory) {
        this.userDirectory = userDirtory;
    }

    public Map getUserDirtory() {
        return userDirectory;
    }
}
