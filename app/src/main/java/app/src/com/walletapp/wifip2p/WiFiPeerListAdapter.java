package app.src.com.walletapp.wifip2p;
/**
 * Created by Anu Bhalla on 2/21/18.
 */


import android.content.Context;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.src.com.walletapp.R;
import app.src.com.walletapp.wifip2p.wifi.WiFiDirectActivity;


/**
 * Array adapter for ListFragment that maintains WifiP2pDevice list.
 */
public class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

    private List<WifiP2pDevice> items;
    private int[] paths;
    ImageView img;
    /**
     * @param context
     * @param textViewResourceId
     * @param objects
     * @param path
     */
    public WiFiPeerListAdapter(Context context, int textViewResourceId,
                               List<WifiP2pDevice> objects, int[] path) {
        super(context, textViewResourceId, objects);
        items = objects;
        paths=path;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_devices, null);
        }
        WifiP2pDevice device = items.get(position);
        if (device != null) {
            TextView hot = v.findViewById(R.id.device_hotspot);
            TextView top = v.findViewById(R.id.device_name);
            TextView bottom = v.findViewById(R.id.device_details);
            img = v.findViewById(R.id.icon);
            if (top != null) {
                hot.setText(device.deviceAddress);
                top.setText(device.deviceName);
            }
            if (bottom != null) {
                    bottom.setText(getDeviceStatus(device.status));
                    bottom.setTextColor(getTextColor(device.status));
            }
            img.setImageResource(paths[position]);
        }
        return v;

    }

    private int getTextColor(int status) {
        switch (status) {
            case WifiP2pDevice.AVAILABLE:
                return Color.GREEN;
            case WifiP2pDevice.INVITED:
                return Color.CYAN;
            case WifiP2pDevice.CONNECTED:
                return Color.BLUE;
            case WifiP2pDevice.FAILED:
                return Color.RED;
            case WifiP2pDevice.UNAVAILABLE:
                return Color.RED;
            default:
                return Color.RED;

        }
    }

    public static String getDeviceStatus(int deviceStatus) {
        Log.d(WiFiDirectActivity.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    public void refresh(int pos,int mdrawable) {
//        paths=mdrawable;
        paths[pos]=mdrawable;
        notifyDataSetChanged();
    }
}

