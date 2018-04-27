package app.src.com.walletapp.wifip2p.wifi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import app.src.com.walletapp.R;
import app.src.com.walletapp.utils.MyProgressDialog;
import app.src.com.walletapp.view.fragment.BaseFragment;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static app.src.com.walletapp.wifip2p.wifi.TransferActivity.TAG;

/**
 * Created by SONY on 4/23/2018.
 */

public class SettingsFragment extends BaseFragment {


    String URL = "http://samepay.net/SamePayWebService.asmx?WSDL";
    String NAMESPACE = "http://samepay.net/";
    String METHOD_NAME = "Get_profile";
    String DEACTIVATE_METHOD_NAME = "Deactivate";
    String UPDATE_METHOD_NAME = "Updateprofile";
    String SOAP_ACTION = NAMESPACE + METHOD_NAME;
    String D_SOAP_ACTION = NAMESPACE + DEACTIVATE_METHOD_NAME;
    String UPDATE_SOAP_ACTION = NAMESPACE + UPDATE_METHOD_NAME;
    ;
    @BindView(R.id.fName)
    TextInputEditText fName;
    @BindView(R.id.firstName)
    TextInputLayout firstName;
    @BindView(R.id.lName)
    TextInputEditText lName;
    @BindView(R.id.lastName)
    TextInputLayout lastName;
    @BindView(R.id.emailAddress)
    TextInputEditText emailAddress;
    @BindView(R.id.emailLayout)
    TextInputLayout emailLayout;
    @BindView(R.id.changeMpin)
    TextInputEditText changeMpin;
    @BindView(R.id.chngMpin)
    TextInputLayout chngMpin;
    @BindView(R.id.renterPin)
    TextInputEditText renterPin;
    @BindView(R.id.reEnterMpin)
    TextInputLayout reEnterMpin;
    @BindView(R.id.bttn_save)
    Button bttnSave;
    @BindView(R.id.deactivare)
    Button deactivare;
    Unbinder unbinder;
    private JSONObject getProfilejson;
    private JSONObject deactivateJson;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        new getProfileAsync().execute();
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.bttn_save)
    public void onViewClicked() {
        if(validate()){
            new UpdateProfileAsync().execute();
        }

    }

    private boolean validate() {
        if(fName.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"FirstName can't be empty",Toast.LENGTH_LONG).show();
            return false;
        }else if(emailAddress.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Email can't be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    /**
     * Get profile
     */


    private class getProfileAsync extends AsyncTask<String, String, String> {

        private String response;
        BufferedReader reader;
        MyProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = MyProgressDialog.show(getActivity());
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
                        "    <Get_profile xmlns=\"http://samepay.net/\">\n" +
                        "      <UserID>" + SharedPreferencesHandler.getSharedPreferences(getActivity()).getString("userid", "") + "</UserID>\n" +
                        "    </Get_profile>\n" +
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
                getProfilejson = XML.toJSONObject(result);
                Log.e("XML", result);
                Log.e("JSON", getProfilejson.toString());
                JSONObject job = getProfilejson.getJSONObject("soap:Envelope");
                JSONObject job1 = job.getJSONObject("soap:Body");
                JSONObject job2 = job1.getJSONObject("Get_profileResponse");
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }


    private class DeactivateAsync extends AsyncTask<String, String, String> {

        private String response;
        BufferedReader reader;
        MyProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = MyProgressDialog.show(getActivity());
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
                        "    <Deactivate xmlns=\"http://samepay.net/\">\n" +
                        "      <UserCode>" + SharedPreferencesHandler.getSharedPreferences(getActivity()).getString("userid", "") + "</UserCode>\n" +
                        "    </Deactivate>\n" +
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
                    conn.setRequestProperty("SOAPAction", D_SOAP_ACTION);
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
                deactivateJson = XML.toJSONObject(result);
                Log.e("XML", result);
                Log.e("JSON", deactivateJson.toString());
                JSONObject job = deactivateJson.getJSONObject("soap:Envelope");
                JSONObject job1 = job.getJSONObject("soap:Body");
                JSONObject job2 = job1.getJSONObject("DeactivateResponse");
                String msg = job2.getString("DeactivateResult");
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }

    /**
     * Update profile
     */
    private class UpdateProfileAsync extends AsyncTask<String, String, String> {

        private String response;
        BufferedReader reader;
        MyProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = MyProgressDialog.show(getActivity());
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
                        "    <Updateprofile xmlns=\"http://samepay.net/\">\n" +
                        "      <UserCode>" + SharedPreferencesHandler.getStringValues(getActivity(), "userid") + "</UserCode>\n" +
                        "      <Phone>" + SharedPreferencesHandler.getStringValues(getActivity(),"phone") + "</Phone>\n" +
                        "      <FullName>" + fName.getText().toString().trim() + " " + lName.getText().toString().trim() + "</FullName>\n" +
                        "      <Email>" + emailAddress.getText().toString().trim() + "</Email>\n" +
                        "    </Updateprofile>\n" +
                        "  </soap12:Body>\n" +
                        "</soap12:Envelope>\n";

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
                deactivateJson = XML.toJSONObject(result);
                Log.e("XML", result);
                Log.e("JSON", deactivateJson.toString());
                JSONObject job = deactivateJson.getJSONObject("soap:Envelope");
                JSONObject job1 = job.getJSONObject("soap:Body");
                JSONObject job2 = job1.getJSONObject("UpdateprofileResponse");
                String msg = job2.getString("UpdateprofileResult");
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }


}
