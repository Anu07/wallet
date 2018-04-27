package app.src.com.walletapp.model;

/**
 * Created by SONY on 4/13/2018.
 */

public class OnlinePaymentEvent {
    String mId;
    String payment;

    public OnlinePaymentEvent(String mId, String payment) {
        this.mId = mId;
        this.payment = payment;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
