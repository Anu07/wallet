package app.src.com.walletapp.wifip2p.wifi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;

import app.src.com.walletapp.R;
import app.src.com.walletapp.view.fragment.BaseFragment;

/**
 * Created by SONY on 4/5/2018.
 */

public class ProgressFragment extends BaseFragment {


    @Subscribe
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.progress_layout,container,false);
        return v;
    }
}
