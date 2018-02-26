
package app.src.com.walletapp.model.onlinepayment;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Payment {

    @SerializedName("amount")
    private String mAmount;
    @SerializedName("currency")
    private String mCurrency;
    @SerializedName("details")
    private String mDetails;
    @SerializedName("device_address")
    private String mDeviceAddress;
    @SerializedName("device_id")
    private String mDeviceId;
    @SerializedName("receiver_id")
    private String mReceiverId;
    @SerializedName("timestamp")
    private String mTimestamp;
    @SerializedName("sender_id")
    private String mSenderId;

    public String getmTransactionId() {
        return mTransactionId;
    }

    public void setmTransactionId(String mTransactionId) {
        this.mTransactionId = mTransactionId;
    }

    @SerializedName("txn_id")
    private String mTransactionId;
    public Payment(String mAmount, String mCurrency, String mDetails, String mDeviceAddress, String mDeviceId, String mReceiverId, String mTimestamp, String transactionId,String senderId) {
        this.mAmount = mAmount;
        this.mCurrency = mCurrency;
        this.mDetails = mDetails;
        this.mDeviceAddress = mDeviceAddress;
        this.mDeviceId = mDeviceId;
        this.mReceiverId = mReceiverId;
        this.mTimestamp = mTimestamp;
        this.mTransactionId=transactionId;
        this.mSenderId=senderId;
    }


    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        mDeviceAddress = deviceAddress;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        mDeviceId = deviceId;
    }

    public String getReceiverId() {
        return mReceiverId;
    }

    public void setReceiverId(String receiverId) {
        mReceiverId = receiverId;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }

}
