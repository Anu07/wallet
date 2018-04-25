package app.src.com.walletapp.wifip2p.wifi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import app.src.com.walletapp.wifip2p.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static app.src.com.walletapp.wifip2p.wifi.TransferActivity.TAG;

/**
 * Created by SONY on 4/11/2018.
 */

public class ProfileFragment extends BaseFragment {


    @BindView(R.id.usr_img)
    CircleImageView usrImg;
    @BindView(R.id.bal_txt)
    TextView balTxt;
    @BindView(R.id.mrchnt_id)
    TextView mrchntId;
    @BindView(R.id.user_info)
    RelativeLayout userInfo;
    @BindView(R.id.share_icon)
    ImageView shareIcon;
    @BindView(R.id.downld_qr)
    TextView downldQr;
    @BindView(R.id.refer_icon)
    ImageView referIcon;
    @BindView(R.id.refer_text)
    TextView referText;
    @BindView(R.id.logout_txt)
    TextView logoutTxt;
    Unbinder unbinder;
    String URL = "http://samepay.net/SamePayWebService.asmx?WSDL";
    String NAMESPACE = "http://samepay.net/";
    String PROFILE_METHOD_NAME = "Get_profile";
    String SOAP_ACTION = NAMESPACE + PROFILE_METHOD_NAME;
    private JSONObject profileJson;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
//        new GetProfileAsync().execute();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.downld_qr)
    public void onViewClicked() {
        Toast.makeText(getActivity(), "Please wait...It may take few seconds to generate QR code.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getActivity(), QRCodeActivity.class));
            }
        }, 1500);
//        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container_current,new BarcodeFragment());

    }

    @OnClick(R.id.logout_txt)
    public void onLogOutViewClicked() {
        Utils.logOutPopUp(getActivity());
    }

    @OnClick(R.id.refer_text)
    public void onReferViewClicked() {
        sendReferral();
    }


    /**
     * sendReferral
     */
    private void sendReferral() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Simple Pay App");
            String sAux = "\nLet me recommend you this amazing application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=tkook.girls.games.com \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    private class GetProfileAsync extends AsyncTask<String, String, String> {

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
                        "      <UserID>"+SharedPreferencesHandler.getSharedPreferences(getActivity()).getString("userid","")+"</UserID>\n" +
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
                    conn.setRequestProperty("SOAPAction", PROFILE_METHOD_NAME);
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
                profileJson = XML.toJSONObject(result);
                Log.e("XML", result);
                Log.e("JSON", profileJson.toString());
                JSONObject job = profileJson.getJSONObject("soap:Envelope");
                JSONObject job1 = job.getJSONObject("soap:Body");
                JSONObject job2 = job1.getJSONObject("Get_profileResponse ");
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }
}
