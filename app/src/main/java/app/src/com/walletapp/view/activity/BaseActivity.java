package app.src.com.walletapp.view.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import app.src.com.walletapp.R;
import app.src.com.walletapp.view.BaseView;


/**
 * Created by Anu Bhalla on 25/02/18.
 */

public class BaseActivity extends AppCompatActivity implements BaseView {

    private Dialog dialog;
    private String TAG=BaseActivity.class.getName();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new Dialog(this, android.R.style.Theme_Translucent);
        View views = LayoutInflater.from(this).inflate(R.layout.dot_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(views);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void showProgress() {
        dialog.show();
    }

    @Override
    public void hideProgress() {
        dialog.dismiss();
    }

    @Override
    public void showError(String error) {
        Log.i(TAG, "showError: "+error);
        Snackbar sBar=Snackbar.make(getWindow().getDecorView(),error,Snackbar.LENGTH_LONG);
        View snackBarView = sBar.getView();
        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        sBar.show();
    }
}
