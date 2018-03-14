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

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import app.src.com.walletapp.R;
import app.src.com.walletapp.sql.SQLiteHelper;
import app.src.com.walletapp.utils.MyPreferences;
import app.src.com.walletapp.wifip2p.ChangeDeviceImage;
import app.src.com.walletapp.wifip2p.WiFiPeerListAdapter;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import app.src.com.walletapp.wifip2p.utils.ShowMyInformation;


/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */
public class DeviceListFragment extends ListFragment implements PeerListListener, ChangeDeviceImage {

    static List<WifiP2pDevice> peers = new ArrayList<>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;
    static int[] drawable = {R.drawable.ic_steak, R.drawable.ic_hamburger};
    private static WiFiPeerListAdapter wAdapter;
    private ImageView peerList;
    private LinearLayout peersLay;
    private ListView PeerList;
    ShowMyInformation mOwnInfoListener;

    public DeviceListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wAdapter = new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers, drawable);
        this.setListAdapter(wAdapter);
        mOwnInfoListener=((WiFiDirectActivity)getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);
        peerList= mContentView.findViewById(R.id.peer_list_show);
        PeerList= mContentView.findViewById(android.R.id.list);
        peersLay= mContentView.findViewById(R.id.peers_lay);
        return mContentView;
    }

    /**2
     * @return this device
     */
    public WifiP2pDevice getDevice() {
        return device;
    }


    /**
     * Initiate a connection with the peer.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        ((DeviceActionListener) getActivity()).showDetails(device, position);
    }

    @Override
    public void onConnectChangeImage(String name) {

    }

    /**
     * Update UI for the current device.
     *
     * @param device WifiP2pDevice object
     */
    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
        mOwnInfoListener.myDetails(device);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((WiFiDirectActivity)getActivity()).mHelper.insertPeerRecord(peers, SharedPreferencesHandler.getFloatValues(getActivity(),"balance"));
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            mOwnInfoListener.disConnectAll();
            Log.d(WiFiDirectActivity.TAG, "No devices found");
            return;
        }

    }

    public void clearPeers() {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    /**
     *
     */
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding nearby devices", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }

    public static void refreshList(WifiP2pConfig config, int ic_steak, int position) {
//        drawable[position] = ic_steak;
        wAdapter.refresh(position,ic_steak);
    }
}
