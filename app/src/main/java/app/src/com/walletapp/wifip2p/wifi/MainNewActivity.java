package app.src.com.walletapp.wifip2p.wifi;

import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.OfflineEvent;
import app.src.com.walletapp.utils.CommonUtils;
import app.src.com.walletapp.view.activity.BaseActivity;
import app.src.com.walletapp.wifip2p.utils.PermissionsAndroid;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import app.src.com.walletapp.wifip2p.utils.ViewPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static app.src.com.walletapp.sql.SQLiteHelper.DATABASE_NAME;
import static app.src.com.walletapp.wifip2p.wifi.TransferActivity.TAG;

/**
 * Created by SONY on 4/1/2018.
 */

public class MainNewActivity extends BaseActivity{


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private int[] tabIcons = {R.drawable.home, R.drawable.history, R.drawable.setting, R.drawable.profile};
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private boolean doubleBackToExitPressedOnce=false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_app);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initFirebase();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        initWifiP2pConnection();
        setupViewPager(viewPager);
        tab.setupWithViewPager(viewPager);
        setupTabIcons();
      /*  if(getIntent().hasExtra("QR")){
            GlobalActivity.userType="S";
            scanDevicesNearby();
        }*/
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOfflineEvent(OfflineEvent event) {
        Log.i(TAG, "onOfflineEvent: ");
    }

    private void initFirebase() {
        FirebaseCrash.log("Activity created");
        Log.i(TAG, "onCreate: ");
        if (Build.VERSION.SDK_INT >= 23) {
            checkStoragePermission();
        }
        Log.i(TAG, "onCreate: " + CommonUtils.doesDatabaseExist(this, DATABASE_NAME));
        SharedPreferencesHandler.setImage(this, "Image", R.drawable.ic_hamburger);
        Fabric.with(this, new Crashlytics());

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

    private void setupViewPager(final ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new ProgressFragment(), "History");
        adapter.addFragment(new SettingsFragment(), "Settings");
        adapter.addFragment(new ProfileFragment(), "Profile");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                adapter.notifyDataSetChanged();
                tab.setupWithViewPager(viewPager);
                setupTabIcons();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setupTabIcons() {
        tab.getTabAt(0).setIcon(tabIcons[0]);
        tab.getTabAt(1).setIcon(tabIcons[1]);
        tab.getTabAt(2).setIcon(tabIcons[2]);
        tab.getTabAt(3).setIcon(tabIcons[3]);
    }


    /**
     * Wifi P2p Connection
     */

    private void initWifiP2pConnection() {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
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

}
