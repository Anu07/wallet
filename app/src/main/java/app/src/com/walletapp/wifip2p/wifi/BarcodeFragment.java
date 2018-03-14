package app.src.com.walletapp.wifip2p.wifi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.List;

import app.src.com.walletapp.R;
import app.src.com.walletapp.view.fragment.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import info.androidhive.barcode.BarcodeReader;

/**
 * Created by SONY on 3/12/2018.
 */


public class BarcodeFragment extends BaseFragment implements BarcodeReader.BarcodeReaderListener {
    private static final String TAG = BarcodeFragment.class.getSimpleName();
    @BindView(R.id.scan_code_own)
    ImageView scanCodeOwn;
    Unbinder unbinder;
    @BindView(R.id.scanLayout)
    RelativeLayout scanLayout;
    ProgressDialog progressBar;
    private BarcodeReader barcodeReader;
    private Bitmap bitmap;
    private static ProgressDialog progressDialogStat;



    public BarcodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barcode, container, false);
        barcodeReader = (BarcodeReader) getChildFragmentManager().findFragmentById(R.id.barcode_fragment);
        barcodeReader.setListener(this);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments().containsKey("QRgen")) {
            scanLayout.setVisibility(View.GONE);
            scanCodeOwn.setVisibility(View.VISIBLE);
            showQRGenerated((WifiP2pDevice) getArguments().getParcelable("device"));
        }
        return view;
    }

    private void showQRGenerated(WifiP2pDevice device) {
        try {
            bitmap = TextToImageEncode(device.deviceAddress);
            scanCodeOwn.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        ((WiFiDirectActivity) getActivity()).scanSilentlyDevicesNearby();

    }

    @Override
    public void onScanned(final Barcode barcode) {
        Log.e(TAG, "onScanned: " + barcode.displayValue);
        barcodeReader.playBeep();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Barcode: " + barcode.displayValue, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(),WiFiDirectActivity.class).putExtra("QR",""));
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Barcodes: " + finalCodes, Toast.LENGTH_SHORT).show();
            }
        });
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


