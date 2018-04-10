package app.src.com.walletapp.wifip2p.wifi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import app.src.com.walletapp.R;
import app.src.com.walletapp.view.fragment.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by SONY on 4/10/2018.
 */

public class TransferOnlineFragment extends BaseFragment {

    @BindView(R.id.amount_ed)
    TextInputEditText amountEd;
    @BindView(R.id.submit_bttn)
    Button submitBttn;
    @BindView(R.id.homeLayout)
    RelativeLayout homeLayout;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_transfer_online, container
                , false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.submit_bttn)
    public void onViewClicked() {
        if(!amountEd.getText().toString().isEmpty()){
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.tab_container, new TransferOnlineAmtFragment(), "amt").addToBackStack("amt").commit();
        }else{
            Toast.makeText(getActivity(),"Please fill merchant Id",Toast.LENGTH_LONG).show();
        }
    }
}
