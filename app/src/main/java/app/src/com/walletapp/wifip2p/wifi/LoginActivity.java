package app.src.com.walletapp.wifip2p.wifi;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rilixtech.CountryCodePicker;

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
import app.src.com.walletapp.utils.MyProgressDialog;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import app.src.com.walletapp.wifip2p.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.src.com.walletapp.wifip2p.wifi.TransferActivity.TAG;

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
    @BindView(R.id.forgot)
    TextView forgot;
    Dialog dial;
    private JSONObject LoginJson, checkPhoneJson;
    String URL = "http://samepay.net/SamePayWebService.asmx?WSDL";
    String NAMESPACE = "http://samepay.net/";
    String METHOD_NAME = "Login";
    String FORGOT_CHECKPHONE = "CheckMobilePhone";
    String UPDATE_ACTION = "UpatePassword";
    String SOAP_ACTION = NAMESPACE + METHOD_NAME;
    String SOAP_ACTION_CHECKPHONE = NAMESPACE + FORGOT_CHECKPHONE;
    String UPDATE_METHOD_NAME = "UdpateUserstatus";
    String UPDATE_SOAP_ACTION = NAMESPACE + UPDATE_METHOD_NAME;
    private String forgotEmail = "";
    String SOAP_ACTION_UPDATE = NAMESPACE + UPDATE_ACTION;
    private String newPwd;
    private JSONObject updatePwdJson;


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
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.forgot)
    public void onForgotViewClicked() {
        dial = Utils.forgotDialog(LoginActivity.this);
        final EditText cancel = dial.findViewById(R.id.forgot_pwd);
        final EditText pwd = dial.findViewById(R.id.update_pwd);
        final TextInputLayout updateLay = dial.findViewById(R.id.updateLay);
        final CountryCodePicker ccp = dial.findViewById(R.id.ccp);
        Button ok = dial.findViewById(R.id.submitforgot);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateLay.getVisibility() == View.VISIBLE) {
                    if (pwd.getText().toString().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Password field can't be empty", Toast.LENGTH_LONG).show();
                    } else if (pwd.getText().toString().length() < 4) {
                        Toast.makeText(LoginActivity.this, "Password field should be greater than 4 characters.", Toast.LENGTH_LONG).show();
                    } else {
                        newPwd = pwd.getText().toString().trim();
                        Log.i(TAG, "onClick: " + forgotEmail);
                        new UpdatePasswordAsync().execute();
                    }
                } else {
                    if (!cancel.getText().toString().isEmpty()) {
                        forgotEmail = ccp.getSelectedCountryCodeWithPlus() + cancel.getText().toString().trim();
                        Log.i(TAG, "onClick:check " + forgotEmail);
                        new CheckPhoneAsync().execute();
                    } else {
                        Toast.makeText(LoginActivity.this, "Phone number can't be empty", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        dial.show();
        dial.getWindow().setLayout((7 * Utils.getScreenWidth(LoginActivity.this)) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private class LoginAsync extends AsyncTask<String, String, String> {

        private String response;
        BufferedReader reader;
        MyProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = MyProgressDialog.show(LoginActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                //paste your request structure here as the String body(copy it exactly as it is in soap ui)
                //assuming that this is your request body
//                String body = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:hs=\"http://tempuri.org/\">\n <soapenv:Body>\n <hs:" + METHOD_NAME + ">\n <hs:" + PARAMETER_FN + ">" + "Mansa Info" + "</hs:" + PARAMETER_FN + ">\n <hs:" + PARAMETER_PHONE + ">" + "98888988888" + "</hs:" + PARAMETER_PHONE + ">\n <hs:" + PARAMETER_EMAIL + ">" + "demo@demo.com" + "</hs:" + PARAMETER_EMAIL + ">\n  <hs:" + PARAMETER_PWD + ">" + "12345" + "</hs:" + PARAMETER_PWD + ">\n <hs:" + PARAMETER_DID + ">" + "android12345" + "</hs:" + PARAMETER_DID + ">\n </hs:" + METHOD_NAME + ">\n </soapenv:Body>\n</soapenv:Envelope>";

                String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "  <soap:Body>\n" +
                        "    <Login xmlns=\"http://samepay.net/\">\n" +
                        "      <Phone>"+phone.getText().toString().trim()+"</Phone>\n" +
                        "      <password>"+password.getText().toString().trim()+"</password>\n" +
                        "      <device_id>"+SharedPreferencesHandler.getStringValues(LoginActivity.this,"DeviceToken")+"</device_id>\n" +
                        "    </Login>\n" +
                        "  </soap:Body>\n" +
                        "</soap:Envelope>";
                Log.i(TAG, "doInBackground: " + body);
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
            MyProgressDialog.dismiss(progressDialog);
            try {
                LoginJson = XML.toJSONObject(result);
                Log.e("XML", result);
                Log.e("JSON", LoginJson.toString());
                JSONObject job = LoginJson.getJSONObject("soap:Envelope");
                JSONObject job1 = job.getJSONObject("soap:Body");
                JSONObject job2 = job1.getJSONObject("LoginResponse");
                String msg = job2.getString("LoginResult");
//                JSONObject MessageJob=new JSONObject(msg);
                Log.i(TAG, "onPostExecute: "+msg);
                if (msg.contains("Successful")) {
                    new UpdateUserstatusAsync().execute();
                    SharedPreferencesHandler.setStringValues(LoginActivity.this, "usercode",msg);
                } else if(msg.contains("failed")){
                    Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_LONG).show();
                }else {
                    SharedPreferencesHandler.setStringValues(LoginActivity.this, "usercode",msg);
                    new UpdateUserstatusAsync().execute();
                    Toast.makeText(LoginActivity.this, "Updating user status...", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }


    /**
     * Forgot password
     */

    private class CheckPhoneAsync extends AsyncTask<String, String, String> {

        private String response;
        BufferedReader reader;
        MyProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = MyProgressDialog.show(LoginActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                //paste your request structure here as the String body(copy it exactly as it is in soap ui)
                //assuming that this is your request body
//                String body = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:hs=\"http://tempuri.org/\">\n <soapenv:Body>\n <hs:" + METHOD_NAME + ">\n <hs:" + PARAMETER_FN + ">" + "Mansa Info" + "</hs:" + PARAMETER_FN + ">\n <hs:" + PARAMETER_PHONE + ">" + "98888988888" + "</hs:" + PARAMETER_PHONE + ">\n <hs:" + PARAMETER_EMAIL + ">" + "demo@demo.com" + "</hs:" + PARAMETER_EMAIL + ">\n  <hs:" + PARAMETER_PWD + ">" + "12345" + "</hs:" + PARAMETER_PWD + ">\n <hs:" + PARAMETER_DID + ">" + "android12345" + "</hs:" + PARAMETER_DID + ">\n </hs:" + METHOD_NAME + ">\n </soapenv:Body>\n</soapenv:Envelope>";

                String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                        "  <soap12:Body>\n" +
                        "    <CheckMobilePhone xmlns=\"http://samepay.net/\">\n" +
                        "      <phone>" + forgotEmail + "</phone>\n" +
                        "    </CheckMobilePhone>\n" +
                        "  </soap12:Body>\n" +
                        "</soap12:Envelope>";

                Log.i(TAG, "doInBackground: " + body);
                try {
                    URL url = new URL(URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDefaultUseCaches(false);
                    conn.setRequestProperty("Content-Type", "text/xml");
                    conn.setRequestProperty("SOAPAction", SOAP_ACTION_CHECKPHONE);
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
            MyProgressDialog.dismiss(progressDialog);
            try {
                checkPhoneJson = XML.toJSONObject(result);
                Log.e("XML", result);
                Log.e("JSON", checkPhoneJson.toString());
                JSONObject job = checkPhoneJson.getJSONObject("soap:Envelope");
                JSONObject job1 = job.getJSONObject("soap:Body");
                JSONObject job2 = job1.getJSONObject("CheckMobilePhoneResponse");
                String msg = job2.getString("CheckMobilePhoneResult");
                if (msg.equalsIgnoreCase("Invalid User")) {
                    dial.dismiss();
                    Toast.makeText(LoginActivity.this, "User not registered", Toast.LENGTH_LONG).show();
                } else {
                    Log.i(TAG, "onPostExecute: " + msg);
                    SharedPreferencesHandler.setStringValues(LoginActivity.this, "userid", msg);
                    dial.findViewById(R.id.updateLay).setVisibility(View.VISIBLE);
                    dial.findViewById(R.id.countryCodeLayout).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }


    /**
     * Update password
     */

    private class UpdatePasswordAsync extends AsyncTask<String, String, String> {

        private String response;
        BufferedReader reader;
        MyProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = MyProgressDialog.show(LoginActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                //paste your request structure here as the String body(copy it exactly as it is in soap ui)
                //assuming that this is your request body
//                String body = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:hs=\"http://tempuri.org/\">\n <soapenv:Body>\n <hs:" + METHOD_NAME + ">\n <hs:" + PARAMETER_FN + ">" + "Mansa Info" + "</hs:" + PARAMETER_FN + ">\n <hs:" + PARAMETER_PHONE + ">" + "98888988888" + "</hs:" + PARAMETER_PHONE + ">\n <hs:" + PARAMETER_EMAIL + ">" + "demo@demo.com" + "</hs:" + PARAMETER_EMAIL + ">\n  <hs:" + PARAMETER_PWD + ">" + "12345" + "</hs:" + PARAMETER_PWD + ">\n <hs:" + PARAMETER_DID + ">" + "android12345" + "</hs:" + PARAMETER_DID + ">\n </hs:" + METHOD_NAME + ">\n </soapenv:Body>\n</soapenv:Envelope>";

                String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                        "  <soap12:Body>\n" +
                        "    <UpatePassword xmlns=\"http://samepay.net/\">\n" +
                        "      <userid>" + SharedPreferencesHandler.getSharedPreferences(LoginActivity.this).getString("userid", "") + "</userid>\n" +
                        "      <password>" + newPwd + "</password>\n" +
                        "    </UpatePassword>\n" +
                        "  </soap12:Body>\n" +
                        "</soap12:Envelope>";

                Log.i(TAG, "doInBackground: " + body);
                try {
                    URL url = new URL(URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDefaultUseCaches(false);
                    conn.setRequestProperty("Content-Type", "text/xml");
                    conn.setRequestProperty("SOAPAction", SOAP_ACTION_UPDATE);
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
            MyProgressDialog.dismiss(progressDialog);
            try {
                updatePwdJson = XML.toJSONObject(result);
                Log.e("XML", result);
                Log.e("JSON", updatePwdJson.toString());
                JSONObject job = updatePwdJson.getJSONObject("soap:Envelope");
                JSONObject job1 = job.getJSONObject("soap:Body");
                JSONObject job2 = job1.getJSONObject("UpatePasswordResponse");
                String msg = job2.getString("UpatePasswordResult");
                if (msg.equalsIgnoreCase("User not registered")) {
                    dial.dismiss();
                    Toast.makeText(LoginActivity.this, "User not registered", Toast.LENGTH_LONG).show();
                } else if (msg.equalsIgnoreCase("Password Updated Successfully")) {
                    dial.dismiss();
                }
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }


    /**
     * Update user status
     */


    private class UpdateUserstatusAsync extends AsyncTask<String, String, String> {

        private String response;
        BufferedReader reader;
        MyProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = MyProgressDialog.show(LoginActivity.this);

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                //paste your request structure here as the String body(copy it exactly as it is in soap ui)
                //assuming that this is your request body
//                String body = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:hs=\"http://tempuri.org/\">\n <soapenv:Body>\n <hs:" + METHOD_NAME + ">\n <hs:" + PARAMETER_FN + ">" + "Mansa Info" + "</hs:" + PARAMETER_FN + ">\n <hs:" + PARAMETER_PHONE + ">" + "98888988888" + "</hs:" + PARAMETER_PHONE + ">\n <hs:" + PARAMETER_EMAIL + ">" + "demo@demo.com" + "</hs:" + PARAMETER_EMAIL + ">\n  <hs:" + PARAMETER_PWD + ">" + "12345" + "</hs:" + PARAMETER_PWD + ">\n <hs:" + PARAMETER_DID + ">" + "android12345" + "</hs:" + PARAMETER_DID + ">\n </hs:" + METHOD_NAME + ">\n </soapenv:Body>\n</soapenv:Envelope>";

                String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "  <soap:Body>\n" +
                        "    <UdpateUserstatus xmlns=\"http://samepay.net/\">\n" +
                        "      <user_code>"+SharedPreferencesHandler.getStringValues(LoginActivity.this,"usercode")+"</user_code>\n" +
                        "      <status>"+"1"+"</status>\n" +        //TODO Whatr would be the status value
                        "    </UdpateUserstatus>\n" +
                        "  </soap:Body>\n" +
                        "</soap:Envelope>";

                Log.i(TAG, "doInBackground: " + body);
                try {
                    java.net.URL url = new URL(URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDefaultUseCaches(false);
                    conn.setRequestProperty("Content-Type", "text/xml");
                    conn.setRequestProperty("SOAPAction", UPDATE_SOAP_ACTION);
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
                MyProgressDialog.dismiss(progressDialog);
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
                JSONObject job2 = job1.getJSONObject("UdpateUserstatusResponse");
                String msg = job2.getString("UdpateUserstatusResult");
                startActivity(new Intent(LoginActivity.this, MainNewActivity.class));
                finish();
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }
}
