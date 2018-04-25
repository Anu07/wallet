package app.src.com.walletapp.wifip2p.wifi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.MyDeviceEvent;
import app.src.com.walletapp.utils.MyPreferences;
import app.src.com.walletapp.view.fragment.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.downld)
    ImageView downld;
    @BindView(R.id.share)
    ImageView share;
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
        if (!MyPreferences.getInstance(getActivity()).getPlaceObj(MyPreferences.Keys.DEVICEQR).equalsIgnoreCase("")) {

            String EncodedString = MyPreferences.getInstance(getActivity()).getPlaceObj(MyPreferences.Keys.DEVICEQR);
            byte[] b = Base64.decode(EncodedString, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            scanCodeOwn.setImageBitmap(bitmap);
        } else {
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
     *
     * @return
     */

    private String getDeviceMacAddress() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        Log.i(TAG, "onCreateView: " + macAddress);
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

    @OnClick({R.id.downld, R.id.share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.downld:
                takeScreenshot();
                break;
            case R.id.share:
                shareQRImage();
                break;
        }
    }


    /**
     * Take screenshot
     */

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getActivity(), "File saved at " + imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }


    /**
     * shareQRImage
     */


    public void shareQRImage() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "qr.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/qr.jpg"));
        startActivity(Intent.createChooser(share, "Share Image"));
    }


}


