package app.src.com.walletapp.model.register;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by SONY on 3/22/2018.
 */
@Root(name = "soap12:Body", strict = false)
public class RegisterRequestBody {

    @Element(name = "Register_u", required = false)
    private RegisterRequestData data;

    public RegisterRequestData getData() {
        return data;
    }

    public void setData(RegisterRequestData data) {
        this.data = data;
    }
}
