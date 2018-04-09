package app.src.com.walletapp.model.register;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

/**
 * Created by SONY on 3/22/2018.
 */
@Root(name = "soap12:Envelope")
@NamespaceList({
        @Namespace( prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
        @Namespace( prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
        @Namespace( prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
})
public class RegisterRequestEnvelope {

        @Element(name = "soap12:Body", required = false)
        private RegisterRequestBody body;

        public RegisterRequestBody getBody() {
            return body;
        }

        public void setBody(RegisterRequestBody body) {
            this.body = body;
        }
    }
