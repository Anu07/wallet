package app.src.com.walletapp.wifip2p.wifi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.OnlinePaymentEvent;
import app.src.com.walletapp.model.TransferOnlineEvent;
import app.src.com.walletapp.utils.MyProgressDialog;
import app.src.com.walletapp.view.fragment.BaseFragment;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import app.src.com.walletapp.wifip2p.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static app.src.com.walletapp.wifip2p.wifi.TransferActivity.TAG;

/**
 * Created by SONY on 4/10/2018.
 */

public class TransferOnlineFragment extends BaseFragment {

    @BindView(R.id.amount_ed)
    TextInputEditText amountEd;
    @BindView(R.id.submit_bttn)
    Button submitBttn;
    @BindView(R.id.homeLayout)
    RelativeLayout homeLayout;
    Unbinder unbinder;
    @BindView(R.id.mrchnt_ed)
    TextInputEditText mrchntEd;
    @BindView(R.id.mrchntInput)
    TextInputLayout mrchntInput;
    @BindView(R.id.amountInput)
    TextInputLayout amountInput;
    private TransferOnlineEvent mEvent;
    String URL = "http://samepay.net/SamePayWebService.asmx?WSDL";
    String NAMESPACE = "http://samepay.net/";
    String METHOD_NAME = "PayOnline";
    String BAL_METHOD_NAME = "GetWalletBalance";
    String SOAP_ACTION = NAMESPACE + METHOD_NAME;
    private JSONObject paymentJson;
    private String SOAP_ACTION_BAL = NAMESPACE + BAL_METHOD_NAME;


    @Subscribe
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_transfer_online, container
                , false);
        unbinder = ButterKnife.bind(this, view);
        mrchntEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                EventBus.getDefault().postSticky(new TransferOnlineEvent(mrchntEd.getText().toString().trim(), ""));
            }
        });

        amountEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                EventBus.getDefault().postSticky(new TransferOnlineEvent(mrchntEd.getText().toString().trim(), amountEd.getText().toString().trim()));
                Log.i(TAG, "afterTextChanged: " + mrchntEd.getText().toString().trim() + "~~~" + amountEd.getText().toString().trim());
            }
        });


        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.submit_bttn)
    public void onViewClicked() {
        mEvent = EventBus.getDefault().getStickyEvent(TransferOnlineEvent.class);
        if (mEvent != null && (!mEvent.getmMrchntId().equalsIgnoreCase("") && !mEvent.getmAmount().equalsIgnoreCase(""))) {
            new syncAccountAsync().execute();

//            getActivity().startActivity(new Intent(getActivity(), MainNewActivity.class));
        } else {
            if (!mrchntEd.getText().toString().isEmpty()) {
                mrchntInput.setVisibility(View.GONE);
                amountInput.setVisibility(View.VISIBLE);
            } else {
                mrchntInput.setVisibility(View.VISIBLE);
                amountInput.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Please fill merchant Id", Toast.LENGTH_LONG).show();
            }
        }

    }

    private class MakePaymentAsync extends AsyncTask<String, String, String> {

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
                        "    <PayOnline xmlns=\"http://samepay.net/\">\n" +
                        "      <UserCode>" +SharedPreferencesHandler.getStringValues(getActivity(), "usercode")+ "</UserCode>\n" +
                        "      <receiverPhone>" + mEvent.getmMrchntId() + "</receiverPhone>\n" +
                        "      <amount>" + mEvent.getmAmount() + "</amount>\n" +
                        "    </PayOnline>\n" +
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
                paymentJson = XML.toJSONObject(result);
                Log.e("XML", result);
                Log.e("JSON", paymentJson.toString());
                JSONObject job = paymentJson.getJSONObject("soap:Envelope");
                JSONObject job1 = job.getJSONObject("soap:Body");
                JSONObject job2 = job1.getJSONObject("PayOnlineResponse");
                String msg = job2.getString("PayOnlineResult");
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                //TODO Update balance after transaction call SyncBalnceAsync
                getActivity().startActivity(new Intent(getActivity(),MainNewActivity.class));
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private class syncAccountAsync extends AsyncTask<String, String, String> {

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
                        "    <GetWalletBalance xmlns=\"http://samepay.net/\">\n" +
                        "      <UserCode>" + SharedPreferencesHandler.getStringValues(getActivity(), "usercode") + "</UserCode>\n" +
                        "    </GetWalletBalance>\n" +
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
                    conn.setRequestProperty("SOAPAction", SOAP_ACTION_BAL);
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
                paymentJson = XML.toJSONObject(result);
                Log.e("XML", result);
                Log.e("JSON", paymentJson.toString());
                JSONObject job = paymentJson.getJSONObject("soap:Envelope");
                JSONObject job1 = job.getJSONObject("soap:Body");
                JSONObject job2 = job1.getJSONObject("GetWalletBalanceResponse");
                String msg = job2.getString("GetWalletBalanceResult");
                if (msg.equalsIgnoreCase("0")) {
                    Toast.makeText(getActivity(), "Insufficient balance in wallet", Toast.LENGTH_LONG).show();
                } else if (Float.parseFloat(msg) < Float.parseFloat(mEvent.getmAmount())) {
                    Toast.makeText(getActivity(), "low wallet balance", Toast.LENGTH_LONG).show();
                } else if(Utils.isNetworkAvailable(getActivity())){
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    new MakePaymentAsync().execute();
                }else{
                    Toast.makeText(getActivity(), "Unexpected error occurred.", Toast.LENGTH_LONG).show();
                }


            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }
        }
    }

}