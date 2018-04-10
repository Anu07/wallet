package app.src.com.walletapp.wifip2p.wifi;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.OfflineEvent;
import app.src.com.walletapp.sql.SQLiteHelper;
import app.src.com.walletapp.utils.WalletBalanceListener;
import app.src.com.walletapp.wifip2p.GlobalActivity;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import app.src.com.walletapp.wifip2p.utils.ShowMyInformation;
import app.src.com.walletapp.wifip2p.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static app.src.com.walletapp.wifip2p.wifi.WiFiDirectActivity.TAG;

/**
 * Created by SONY on 4/1/2018.
 */

public class HomeFragment extends Fragment implements DeviceActionListener, WalletBalanceListener, ShowMyInformation {

    @BindView(R.id.wallet_balance_msg)
    TextView walletBalanceMsg;
    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.onlineView)
    ImageView onlineView;
    @BindView(R.id.offlineView)
    ImageView offlineView;
    @BindView(R.id.qrView)
    ImageView qrView;
    @BindView(R.id.recieveView)
    ImageView recieveView;
    Unbinder unbinder;
    @BindView(R.id.deviceListLayout)
    RelativeLayout deviceListLayout;
    @BindView(R.id.homeLayout)
    LinearLayout homeLayout;
    @BindView(R.id.optionsLayout)
    LinearLayout optionsLayout;
    @BindView(R.id.tab_container)
    LinearLayout tabContainer;
    private SQLiteHelper mHelper;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private float wallet_curr = 0.0f;
    private boolean failedERROR = false;
    private WifiP2pDevice mDevice;
    private float mAmtUpdated;

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        initSqlite();
        setWalletBalance();
        return view;
    }

    /**
     * Wallet Balance
     */

    private void setWalletBalance() {
        if (!SharedPreferencesHandler.getSharedPreferences(getActivity()).contains("balance")) {
            walletBalance.setText("100.00");
            SharedPreferencesHandler.setFloatValues(getActivity(), "balance", 100f);
        } else {
            walletBalance.setText("$" + (SharedPreferencesHandler.getFloatValues(getActivity(), "balance")));
        }

        if (!walletBalance.getText().toString().equalsIgnoreCase("NA")) {
            wallet_curr = Float.parseFloat(walletBalance.getText().toString().replace("$", ""));
        }
        if (getActivity().getIntent().hasExtra("balance")) {
            float newBal = wallet_curr + getActivity().getIntent().getIntExtra("balance", 0);
            walletBalance.setText("$" + newBal);
            SharedPreferencesHandler.setFloatValues(getActivity(), "balance", newBal);
        }

    }


    /**
     * Sqlite connection
     */
    private void initSqlite() {
        mHelper = new SQLiteHelper(getActivity());
        mHelper = new SQLiteHelper(getActivity());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.onlineView, R.id.offlineView, R.id.qrView, R.id.recieveView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.onlineView:
                optionsLayout.setVisibility(View.GONE);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.tab_container, new TransferOnlineFragment(), "online").addToBackStack("online").commit();
                break;
            case R.id.offlineView:
                optionsLayout.setVisibility(View.VISIBLE);
                GlobalActivity.userType = "S";            //set user type to sender
                EventBus.getDefault().postSticky(new OfflineEvent("1", "0"));
                startActivity(new Intent(getActivity(), TransferActivity.class));
                break;
            case R.id.qrView:
                optionsLayout.setVisibility(View.VISIBLE);
                startActivity(new Intent(getActivity(), BarCodeScanActivity.class));
                break;
            case R.id.recieveView:
                optionsLayout.setVisibility(View.VISIBLE);
                GlobalActivity.userType = "R";            //setting usertype to reciever
                EventBus.getDefault().postSticky(new OfflineEvent("0", "1"));
                startActivity(new Intent(getActivity(), TransferActivity.class));
                break;
        }
    }

    /**
     * Scan nearby Devices
     */
    private void scanDevicesNearby() {

        startActivity(new Intent(getActivity(), TransferActivity.class));
    }


    @Override
    public void myDetails(WifiP2pDevice device) {
        SharedPreferencesHandler.setStringValues(getActivity(), "ownAddress", device.deviceAddress);
        mDevice = device;
    }

    @Override
    public void disConnectAll() {

    }

    @Override
    public void showDetails(WifiP2pDevice device, int position) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.showDetails(device, position);
    }

    @Override
    public void cancelDisconnect() {
 /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getActivity().getFragmentManager().findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(getActivity(), "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(getActivity(),
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    @Override
    public void connect(WifiP2pConfig config, int position) {

    }

    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        fragment.getView().setVisibility(View.GONE);

        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                if (wifiP2pGroup != null) {
                    manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

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

    @Override
    public void walletBalanceUpdate(float amt) {
        mAmtUpdated = amt;
        Log.i(TAG, "walletBalanceUpdate: " + mAmtUpdated);
        updateGoldTextView(mAmtUpdated);

    }

    public void updateGoldTextView(float goldAmount) {
        walletBalance.setText(Utils.getFloatFormatter(goldAmount));
    }

}
