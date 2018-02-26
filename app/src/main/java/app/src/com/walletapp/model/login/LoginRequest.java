package app.src.com.walletapp.model.login;

/**
 * Created by Anu Bhalla on 2/21/18.
 */


public class LoginRequest {
    String username;
    String password;
    String device_type;
    String device_id;
    String device_token;
    String timezone;


    public LoginRequest(String mUsername, String mPasswd, String mDevicetype, String mDeviceId, String mDeviceToken, String mTimezone) {
        this.username = mUsername;
        this.password = mPasswd;
        this.device_type = mDevicetype;
        this.device_id = mDeviceId;
        this.device_token = mDeviceToken;
        this.timezone = mTimezone;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
