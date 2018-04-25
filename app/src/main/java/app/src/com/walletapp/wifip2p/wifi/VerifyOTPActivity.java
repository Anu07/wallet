package app.src.com.walletapp.wifip2p.wifi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dpizarro.pinview.library.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Verify;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.OfflineEvent;
import app.src.com.walletapp.model.UserDataEvent;
import app.src.com.walletapp.utils.MyProgressDialog;
import app.src.com.walletapp.view.activity.BaseActivity;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import butterknife.BindView;
import butterknife.ButterKnife;

import static app.src.com.walletapp.wifip2p.wifi.TransferActivity.TAG;

/**
 * Created by SONY on 4/17/2018.
 */

public class VerifyOTPActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    public static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    @BindView(R.id.pinView)
    PinView pinView;
    @BindView(R.id.submitOTP)
    Button submitOTP;
    @BindView(R.id.resendTxt)
    TextView resendTxt;
    public static FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ViewGroup mPhoneNumberViews;
    private boolean mVerificationInProgress = false;
    private String TAG = VerifyOTPActivity.class.getName();
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
    private UserDataEvent dataEvent;
    private String phoneNumber;

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        ButterKnife.bind(this);
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        resendTxt.setOnClickListener(this);
        submitOTP.setOnClickListener(this);

        dataEvent= EventBus.getDefault().getStickyEvent(UserDataEvent.class);
        Log.i(TAG, "onCreate: "+dataEvent.getPhone());
        phoneNumber=dataEvent.getPhone();
        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Log.i(TAG, "onVerificationFailed: "+e.getMessage() +"111"+dataEvent.getPhone());
                    Snackbar.make(findViewById(android.R.id.content), "Invalid Phone number",
                            Snackbar.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };

        startAuthentication();
    }

    private void startAuthentication() {
        startPhoneNumberVerification(phoneNumber);
    }

    @Subscribe
    public void onOfflineEvent(OfflineEvent event) {
        Log.i(TAG, "onOfflineEvent: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(phoneNumber);
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                Toast.makeText(VerifyOTPActivity.this, "Invalid code.", Toast.LENGTH_SHORT).show();
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]



    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                break;
            case STATE_CODE_SENT:
                break;
                // Code sent state, show the verification field, the
            case STATE_VERIFY_FAILED:
                break;
                // Verification has failed, show all options
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                // Set the verification text based on the credential
                Log.i(TAG, "updateUI: Success");
               new LoginAsync().execute();
                break;
            case STATE_SIGNIN_FAILED:
                break;
                // No-op, handled by sign-in check
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                new LoginAsync().execute();
                break;
        }

        if (user == null) {
            // Signed out
            Log.e(TAG, "updateUI: User  null" );        } else {
            // Signed in
            Log.e(TAG, "updateUI: User not null" );          }
    }

    private boolean validatePhoneNumber() {
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(VerifyOTPActivity.this, "Invalid phone number.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitOTP:
                String code = pinView.getPinResults().toString();
                Log.i(TAG, "onClick:PinView "+code);
                if (code.trim().equalsIgnoreCase("")) {
                    Toast.makeText(VerifyOTPActivity.this, "OTP cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    new LoginAsync().execute();
                }

                break;
            case R.id.resendTxt:
                pinView.clear();
                resendVerificationCode(phoneNumber, mResendToken);
                break;
        }
    }

    private class LoginAsync extends AsyncTask<String, String, String> {

        private String response;
        BufferedReader reader;
        MyProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = MyProgressDialog.show(VerifyOTPActivity.this);

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                //paste your request structure here as the String body(copy it exactly as it is in soap ui)
                //assuming that this is your request body
//                String body = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:hs=\"http://tempuri.org/\">\n <soapenv:Body>\n <hs:" + METHOD_NAME + ">\n <hs:" + PARAMETER_FN + ">" + "Mansa Info" + "</hs:" + PARAMETER_FN + ">\n <hs:" + PARAMETER_PHONE + ">" + "98888988888" + "</hs:" + PARAMETER_PHONE + ">\n <hs:" + PARAMETER_EMAIL + ">" + "demo@demo.com" + "</hs:" + PARAMETER_EMAIL + ">\n  <hs:" + PARAMETER_PWD + ">" + "12345" + "</hs:" + PARAMETER_PWD + ">\n <hs:" + PARAMETER_DID + ">" + "android12345" + "</hs:" + PARAMETER_DID + ">\n </hs:" + METHOD_NAME + ">\n </soapenv:Body>\n</soapenv:Envelope>";

                Log.i(TAG, "doInBackground: "+dataEvent.getPhone());

                String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://samepay.net/\">\n" +
                        "  <SOAP-ENV:Body>\n" +
                        "    <ns1:Register_u>\n" +
                        "      <ns1:fullname>" + dataEvent.getName()+"</ns1:fullname>\n" +
                        "      <ns1:Phone>" +dataEvent.getPhone()+"</ns1:Phone>\n" +
                        "      <ns1:email>" + dataEvent.getEmail()+ "</ns1:email>\n" +
                        "      <ns1:password>" +dataEvent.getPwd()+ "</ns1:password>\n" +
                        "      <ns1:device_id>" + SharedPreferencesHandler.getStringValues(VerifyOTPActivity.this, "DeviceToken") + "</ns1:device_id>\n" +
                        "    </ns1:Register_u>\n" +
                        "  </SOAP-ENV:Body>\n" +
                        "</SOAP-ENV:Envelope>";

                Log.i(TAG, "doInBackground: "+body);
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
                JSONObject job2 = job1.getJSONObject("Register_uResponse");
                String msg = job2.getString("Register_uResult");
                Toast.makeText(VerifyOTPActivity.this, msg, Toast.LENGTH_LONG).show();
                if (msg.contains("Successfully") || msg.contains("Already")) {
                    SharedPreferencesHandler.setStringValues(VerifyOTPActivity.this, "user", "1");
                    SharedPreferencesHandler.setStringValues(VerifyOTPActivity.this, "phone",dataEvent.getPhone());
                    startActivity(new Intent(VerifyOTPActivity.this,MainNewActivity.class));
                    finish();
                }
            } catch (Exception e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }

        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
