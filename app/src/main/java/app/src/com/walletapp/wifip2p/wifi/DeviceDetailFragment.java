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
import android.graphics.drawable.ColorDrawable;
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
import android.view.Gravity;
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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import java.util.Timer;
import java.util.TimerTask;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.onlinepayment.OnlinePaymentRequest;
import app.src.com.walletapp.model.onlinepayment.Payment;
import app.src.com.walletapp.utils.DecimalFilter;
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
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener, FilePickerCallback ,DeviceActionListener{

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
    static float valueUpdated;
    public static String WiFiClientIp = "";
    static Boolean ClientCheck = false;
    public static String GroupOwnerAddress = "";
    static long ActualFilelength = 0;
    static int Percentage = 0;
    public static String FolderName = "WalletApp";
    int position;
    private Uri MsgUri;
    private Button sendBttn;
    private EditText amtEntered;
    ProgressDialog pDialog;
    static ProgressDialog pDial=null;
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);
        sendBttn = mContentView.findViewById(R.id.btn_start_client);
        amtEntered = mContentView.findViewById(R.id.amt_text);
        creditsLay = mContentView.findViewById(R.id.credits_lay);
        connectDiscnnct = mContentView.findViewById(R.id.cnnctDiscnnctLayout);
        sendCreditsBttn = mContentView.findViewById(R.id.sendMoney);
        mdeductionMsg = mContentView.findViewById(R.id.device_address);
        pDialog = new ProgressDialog(getActivity(), R.style.DialogTheme);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pDialog.getWindow().setGravity(Gravity.CENTER);
        pDialog.setMessage("Sending...");

        //restricting user to not enter more than 2 decimal places
//      amtEntered.addTextChangedListener(new DecimalFilter(amtEntered,getActivity()));
        unbinder = ButterKnife.bind(this, mContentView);
        btnConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                if (config != null && config.deviceAddress != null && device != null) {
                    config.deviceAddress = device.deviceAddress;
                    config.wps.setup = WpsInfo.PBC;
                    btnConnect.setEnabled(false);

                    btnConnect.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnConnect.setEnabled(true);
                        }
                    }, 5000);
                    //send dynamic image
                    ((DeviceActionListener) getActivity()).connect(config, position);
                }
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                        if (((TransferActivity) getActivity()).failedERROR) {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                        }
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.dismiss();
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


        return mContentView;
    }


    /**
     * To send credits
     */

    private void sendCredits() {
        pDial = new ProgressDialog(getActivity());
        pDial.setMessage("Sending...");
        if (!amtEntered.getText().toString().equalsIgnoreCase("") && Float.parseFloat(amtEntered.getText().toString()) < SharedPreferencesHandler.getFloatValues(getActivity(), "balance") && Float.parseFloat(amtEntered.getText().toString()) > 0 &&Float.parseFloat(amtEntered.getText().toString()) != 0) {
            pDial.show();
            TxnId = Utils.getTransactionId();
            ((TransferActivity)getActivity()).mHelper.saveTxnDetails(TxnId,amtEntered.getText().toString(),device.deviceAddress,getActivity());
            sendTextMsg("Transaction Id:" + TxnId + "," + "" + "," + amtEntered.getText().toString());
        } else {
            Utils.showSnackBar(getView(), "Invalid amount");
        }
    }


    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (pDial != null && pDial.isShowing()) {
            pDial.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
        Log.i(TAG, "onConnectionInfoAvailable: now");
        if(device !=null && device.status==3 && GlobalActivity.userType.equalsIgnoreCase("S")){       //connected
            creditsLay.setVisibility(View.VISIBLE);
            sendBttn.setVisibility(View.GONE);
            btnConnect.setVisibility(View.GONE);
        }else if(device!=null && device.status ==3  && GlobalActivity.userType.equalsIgnoreCase("R")){
            creditsLay.setVisibility(View.GONE);
            sendBttn.setVisibility(View.GONE);
            btnConnect.setVisibility(View.GONE);
            btnDisconnect.setVisibility(View.GONE);
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
        if(GlobalActivity.userType.equalsIgnoreCase("S") && device.status==3){          //3 means available
            connectDiscnnct.setVisibility(View.VISIBLE);
            creditsLay.setVisibility(View.GONE);
        }else{
            connectDiscnnct.setVisibility(View.GONE);
            creditsLay.setVisibility(View.GONE);
        }
       /* if (device.status ==3) {
            connectDiscnnct.setVisibility(View.VISIBLE);
        } else if(device.status==2){
            btnConnect.setVisibility(View.GONE);
            creditsLay.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void cancelDisconnect() {

    }

    @Override
    public void connect(WifiP2pConfig device, int position) {
        WifiP2pConfig config = new WifiP2pConfig();
        if (config != null && config.deviceAddress != null && device != null) {
            config.deviceAddress = device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            pDialog.show();            //send dynamic image
        } else {

        }
    }

    @Override
    public void disconnect() {
        ((DeviceActionListener) getActivity()).disconnect();
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
        pDial.dismiss();
        updateValuesafterCreditsSent();
    }

    private void updateValuesafterCreditsSent() {
        if(!amtEntered.getText().toString().equalsIgnoreCase("")){
            leftBalance = SharedPreferencesHandler.getFloatValues(getActivity(), "balance") - Float.parseFloat(amtEntered.getText().toString());
            Log.i(TAG, "onEditorAction: " + leftBalance);
            amountSent=Float.parseFloat(amtEntered.getText().toString());
            Log.i(TAG, "updateValuesafterCreditsSent: amount"+amountSent);
            SharedPreferencesHandler.setFloatValues(getActivity(), "balance", leftBalance);
            mListener.walletBalanceUpdate(SharedPreferencesHandler.getFloatValues(getActivity(), "balance"));
            Log.i(TAG, "updateValuesafterCreditsSent: " + SharedPreferencesHandler.getFloatValues(getActivity(), "balance"));
            amtEntered.setText("");
        }
    }


    private void pickFilesSingle() {
        filePicker = new FilePicker(getActivity());
        filePicker.setFilePickerCallback(this);
        filePicker.setFolderName(getActivity().getString(R.string.app_name));
        filePicker.pickFile();
    }


    @Override
    public void onError(String message) {
        Log.i(TAG, "onError:Some ERRROR OCCURRED ");
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
        /*if (pDial == null)
            pDial = new ProgressDialog(mFilecontext,
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
                            pDial.setMessage("Receiving...");
                            pDial.setIndeterminate(false);
                            pDial.setMax(100);
                            pDial.setProgress(0);
                            pDial.setProgressNumberFormat(null);
                            pDial
                                    .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            pDial.show();
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
                if(e.equals("ADDRINUSE")){
                }
                Log.e(TransferActivity.TAG, e.getMessage());
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
       /* if (pDial == null) {
            pDial = new ProgressDialog(mFilecontext);
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
        SharedPreferencesHandler.setFloatValues(activity,"balance",bal);
        showRecieveAlert(activity, valueUpdated, payment);
    }

    private static void showRecieveAlert(final Activity activity, float money, final Payment payment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.AppTheme);
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
        OnlinePaymentRequest receiveRequest = new OnlinePaymentRequest(ONLINE, payments, MyPreferences.getInstance(GlobalActivity.getGlobalContext()).getPlaceObj(MyPreferences.Keys.TOKEN), MyPreferences.getInstance(GlobalActivity.getGlobalContext()).getPlaceObj(MyPreferences.Keys.USERID));
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
                    pDial.setProgress(Percentage);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    Percentage = 0;
                    ActualFilelength = 0;
                }
            }
            if (pDial != null) {
                if (pDial.isShowing()) {
                    pDial.dismiss();
                }
            }

            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(TransferActivity.TAG, e.toString());
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
//                    pDial.setProgress(progresspercentage);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    if (pDial != null) {
                        if (pDial.isShowing()) {
                            pDial.dismiss();
                        }
                    }
                }
            }
            // dismiss progress after sending
            if (pDial != null) {
                if (pDial.isShowing()) {
                    pDial.dismiss();
                }
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(TransferActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    public void showprogress(final String task) {
        if (pDial == null) {
            pDial = new ProgressDialog(getActivity(),
                    R.style.DialogTheme);
        }
        Handler handle = new Handler();
        final Runnable send = new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                if(pDial!=null){
                    pDial.setMessage(task);
                    // pDial.setProgressNumberFormat(null);
                    // pDial.setProgressPercentFormat(null);
                    pDial.setIndeterminate(false);
                    pDial.setMax(100);
//				pDial.setCancelable(false);
                    pDial.setProgressNumberFormat(null);
                    pDial
                            .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDial.show();
                }
            }
        };
        handle.post(send);
    }

    public static void DismissProgressDialog() {
        try {
            if (pDial != null && pDial.isShowing()) {
                    pDial.dismiss();
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
                Log.i(TAG, "onReceive: Device not available");
                bal = SharedPreferencesHandler.getFloatValues(context, "balance") + amountSent;
                Log.e(TAG, "onReceive: "+bal+"~~~~~"+amountSent);
                amountSent=0;
                SharedPreferencesHandler.setFloatValues(getContext(),"balance",bal);        //saved updated values
                mListener.walletBalanceUpdate(bal);
                ((DeviceActionListener) getActivity()).disconnect();
            }
        };
        getActivity().registerReceiver(updateUIReciver,filter);
    }

}
