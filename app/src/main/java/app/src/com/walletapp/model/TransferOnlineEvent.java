package app.src.com.walletapp.model;

/**
 * Created by SONY on 4/22/2018.
 */

public class TransferOnlineEvent {
    String mMrchntId;
    String mAmount;


    public String getmMrchntId() {
        return mMrchntId;
    }

    public void setmMrchntId(String mMrchntId) {
        this.mMrchntId = mMrchntId;
    }

    public String getmAmount() {
        return mAmount;
    }

    public void setmAmount(String mAmount) {
        this.mAmount = mAmount;
    }

    public TransferOnlineEvent(String mMrchntId, String mAmount) {

        this.mMrchntId = mMrchntId;
        this.mAmount = mAmount;
    }
}
