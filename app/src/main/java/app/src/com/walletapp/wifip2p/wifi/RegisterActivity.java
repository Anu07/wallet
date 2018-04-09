package app.src.com.walletapp.wifip2p.wifi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import app.src.com.walletapp.R;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by SONY on 3/22/2018.
 */

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.fullname)
    EditText fullname;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    private JSONObject LoginJson;
    String URL = "http://samepay.net/SamePayWebService.asmx?WSDL";
    String NAMESPACE = "http://samepay.net/";
    String METHOD_NAME = "Register_u";
    String SOAP_ACTION = NAMESPACE + METHOD_NAME;
    String PARAMETER_FN = "fullname";
    String PARAMETER_PHONE = "Phone";
    String PARAMETER_EMAIL = "email";
    String PARAMETER_PWD = "password";
    String PARAMETER_DID = "device_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.submit_bttn)
    public void onViewClicked() {
        if(validate()){
            new LoginAsync().execute();
        }
    }

    private boolean validate() {
        if(fullname.getText().toString().isEmpty()){
            Toast.makeText(this,"Fullname can't be left empty.",Toast.LENGTH_LONG).show();
            return false;
        }else  if(phone.getText().toString().isEmpty()){
            Toast.makeText(this,"Phone can't be left empty.",Toast.LENGTH_LONG).show();
            return false;
        }else if(email.getText().toString().isEmpty()){
            Toast.makeText(this,"Email can't be left empty.",Toast.LENGTH_LONG).show();
            return false;
        }else if(!isValidEmail(email.getText().toString())){
            Toast.makeText(this,"Invalid email",Toast.LENGTH_LONG).show();
            return false;
        }else if(password.getText().toString().isEmpty()){
            Toast.makeText(this,"Password can't be left empty.",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private class LoginAsync extends AsyncTask<String, String, String> {

        private String response;
        BufferedReader reader;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                //paste your request structure here as the String body(copy it exactly as it is in soap ui)
                //assuming that this is your request body
//                String body = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:hs=\"http://tempuri.org/\">\n <soapenv:Body>\n <hs:" + METHOD_NAME + ">\n <hs:" + PARAMETER_FN + ">" + "Mansa Info" + "</hs:" + PARAMETER_FN + ">\n <hs:" + PARAMETER_PHONE + ">" + "98888988888" + "</hs:" + PARAMETER_PHONE + ">\n <hs:" + PARAMETER_EMAIL + ">" + "demo@demo.com" + "</hs:" + PARAMETER_EMAIL + ">\n  <hs:" + PARAMETER_PWD + ">" + "12345" + "</hs:" + PARAMETER_PWD + ">\n <hs:" + PARAMETER_DID + ">" + "android12345" + "</hs:" + PARAMETER_DID + ">\n </hs:" + METHOD_NAME + ">\n </soapenv:Body>\n</soapenv:Envelope>";

                String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://samepay.net/\">\n" +
                        "  <SOAP-ENV:Body>\n" +
                        "    <ns1:Register_u>\n" +
                        "      <ns1:fullname>"+fullname.getText().toString().trim()+"</ns1:fullname>\n" +
                        "      <ns1:Phone>"+phone.getText().toString().trim()+"</ns1:Phone>\n" +
                        "      <ns1:email>"+email.getText().toString().trim()+"</ns1:email>\n" +
                        "      <ns1:password>"+password.getText().toString().trim()+"</ns1:password>\n" +
                        "      <ns1:device_id>"+ SharedPreferencesHandler.getStringValues(RegisterActivity.this,"DeviceToken")+"</ns1:device_id>\n" +
                        "    </ns1:Register_u>\n" +
                        "  </SOAP-ENV:Body>\n" +
                        "</SOAP-ENV:Envelope>";
                try {
                    java.net.URL url = new URL(URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDefaultUseCaches(false);
                    conn.setRequestProperty("Content-Type", "text/xml");
                    conn.setRequestProperty("SOAPAction", SOAP_ACTION);
                    //push the request to the server address
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(body);
                    wr.flush();

                    //get the server response
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                        response = builder.toString();//this is the response, parse it in onPostExecute
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
            }

            return response;
        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(String result) {
            try {
                progressDialog.cancel();
                Log.e("Response======", result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                LoginJson = XML.toJSONObject(result);
                Log.e("XML", result);
                Log.e("JSON", LoginJson.toString());
                JSONObject job = LoginJson.getJSONObject("soap:Envelope");
                JSONObject job1 = job.getJSONObject("soap:Body");
                JSONObject job2 = job1.getJSONObject("Register_uResponse");
                String msg = job2.getString("Register_uResult");
                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                SharedPreferencesHandler.setStringValues(RegisterActivity.this,"user","1");
                startActivity(new Intent(RegisterActivity.this, WiFiDirectActivity.class));
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }
}
