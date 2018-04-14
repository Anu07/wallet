package app.src.com.walletapp.wifip2p.wifi;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.MyDeviceEvent;
import app.src.com.walletapp.utils.MyPreferences;
import app.src.com.walletapp.view.fragment.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by SONY on 3/12/2018.
 */


public class BarcodeFragment extends BaseFragment {
    private static final String TAG = BarcodeFragment.class.getSimpleName();
    @BindView(R.id.scan_code_own)
    ImageView scanCodeOwn;
    Unbinder unbinder;
    ProgressDialog dialog;
    private Bitmap bitmap;
    private ProgressDialog pDial;
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    public BarcodeFragment() {
        // Required empty public constructor
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle state) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barcode, container, false);
        unbinder = ButterKnife.bind(this, view);
        if(!MyPreferences.getInstance(getActivity()).getPlaceObj(MyPreferences.Keys.DEVICEQR).equalsIgnoreCase("") ){

            String EncodedString = MyPreferences.getInstance(getActivity()).getPlaceObj(MyPreferences.Keys.DEVICEQR);
            byte[] b = Base64.decode(EncodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            scanCodeOwn.setImageBitmap(bitmap);
        }else{
            showQRGenerated();
        }

        return view;

    }

    @Subscribe
    public void onMyDeviceEvent(MyDeviceEvent event) {
        Log.i(TAG, "onMyDeviceEvent: ");
    }


    private void showQRGenerated() {
        try {
            bitmap = TextToImageEncode(getDeviceMacAddress());
            MyPreferences.getInstance(getActivity()).savePlaceObj(MyPreferences.Keys.DEVICEQR, encodeImageto64(bitmap));
            scanCodeOwn.setImageBitmap(bitmap);
//            pDial.dismiss();
        } catch (WriterException e) {
            e.printStackTrace();
        }
//        ((WiFiDirectActivity) getActivity()).scanSilentlyDevicesNearby();
    }

    private String encodeImageto64(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * To get MAC address of device
     * @return
     */

    private String getDeviceMacAddress() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        Log.i(TAG, "onCreateView: "+macAddress);
        return macAddress;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }



    /**
     * get QR code for your device
     */
    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    500, 500, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
//        MyPreferences.getInstance().writePreference(MyPreferences.Keys.QrImage,bitmap);
        return bitmap;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}


