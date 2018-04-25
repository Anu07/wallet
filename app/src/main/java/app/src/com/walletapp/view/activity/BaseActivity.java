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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.OfflineEvent;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        dialog = new Dialog(this, android.R.style.Theme_Translucent);
        View views = LayoutInflater.from(this).inflate(R.layout.dot_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(views);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOfflineEvent(OfflineEvent event) {
        Log.i(TAG, "onOfflineEvent: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
