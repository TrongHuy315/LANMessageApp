package src.client;

import java.io.PrintWriter;

import config.Config;
import src.database.UserDAO;
import src.security.DeviceUtils;

public class AuthenticationClient {
    private String username;

    PrintWriter out;

    protected AuthenticationClient(String name, PrintWriter out) {
        this.out = out;
        this.username = null;

        String macAddress = DeviceUtils.getMacAddress();

        if (!UserDAO.validateLogin(name, macAddress)) {
            try {
                UserDAO.registerUser(name, macAddress);
            } catch (RuntimeException e) {
                out.println(Config.get("security.status.account.exist")); // tranh trung voi tin nhan user gui
                return;
            }
        }

        this.username = name;
    }

    public String getUsername() {
        return this.username;
    }
}
