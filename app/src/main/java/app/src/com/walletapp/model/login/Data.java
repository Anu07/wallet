
package app.src.com.walletapp.model.login;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Data {

    @SerializedName("current_time")
    private String mCurrentTime;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("id")
    private String mId;
    @SerializedName("last_login")
    private String mLastLogin;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("token")
    private Long mToken;
    @SerializedName("username")
    private String mUsername;

    public String getCurrentTime() {
        return mCurrentTime;
    }

    public void setCurrentTime(String currentTime) {
        mCurrentTime = currentTime;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getLastLogin() {
        return mLastLogin;
    }

    public void setLastLogin(String lastLogin) {
        mLastLogin = lastLogin;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public Long getToken() {
        return mToken;
    }

    public void setToken(Long token) {
        mToken = token;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

}
