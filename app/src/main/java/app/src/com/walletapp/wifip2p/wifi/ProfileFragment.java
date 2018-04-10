package app.src.com.walletapp.wifip2p.wifi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.src.com.walletapp.R;
import app.src.com.walletapp.view.fragment.BaseFragment;

/**
 * Created by SONY on 4/11/2018.
 */

public class ProfileFragment extends BaseFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment,container,false);
    }
}
