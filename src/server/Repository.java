package server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Repository {
    public static String validUser = "admin";
    public static String validPassword = "admin";
    public static boolean userLoginStatus = false;

    // file path
    public static String rootDir;
    public static String currentDir;
    final static String fileSeperator = File.separator;

    private Map<String, String> userDirectory = new HashMap<String, String>();

    public void setUserDirectory(Map userDirtory) {
        this.userDirectory = userDirtory;
    }

    public Map getUserDirectory() {
        return userDirectory;
    }
}
