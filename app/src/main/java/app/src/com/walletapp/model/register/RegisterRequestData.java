package app.src.com.walletapp.model.register;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/**
 * Created by SONY on 3/22/2018.
 */

@Root(name = "Register_u", strict = false)
@Namespace(reference = "http://samepay.net/")
public class RegisterRequestData {

        @Element(name = "fullname", required = false)
        private String fullname;

        @Element(name = "Phone", required = false)
        private String Phone;

        @Element(name = "email", required = false)
        private String email;

        @Element(name = "password", required = false)
        private String password;

        @Element(name = "device_id", required = false)
        private String device_id;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
