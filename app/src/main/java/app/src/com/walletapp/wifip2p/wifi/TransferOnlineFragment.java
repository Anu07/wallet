package app.src.com.walletapp.wifip2p.wifi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.OnlinePaymentEvent;
import app.src.com.walletapp.model.TransferOnlineEvent;
import app.src.com.walletapp.view.fragment.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static app.src.com.walletapp.wifip2p.wifi.TransferActivity.TAG;

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
    @BindView(R.id.mrchnt_ed)
    TextInputEditText mrchntEd;
    @BindView(R.id.mrchntInput)
    TextInputLayout mrchntInput;
    @BindView(R.id.amountInput)
    TextInputLayout amountInput;
    private OnlinePaymentEvent mEvent;

    @Subscribe
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_transfer_online, container
                , false);
        unbinder = ButterKnife.bind(this, view);
        mrchntEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                EventBus.getDefault().postSticky(new TransferOnlineEvent(mrchntEd.getText().toString().trim(), ""));
            }
        });

        amountEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                EventBus.getDefault().postSticky(new TransferOnlineEvent(mrchntEd.getText().toString().trim(), amountEd.getText().toString().trim()));
                Log.i(TAG, "afterTextChanged: "+mrchntEd.getText().toString().trim()+"~~~"+amountEd.getText().toString().trim());
            }
        });


        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.submit_bttn)
    public void onViewClicked() {
        mEvent = EventBus.getDefault().getStickyEvent(OnlinePaymentEvent.class);
        if (mEvent != null) {
            Toast.makeText(getActivity(), "In progress", Toast.LENGTH_LONG).show();
            getActivity().startActivity(new Intent(getActivity(), MainNewActivity.class));
        } else {
            if (!mrchntEd.getText().toString().isEmpty()) {
                EventBus.getDefault().postSticky(new OnlinePaymentEvent(mrchntEd.getText().toString().trim()));
                mrchntInput.setVisibility(View.GONE);
                amountInput.setVisibility(View.VISIBLE);
            } else {
                mrchntInput.setVisibility(View.VISIBLE);
                amountInput.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Please fill merchant Id", Toast.LENGTH_LONG).show();
            }
        }

    }

}
