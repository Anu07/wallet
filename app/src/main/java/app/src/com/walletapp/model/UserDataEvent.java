package app.src.com.walletapp.model;

/**
 * Created by SONY on 4/20/2018.
 */

public class UserDataEvent {

    String name,phone,email,pwd;

    public UserDataEvent(String name, String phone, String email, String pwd) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.pwd = pwd;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
