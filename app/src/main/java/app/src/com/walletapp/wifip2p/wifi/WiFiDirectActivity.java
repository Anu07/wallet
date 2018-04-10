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

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;

import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.OfflineEvent;
import app.src.com.walletapp.presenter.LoginPresenter;
import app.src.com.walletapp.sql.SQLiteHelper;
import app.src.com.walletapp.utils.CommonUtils;
import app.src.com.walletapp.utils.WalletBalanceListener;
import app.src.com.walletapp.wifip2p.GlobalActivity;
import app.src.com.walletapp.wifip2p.WiFiPeerListAdapter;
import app.src.com.walletapp.wifip2p.utils.PermissionsAndroid;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import app.src.com.walletapp.wifip2p.utils.ShowMyInformation;
import app.src.com.walletapp.wifip2p.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

import static app.src.com.walletapp.sql.SQLiteHelper.*;


/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class WiFiDirectActivity extends AppCompatActivity implements ChannelListener, DeviceActionListener, WalletBalanceListener, ShowMyInformation, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.scanqr)
    Button scanqr;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.getownqr)
    Button getownqr;
    private Toolbar mToolbar;
    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    public static String FILE_FINISHED = "File";
    private boolean retryChannel = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    static int[] drawableImg = {R.drawable.ic_steak, R.drawable.ic_hamburger, R.drawable.ic_broccoli};
    private TextView wallet;
    private Button sendCredits;
    float wallet_curr;
    private WifiP2pManager p2pManager;
    private Button trnsfr_bttn;
    private LinearLayout fragmentsLayout;
    private Dialog dialog;
    LoginPresenter mPresenter;
    @BindView(R.id.container)
    FrameLayout mContainer;
    private TextView walletMsg;
    public boolean failedERROR = false;
    private Button recieve_bttn;
    private float mAmtUpdated = 0;
    public SQLiteHelper mHelper;
    WifiP2pDevice mDevice;
    private boolean doubleBackToExitPressedOnce=false;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        initViews();
        mHelper = new SQLiteHelper(this);
//        setNavigationView();
        FirebaseCrash.log("Activity created");
        Log.i(TAG, "onCreate: ");
        if (Build.VERSION.SDK_INT >= 23) {
            checkStoragePermission();
        }
        Log.i(TAG, "onCreate: " + CommonUtils.doesDatabaseExist(this, DATABASE_NAME));
        SharedPreferencesHandler.setImage(this, "Image", R.drawable.ic_hamburger);
        Fabric.with(this, new Crashlytics());
        mHelper = new SQLiteHelper(this);
//        mHelper.getAllDevices();

        if(getIntent().hasExtra("QR")){
            GlobalActivity.userType="S";
            scanDevicesNearby();
        }
    }


    @Subscribe
    public void onOfflineEvent(OfflineEvent event) {
        Log.i(TAG, "onOfflineEvent: ");
    }

    private void setNavigationView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView ustext = header.findViewById(R.id.usname);
        TextView phtext = header.findViewById(R.id.phone);
        ustext.setText("Sam Martin");
        phtext.setText("5014995222");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    /*
      Ask permissions for Filestorage if device api > 23
       */
    private void checkStoragePermission() {
        boolean isExternalStorage = PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(this);
        if (!isExternalStorage) {
            PermissionsAndroid.getInstance().requestForWriteExternalStoragePermission(this);
        }
    }

    private void initViews() {
        // add necessary intent values to be matched.
        mToolbar = findViewById(R.id.toolbar);
        wallet = findViewById(R.id.wallet_balance);
        walletMsg = findViewById(R.id.wallet_balance_msg);
        trnsfr_bttn = findViewById(R.id.transfer_bttn);
        recieve_bttn = findViewById(R.id.recieve_bttn);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Wallet App");
        walletMsg.setText(getResources().getString(R.string.your_wallet_balance) + ": " + "$");            //To be replaced with real time currency
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (!SharedPreferencesHandler.getSharedPreferences(this).contains("balance")) {
            wallet.setText("100.00");
            SharedPreferencesHandler.setFloatValues(this, "balance", 100f);
        } else {
            wallet.setText(""+(SharedPreferencesHandler.getFloatValues(this,"balance")));
        }
        channel = manager.initialize(this, getMainLooper(), this);
        if (!isWifiP2pEnabled) {
            turnOnWifi();
        }
        if (!wallet.getText().toString().equalsIgnoreCase("NA")) {
            wallet_curr = Float.parseFloat(wallet.getText().toString());
        }
        if (getIntent().hasExtra("balance")) {
            float newBal = wallet_curr + getIntent().getIntExtra("balance", 0);
            wallet.setText(""+newBal);
            SharedPreferencesHandler.setFloatValues(this, "balance", newBal);
        }

        scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putParcelable("device",mDevice);
                Fragment barcodeFrag=new BarcodeFragment();
                barcodeFrag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.container, barcodeFrag).addToBackStack("qr").commit();
            }
        });

        recieve_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GlobalActivity.userType="R";            //setting usertype to reciever

                if (!isWifiP2pEnabled) {
                    turnOnWifi();
                } else {
                    disconnect();
                    scanDevicesNearby();
                }
            }

        });


        trnsfr_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GlobalActivity.userType="S";            //set user type to sender
                if (!isWifiP2pEnabled) {
                    turnOnWifi();
                } else {
                    disconnect();
                    scanDevicesNearby();
                }
            }

        });
    }

    public void scanDevicesNearby() {
        DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        fragment.onInitiateDiscovery();
        manager.discoverPeers(channel, new ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(WiFiDirectActivity.this, "Scanning nearby devices....",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(WiFiDirectActivity.this, "Scanning failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void turnOnWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }


    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        wallet = findViewById(R.id.wallet_balance);
//        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
        if (SharedPreferencesHandler.getSharedPreferences(this).contains("balance")) {
            wallet.setText(Utils.getFloatFormatter(SharedPreferencesHandler.getFloatValues(this, "balance")));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public void showDetails(WifiP2pDevice device, int position) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.showDetails(device, position);
    }

    @Override
    public void connect(final WifiP2pConfig config, final int position) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: Accepted");
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                DeviceListFragment.refreshList(config, drawableImg[setRandomIcon(position)], position);
//                Toast.makeText(WiFiDirectActivity.this,"Image",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int reason) {
                Log.i(TAG, "onSuccess: Rejected");
                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int setRandomIcon(int position) {
        Log.i(TAG, "setRandomIcon: " + (new Random()).nextInt(position + 1));
        return (new Random()).nextInt(position + 1);
    }

    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        fragment.getView().setVisibility(View.GONE);

        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                if (wifiP2pGroup != null) {
                    manager.removeGroup(channel, new ActionListener() {

                        @Override
                        public void onFailure(int reasonCode) {
                            Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                        }

                        @Override
                        public void onSuccess() {
                            //TODO

                            fragment.getView().setVisibility(View.GONE);
                        }

                    });
                } else {
                    failedERROR = true;
                    fragment.getView().setVisibility(View.GONE);
                    Log.e(TAG, "onGroupInfoAvailable: Dismiss");
                }
            }
        });


    }


    /**
     * To resolve error in disconnection returning response error code:2
     */

    private void deletePersistentGroups() {
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(manager, channel, netid, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiDirectActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Fragment> listOfFragments = getSupportFragmentManager().getFragments();

        if (listOfFragments.size() >= 1) {
            for (Fragment fragment : listOfFragments) {
                if (fragment instanceof DeviceDetailFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
//        (getSupportFragmentManager().findFragmentById(R.id.device_detail_container)).
//                onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void myDetails(WifiP2pDevice device) {
        TextView view = findViewById(R.id.my_name);
        view.setText(device.deviceName);
        view = findViewById(R.id.my_status);
        view.setText(WiFiPeerListAdapter.getDeviceStatus(device.status));
        ImageView img = findViewById(R.id.icon);
        img.setImageResource(R.drawable.ic_hamburger);
        SharedPreferencesHandler.setStringValues(WiFiDirectActivity.this,"ownAddress",device.deviceAddress);
        mDevice=device;
    }

    @Override
    public void disConnectAll() {
        disconnect();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void walletBalanceUpdate(float amt) {
        mAmtUpdated = amt;
        Log.i(TAG, "walletBalanceUpdate: " + mAmtUpdated);
        updateGoldTextView(mAmtUpdated);

    }

    public void updateGoldTextView(float goldAmount) {
        wallet.setText(Utils.getFloatFormatter(goldAmount));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.getownqr)
    public void onViewClicked() {
        Bundle bundle=new Bundle();
        bundle.putString("QRgen","qrgenerate");
        bundle.putParcelable("device",mDevice);
        Fragment barCode=new BarcodeFragment();
        barCode.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, barCode).commit();
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    public void scanSilentlyDevicesNearby() {
        DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
//        fragment.onInitiateDiscovery();
        manager.discoverPeers(channel, new ActionListener() {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reasonCode) {
            }
        });

    }
}
