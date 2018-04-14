package app.src.com.walletapp.wifip2p.wifi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.src.com.walletapp.R;
import app.src.com.walletapp.view.fragment.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SONY on 4/11/2018.
 */

public class ProfileFragment extends BaseFragment {


    @BindView(R.id.usr_img)
    CircleImageView usrImg;
    @BindView(R.id.bal_txt)
    TextView balTxt;
    @BindView(R.id.mrchnt_id)
    TextView mrchntId;
    @BindView(R.id.user_info)
    RelativeLayout userInfo;
    @BindView(R.id.share_icon)
    ImageView shareIcon;
    @BindView(R.id.downld_qr)
    TextView downldQr;
    @BindView(R.id.refer_icon)
    ImageView referIcon;
    @BindView(R.id.refer_text)
    TextView referText;
    @BindView(R.id.logout_txt)
    TextView logoutTxt;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.downld_qr)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(),QRCodeActivity.class));
//        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container_current,new BarcodeFragment());

    }
}
