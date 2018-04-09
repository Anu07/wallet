package app.src.com.walletapp.wifip2p.wifi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.List;

import app.src.com.walletapp.R;
import app.src.com.walletapp.view.fragment.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by SONY on 3/12/2018.
 */


public class BarcodeFragment extends BaseFragment implements
        ZXingScannerView.ResultHandler{
    private static final String TAG = BarcodeFragment.class.getSimpleName();
    @BindView(R.id.scan_code_own)
    ImageView scanCodeOwn;
    Unbinder unbinder;
    @BindView(R.id.scanLayout)
    RelativeLayout scanLayout;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle state) {

        if (getArguments().containsKey("QRgen")) {
            scanLayout.setVisibility(View.GONE);
//            pDial=new ProgressDialog(getContext());
//            pDial.setMessage("generating...");
//            pDial.show();
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_barcode, container, false);

            unbinder = ButterKnife.bind(this, view);

            scanCodeOwn.setVisibility(View.VISIBLE);
            showQRGenerated((WifiP2pDevice) getArguments().getParcelable("device"));
            return view;

        }else{
            if(state != null) {
                mFlash = state.getBoolean(FLASH_STATE, false);
                mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
                mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
                mCameraId = state.getInt(CAMERA_ID, -1);
            } else {
                mFlash = false;
                mAutoFocus = true;
                mSelectedIndices = null;
                mCameraId = -1;
            }
            setupFormats();                // Set the scanner view as the content view
            return mScannerView;
        }
    }

    private void showQRGenerated(WifiP2pDevice device) {
        try {
            bitmap = TextToImageEncode(device.deviceAddress);
            scanCodeOwn.setImageBitmap(bitmap);
//            pDial.dismiss();
        } catch (WriterException e) {
            e.printStackTrace();
        }
        ((WiFiDirectActivity) getActivity()).scanSilentlyDevicesNearby();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments().containsKey("QRgen")) {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera(mCameraId);
            mScannerView.setFlash(mFlash);
            mScannerView.setAutoFocus(mAutoFocus);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }


    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<>();
        if(mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<>();
            for(int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for(int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if(mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {}
    }

    /*@Override
    public void onScanned(final Barcode barcode) {
        Log.e(TAG, "onScanned: " + barcode.displayValue);
        barcodeReader.playBeep();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Barcode: " + barcode.displayValue, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), WiFiDirectActivity.class).putExtra("QR", ""));
            }
        });
    }



    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        Log.e(TAG, "onScannedMultiple: " + barcodes.size());

        String codes = "";
        for (Barcode barcode : barcodes) {
            codes += barcode.displayValue + ", ";
        }

        final String finalCodes = codes;

        Toast.makeText(getActivity(), "Barcodes: " + finalCodes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {
        Log.e(TAG, "onScanError: " + errorMessage);
    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getActivity(), "Camera permission denied!", Toast.LENGTH_LONG).show();
    }
*/

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
                        getResources().getColor(R.color.dialogplus_black_overlay) : getResources().getColor(R.color.white);
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


