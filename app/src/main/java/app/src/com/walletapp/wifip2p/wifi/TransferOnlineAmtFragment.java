package app.src.com.walletapp.wifip2p.wifi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.src.com.walletapp.R;
import app.src.com.walletapp.view.fragment.BaseFragment;

/**
 * Created by SONY on 4/10/2018.
 */

public class TransferOnlineAmtFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_transfer_amt_online, container
                , false);
        return view;
    }
}
