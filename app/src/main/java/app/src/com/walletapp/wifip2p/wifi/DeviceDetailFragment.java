/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.src.com.walletapp.wifip2p.wifi;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.onlinepayment.OnlinePaymentRequest;
import app.src.com.walletapp.model.onlinepayment.Payment;
import app.src.com.walletapp.utils.MyPreferences;
import app.src.com.walletapp.utils.WalletBalanceListener;
import app.src.com.walletapp.wifip2p.GlobalActivity;
import app.src.com.walletapp.wifip2p.beans.WiFiTransferModal;
import app.src.com.walletapp.wifip2p.utils.PermissionsAndroid;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import app.src.com.walletapp.wifip2p.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.ContentValues.TAG;
import static app.src.com.walletapp.utils.CommonUtils.ONLINE;
import static app.src.com.walletapp.utils.CommonUtils.getMacAddress;
import static app.src.com.walletapp.utils.CommonUtils.getUniqueDeviceId;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener, FilePickerCallback {

    private static float val;
    private static String senderName;
    private static String txnId;
    private static String senderId;
    private static ProgressDialog progressDialogStat;
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.btn_disconnect)
    Button btnDisconnect;
    Unbinder unbinder;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    static float valueUpdated;
    private static ProgressDialog mProgressDialog;
    public static String WiFiClientIp = "";
    static Boolean ClientCheck = false;
    public static String GroupOwnerAddress = "";
    static long ActualFilelength = 0;
    static int Percentage = 0;
    public static String FolderName = "WalletApp";
    int position;
    private Uri MsgUri;
    private Button sendBttn;
    private TextView amtEntered;
    ProgressDialog pDial;
    private float leftBalance;
    static Context ctx;
    private static LinearLayout creditsLay;
    private Button sendCreditsBttn;
    private EditText reciever_name;
    private TextView mdeductionMsg;
    private Dialog verifydialog;
    private boolean result;
    private static String TxnId;
    private FilePicker filePicker;
    private String pickerPath;
    private LinearLayout connectDiscnnct;
    static WalletBalanceListener mListener;
    static float bal = 0;
    private BroadcastReceiver updateUIReciver;
    private float amountSent=0;
    private int count;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (WalletBalanceListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + e.getMessage());
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);
        sendBttn = mContentView.findViewById(R.id.btn_start_client);
        amtEntered = mContentView.findViewById(R.id.amt_text);
        creditsLay = mContentView.findViewById(R.id.credits_lay);
        connectDiscnnct = mContentView.findViewById(R.id.cnnctDiscnnctLayout);
        sendCreditsBttn = mContentView.findViewById(R.id.sendMoney);
        mdeductionMsg = mContentView.findViewById(R.id.device_address);
        pDial = new ProgressDialog(getActivity());
        pDial.setMessage("Sending...");
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                if (config != null && config.deviceAddress != null && device != null) {
                    config.deviceAddress = device.deviceAddress;
                    config.wps.setup = WpsInfo.PBC;
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                            "Connecting to :" + device.deviceAddress, true, true
                    );

                    //send dynamic image
                    ((DeviceActionListener) getActivity()).connect(config, position);
                } else {

                }
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                        if (((WiFiDirectActivity) getActivity()).failedERROR) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });

        sendCreditsBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!amtEntered.getText().toString().equalsIgnoreCase("")) {
                    sendCredits();
                } else {
                    Utils.showSnackBar(getView(), "Field can't be empty.");
                }
            }
        });

        //restricting user to not enter more than 2 decimal places
        amtEntered.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {

            }
            public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {

            }

            public void afterTextChanged(Editable arg0) {
                if (arg0.length() > 0) {
                    String str = amtEntered.getText().toString();
                    amtEntered.setOnKeyListener(new View.OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL) {
                                count--;
                                InputFilter[] fArray = new InputFilter[1];
                                fArray[0] = new InputFilter.LengthFilter(100);
                                amtEntered.setFilters(fArray);
                                //change the edittext's maximum length to 100.
                                //If we didn't change this the edittext's maximum length will
                                //be number of digits we previously entered.
                            }
                            return false;
                        }
                    });
                    char t = str.charAt(arg0.length() - 1);
                    if (t == '.') {
                        count = 0;
                    }
                    if (count >= 0) {
                        if (count == 2) {
                            InputFilter[] fArray = new InputFilter[1];
                            fArray[0] = new InputFilter.LengthFilter(arg0.length());
                            amtEntered.setFilters(fArray);
                            //prevent the edittext from accessing digits
                            //by setting maximum length as total number of digits we typed till now.
                        }
                        count++;
                    }
                }
            }
        });
        unbinder = ButterKnife.bind(this, mContentView);
        return mContentView;
    }


    /**
     * To send credits
     */

    private void sendCredits() {
        if (!amtEntered.getText().toString().equalsIgnoreCase("") && Float.parseFloat(amtEntered.getText().toString()) < SharedPreferencesHandler.getFloatValues(getActivity(), "balance") && Float.parseFloat(amtEntered.getText().toString()) > 0 &&Float.parseFloat(amtEntered.getText().toString()) != 0) {
            pDial.show();
            TxnId = Utils.getTransactionId();
            ((WiFiDirectActivity)getActivity()).mHelper.saveTxnDetails(TxnId,amtEntered.getText().toString(),device.deviceAddress,"My device");
            sendTextMsg("Transaction Id:" + TxnId + "," + "" + "," + amtEntered.getText().toString());
        } else {
            Utils.showSnackBar(getView(), "Invalid amount");
        }
    }


    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
        Log.i(TAG, "onConnectionInfoAvailable: now");
        if(device !=null && device.status==3){       //connected
            creditsLay.setVisibility(View.VISIBLE);
            sendBttn.setVisibility(View.GONE);
            btnConnect.setVisibility(View.GONE);
        }
        // The owner IP is now known.
        TextView view = mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = mContentView.findViewById(R.id.device_info);
        if (info != null && info.groupOwnerAddress.getHostAddress() != null) {
            view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());
        } else {
            CommonMethods.DisplayToast(getActivity(), "Host Address not found");
        }
        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        try {
            String GroupOwner = info.groupOwnerAddress.getHostAddress();
            if (GroupOwner != null && !GroupOwner.equals(""))
                SharedPreferencesHandler.setStringValues(getActivity(),
                        getString(R.string.pref_GroupOwnerAddress), GroupOwner);
            //first check for file storage permission
            if (!PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(getActivity())) {
                Utils.getInstance().showToast("Please enable storage Permission from application storage option");
                return;
            }

            if (info.groupFormed && info.isGroupOwner) {
            /*
             * set sharedpreference which remember that device is server.
        	 */
                SharedPreferencesHandler.setStringValues(getActivity(),
                        getString(R.string.pref_ServerBoolean), "true");

                FileServerAsyncTask FileServerobj = new FileServerAsyncTask(getActivity(),
                        getActivity(), FileTransferService.PORT);
                if (FileServerobj != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        FileServerobj.executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR,
                                new String[]{null});
                        // FileServerobj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Void);
                    } else
                        FileServerobj.execute();
                }
            } else {
                // The other device acts as the client. In this case, we enable the
                // get file button.
                if (!ClientCheck) {
                    firstConnectionMessage firstObj = new firstConnectionMessage(
                            GroupOwnerAddress);
                    if (firstObj != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            firstObj.executeOnExecutor(
                                    AsyncTask.THREAD_POOL_EXECUTOR,
                                    new String[]{null});
                        } else
                            firstObj.execute();
                    }
                }
                FileServerAsyncTask FileServerobj = new FileServerAsyncTask(getActivity(),
                        getActivity(), FileTransferService.PORT);
                if (FileServerobj != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        FileServerobj.executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR,
                                new String[]{null});
                    } else
                        FileServerobj.execute();

                }

            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerCustomReceiver();
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(updateUIReciver);
    }

    /**
     * Updates the UI with device data
     *
     * @param device   the device to be displayed
     * @param position
     */
    public void showDetails(WifiP2pDevice device, int position) {
        this.position = position;
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());
        Log.i(TAG, "showDetails: "+GlobalActivity.userType);
        if(GlobalActivity.userType.equalsIgnoreCase("S")){
            connectDiscnnct.setVisibility(View.VISIBLE);
        }else{
            connectDiscnnct.setVisibility(View.GONE);
        }
       /* if (device.status ==3) {
            connectDiscnnct.setVisibility(View.VISIBLE);
        } else if(device.status==2){
            btnConnect.setVisibility(View.GONE);
            creditsLay.setVisibility(View.VISIBLE);
        }*/
    }



    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        
        /*
         * Remove All the prefrences here
         */
        SharedPreferencesHandler.setStringValues(getActivity(),
                getString(R.string.pref_GroupOwnerAddress), "");
        SharedPreferencesHandler.setStringValues(getActivity(),
                getString(R.string.pref_ServerBoolean), "");
        SharedPreferencesHandler.setStringValues(getActivity(),
                getString(R.string.pref_WiFiClientIp), "");

        ClientCheck = false;
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    static Handler handler;


    @Override
    public void onFilesChosen(List<ChosenFile> list) {
        ChosenFile file = list.get(0);
        String extension = "";
        int i = list.get(0).getDisplayName().lastIndexOf('.');
        if (i > 0) {
            extension = list.get(0).getDisplayName().substring(i + 1);
        }

        ActualFilelength = file.getSize();

        TextView statusText = mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + file.getOriginalPath());

        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, file.getQueryUri());
                /*
                 * Choose on which device file has to send weather its server or client
    	         */
        String Ip = SharedPreferencesHandler.getStringValues(
                getActivity(), getString(R.string.pref_WiFiClientIp));
        String OwnerIp = SharedPreferencesHandler.getStringValues(
                getActivity(), getString(R.string.pref_GroupOwnerAddress));
        if (!TextUtils.isEmpty(OwnerIp) && OwnerIp.length() > 0) {
            String host = null;
            int sub_port = -1;

            String ServerBool = SharedPreferencesHandler.getStringValues(getActivity(), getString(R.string.pref_ServerBoolean));
            if (!TextUtils.isEmpty(ServerBool) && ServerBool.equalsIgnoreCase("true") && !TextUtils.isEmpty(Ip)) {
                host = Ip;
                sub_port = FileTransferService.PORT;
                serviceIntent
                        .putExtra(
                                FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                                Ip);

            } else {
                FileTransferService.PORT = 8888;
                host = OwnerIp;
                sub_port = FileTransferService.PORT;
                serviceIntent.putExtra(
                        FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                        OwnerIp);
            }
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, FileTransferService.PORT);

            serviceIntent.putExtra(FileTransferService.Extension, file.getDisplayName());

            serviceIntent.putExtra(FileTransferService.Filelength,
                    String.valueOf(ActualFilelength));

            if (host != null && sub_port != -1) {
                showprogress("Sending...");
                getActivity().startService(serviceIntent);
            } else {
                CommonMethods.DisplayToast(getActivity(),
                        "Host Address not found, Please Re-Connect");
                DismissProgressDialog();
            }

        } else {
            DismissProgressDialog();
            CommonMethods.DisplayToast(getActivity(),
                    "Host Address not found, Please Re-Connect");
        }
    }


    private void checkExternalStoragePermission(String s) {
        boolean isExternalStorage = PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(getActivity());
        if (!isExternalStorage) {
            PermissionsAndroid.getInstance().requestForWriteExternalStoragePermission(getActivity());
        } else {
            sendTextMsg(s);
        }
    }


    private void sendTextMsg(String msg) {
        filePicker = new FilePicker(getActivity());
        filePicker.setFilePickerCallback(this);
        filePicker.setFolderName(getActivity().getString(R.string.app_name));
        MsgUri = Utils.generateNoteOnSD(getActivity(), "sender_credits.txt", msg);
        Log.i(TAG, "sendTextMsg: " + MsgUri);
        Intent intent = new Intent();           //Custom intent to send text file to receiver
        intent.setData(MsgUri);
        filePicker.submit(intent, "", Utils.getTransactionId());
        updateValuesafterCreditsSent();
    }

    private void updateValuesafterCreditsSent() {
        leftBalance =(float)(SharedPreferencesHandler.getFloatValues(getActivity(), "balance") - Float.parseFloat(amtEntered.getText().toString()));
        Log.i(TAG, "onEditorAction: " + leftBalance);
        amountSent=Float.parseFloat(amtEntered.getText().toString());
        Log.i(TAG, "updateValuesafterCreditsSent: amount"+amountSent);
        SharedPreferencesHandler.setFloatValues(getActivity(), "balance", leftBalance);
        pDial.dismiss();
        mListener.walletBalanceUpdate(SharedPreferencesHandler.getFloatValues(getActivity(), "balance"));
        Log.i(TAG, "updateValuesafterCreditsSent: " + SharedPreferencesHandler.getFloatValues(getActivity(), "balance"));
        amtEntered.setText("");
    }


    private void pickFilesSingle() {
        filePicker = new FilePicker(getActivity());
        filePicker.setFilePickerCallback(this);
        filePicker.setFolderName(getActivity().getString(R.string.app_name));
        filePicker.pickFile();
    }


    @Override
    public void onError(String message) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public static class FileServerAsyncTask extends AsyncTask<String, String, String> {

        //private TextView statusText;
        private Context mFilecontext;
        private Activity mActivity;
        private String Extension, Key;
        private File EncryptedFile;
        private long ReceivedFileLength;
        private int PORT;

        /**
         * @param context
         * @param port
         */
        public FileServerAsyncTask(Activity ctx, Context context, int port) {
            this.mFilecontext = context;
            mActivity = ctx;
            handler = new Handler();
            this.PORT = port;
        /*if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(mFilecontext,
                    ProgressDialog.THEME_HOLO_LIGHT);*/
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                CommonMethods.e("File Async task port", "File Async task port-> " + PORT);
                // init handler for progressdialog
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(PORT));

                Log.d(CommonMethods.Tag, "Server: Socket opened");
                Socket client = serverSocket.accept();

                Utils.d("Client's InetAddresssss  ", "" + client.getInetAddress());

                WiFiClientIp = client.getInetAddress().getHostAddress();
                ObjectInputStream ois = new ObjectInputStream(
                        client.getInputStream());
                WiFiTransferModal obj = null;
                String InetAddress;
                try {
                    obj = (WiFiTransferModal) ois.readObject();

                    if (obj != null) {
                        InetAddress = obj.getInetAddress();
                        if (InetAddress != null
                                && InetAddress
                                .equalsIgnoreCase(FileTransferService.inetaddress)) {
                            CommonMethods.e("File Async Group Client Ip", "port-> "
                                    + WiFiClientIp);
                            SharedPreferencesHandler.setStringValues(mFilecontext,
                                    mFilecontext.getString(R.string.pref_WiFiClientIp), WiFiClientIp);
                            CommonMethods
                                    .e("File Async Group Client Ip from SHAREDPrefrence",
                                            "port-> "
                                                    + SharedPreferencesHandler
                                                    .getStringValues(
                                                            mFilecontext,
                                                            mFilecontext.getString(R.string.pref_WiFiClientIp)));
                            //set boolean true which identifiy that this device will act as server.
                            SharedPreferencesHandler.setStringValues(mFilecontext,
                                    mFilecontext.getString(R.string.pref_ServerBoolean), "true");
                            ois.close(); // close the ObjectOutputStream object
                            // after saving
                            serverSocket.close();

                            return "Demo";
                        }

                   /* final Runnable r = new Runnable() {

                        public void run() {
                            // TODO Auto-generated method stub
                            mProgressDialog.setMessage("Receiving...");
                            mProgressDialog.setIndeterminate(false);
                            mProgressDialog.setMax(100);
                            mProgressDialog.setProgress(0);
                            mProgressDialog.setProgressNumberFormat(null);
                            mProgressDialog
                                    .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            mProgressDialog.show();
                        }
                    };
                    handler.post(r);*/
                        Utils.d("FileName got from socket on other side->>> ",
                                obj.getFileName());
                    }

                    final File f = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + FolderName + "/"
                                    + obj.getFileName());

                    File dirs = new File(f.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    f.createNewFile();

				/*
                 * Recieve file length and copy after it
				 */
                    this.ReceivedFileLength = obj.getFileLength();

                    InputStream inputstream = client.getInputStream();


                    copyRecievedFile(inputstream, new FileOutputStream(f),
                            ReceivedFileLength);
                    ois.close(); // close the ObjectOutputStream object after saving
                    // file to storage.
                    serverSocket.close();

				/*
                 * Set file related data and decrypt file in postExecute.
				 */
                    this.Extension = obj.getFileName();
                    this.EncryptedFile = f;
                    return f.getAbsolutePath();

                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return "";
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.i(TAG, "onPostExecute: after transfer"+result);
                if (!result.equalsIgnoreCase("Demo")) {

                    openFile(mActivity, result, mFilecontext);
                    //TODO Update balance in main Activity
                } else if (!TextUtils.isEmpty(result)) {
                    /*
                     * To initiate socket again we are intiating async task
					 * in this condition.
					 */
                    FileServerAsyncTask FileServerobj = new
                            FileServerAsyncTask(mActivity, mFilecontext, FileTransferService.PORT);
                    if (FileServerobj != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            FileServerobj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{null});

                        } else FileServerobj.execute();
                    }
                }
            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
       /* if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mFilecontext);
        }*/
        }
    }


    public static void openFile(Activity activity, String stringUrl, Context context) {
        Uri uri = Uri.parse(stringUrl);
        valueUpdated = checkAmountFromFile(Utils.readFromSD());
        Log.i(TAG, "openFile: "+valueUpdated);
        senderName = Utils.readFromSD().split(",")[1];
        txnId = Utils.readFromSD().split(",")[0].split(":")[1];
        Log.i(TAG, "openFile: " + TxnId + "fdjdshf" + senderName + "SID" + senderId);
        Payment payment = new Payment(String.valueOf(valueUpdated), "$", "Cash received", getMacAddress(activity), getUniqueDeviceId(), "", Calendar.getInstance().getTime().toString(), txnId, senderId);
        bal = SharedPreferencesHandler.getFloatValues(activity, "balance") + valueUpdated;
        Log.i(TAG, "balance after recieving: " + bal);
        showRecieveAlert(activity, valueUpdated, payment);
    }

    private static void showRecieveAlert(final Activity activity, float money, final Payment payment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.DialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final TextView edt = dialogView.findViewById(R.id.edit1);
        edt.setText("Hi you have received $" + money + " in your wallet.");
        dialogBuilder.setTitle("Money Received");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mListener.walletBalanceUpdate(bal);
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        try {
            b.show();
            b.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static OnlinePaymentRequest syncReceiveRequest(Payment payment) {
        List<Payment> payments = new ArrayList<>();
        payments.add(payment);
        OnlinePaymentRequest receiveRequest = new OnlinePaymentRequest(ONLINE, payments, MyPreferences.getInstance().getPreference(MyPreferences.Keys.TOKEN), MyPreferences.getInstance().getPreference(MyPreferences.Keys.USERID));
        return receiveRequest;
    }

    private static float checkAmountFromFile(String text) {
        if (!text.toString().equalsIgnoreCase("")) {
            val = Float.parseFloat(text.split(",")[2].replace("\n", ""));
        }
        return val;
    }


    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        long total = 0;
        long test = 0;
        byte buf[] = new byte[FileTransferService.ByteSize];
        if (buf == null) return false;

        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
                try {
                    total += len;
                    if (ActualFilelength > 0) {
                        Percentage = (int) ((total * 100) / ActualFilelength);
                    }
                    mProgressDialog.setProgress(Percentage);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    Percentage = 0;
                    ActualFilelength = 0;
                }
            }
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    public static boolean copyRecievedFile(InputStream inputStream,
                                           OutputStream out, Long length) {


        byte buf[] = new byte[FileTransferService.ByteSize];
        byte Decryptedbuf[] = new byte[FileTransferService.ByteSize];
        String Decrypted;
        int len;
        long total = 0;
        int progresspercentage = 0;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                try {
                    out.write(buf, 0, len);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    total += len;
                    if (length > 0) {
                        progresspercentage = (int) ((total * 100) / length);
                    }
//                    mProgressDialog.setProgress(progresspercentage);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    if (mProgressDialog != null) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                }
            }
            // dismiss progress after sending
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    public void showprogress(final String task) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity(),
                    ProgressDialog.THEME_HOLO_LIGHT);
        }
        Handler handle = new Handler();
        final Runnable send = new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                mProgressDialog.setMessage(task);
                // mProgressDialog.setProgressNumberFormat(null);
                // mProgressDialog.setProgressPercentFormat(null);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
//				mProgressDialog.setCancelable(false);
                mProgressDialog.setProgressNumberFormat(null);
                mProgressDialog
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.show();
            }
        };
        handle.post(send);
    }

    public static void DismissProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    /*
     * Async class that has to be called when connection establish first time. Its main motive is to send blank message
     * to server so that server knows the IP address of client to send files Bi-Directional.
     */
    class firstConnectionMessage extends AsyncTask<String, Void, String> {

        String GroupOwnerAddress = "";

        public firstConnectionMessage(String owner) {
            // TODO Auto-generated constructor stub
            this.GroupOwnerAddress = owner;

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            CommonMethods.e("On first Connect", "On first Connect");

            Intent serviceIntent = new Intent(getActivity(),
                    WiFiClientIPTransferService.class);

            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);

            if (info.groupOwnerAddress.getHostAddress() != null) {
                serviceIntent.putExtra(
                        FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                        info.groupOwnerAddress.getHostAddress());

                serviceIntent.putExtra(
                        FileTransferService.EXTRAS_GROUP_OWNER_PORT,
                        FileTransferService.PORT);
                serviceIntent.putExtra(FileTransferService.inetaddress,
                        FileTransferService.inetaddress);

            }

            getActivity().startService(serviceIntent);

            return "success";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                if (result.equalsIgnoreCase("success")) {
                    CommonMethods.e("On first Connect",
                            "On first Connect sent to asynctask");
                    ClientCheck = true;
                }
            }

        }

    }


    private void registerCustomReceiver() {
        IntentFilter filter = new IntentFilter();

        filter.addAction("com.hello.action");

        updateUIReciver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //UI update here
                bal = SharedPreferencesHandler.getFloatValues(context, "balance") + amountSent;
                Log.e(TAG, "onReceive: "+bal+"~~~~~"+amountSent);
                amountSent=0;
                mListener.walletBalanceUpdate(bal);
            }
        };
        getActivity().registerReceiver(updateUIReciver,filter);
    }




}
