package app.src.com.walletapp.wifip2p.wifi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.OfflineEvent;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.src.com.walletapp.wifip2p.wifi.WiFiDirectActivity.TAG;

/**
 * Created by SONY on 3/23/2018.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.submit_bttn)
    Button submitBttn;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.textView2)
    Button textView2;
    private JSONObject LoginJson;
    String URL = "http://samepay.net/SamePayWebService.asmx?WSDL";
    String NAMESPACE = "http://samepay.net/";
    String METHOD_NAME = "Login";
    String SOAP_ACTION = NAMESPACE + METHOD_NAME;

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Subscribe
    public void onOfflineEvent(OfflineEvent event) {
        Log.i(TAG, "onOfflineEvent: ");
    }

    @OnClick(R.id.submit_bttn)
    public void onViewClicked() {
        if (validate()) {
            new LoginAsync().execute();
        }
    }

    public boolean validate() {
        if (phone.getText().toString().isEmpty()) {
            Toast.makeText(this, "Phone can't be left empty.", Toast.LENGTH_LONG).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, "Password can't be left empty.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.textView2)
    public void onRegisterViewClicked() {
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }


    private class LoginAsync extends AsyncTask<String, String, String> {

        private String response;
        BufferedReader reader;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
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
                        "    <ns1:Login>\n" +
                        "      <ns1:Phone>" + phone.getText().toString().trim() + "</ns1:Phone>\n" +
                        "      <ns1:password>" + password.getText().toString().trim() + "</ns1:password>\n" +
                        "    </ns1:Login>\n" +
                        "  </SOAP-ENV:Body>\n" +
                        "</SOAP-ENV:Envelope>";
                try {
                    URL url = new URL(URL);
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
                JSONObject job2 = job1.getJSONObject("LoginResponse");
                String msg = job2.getString("LoginResult");
                if (!msg.equalsIgnoreCase("Login Failed")) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    SharedPreferencesHandler.setStringValues(LoginActivity.this,"loginId",msg);
                    startActivity(new Intent(LoginActivity.this, WiFiDirectActivity.class));
                    finish();
                }else if(msg.equalsIgnoreCase("Login Failed")){
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }
}
