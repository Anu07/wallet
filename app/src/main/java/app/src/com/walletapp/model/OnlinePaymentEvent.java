package app.src.com.walletapp.model;

/**
 * Created by SONY on 4/13/2018.
 */

public class OnlinePaymentEvent {
    String mId;
    public OnlinePaymentEvent(String id) {
        this.mId=id;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }
}
