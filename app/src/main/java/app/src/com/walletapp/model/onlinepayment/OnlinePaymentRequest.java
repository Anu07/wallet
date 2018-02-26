
package app.src.com.walletapp.model.onlinepayment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class OnlinePaymentRequest {

    @SerializedName("payment_status")
    private String mPaymentStatus;
    @SerializedName("payments")
    private List<Payment> mPayments;
    @SerializedName("token")
    private String mToken;
    @SerializedName("user_id")
    private String mUserId;

    public OnlinePaymentRequest(String mPaymentStatus, List<Payment> mPayments, String mToken, String mUserId) {
        this.mPaymentStatus = mPaymentStatus;
        this.mPayments = mPayments;
        this.mToken = mToken;
        this.mUserId = mUserId;
    }

    public String getPaymentStatus() {
        return mPaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        mPaymentStatus = paymentStatus;
    }

    public List<Payment> getPayments() {
        return mPayments;
    }

    public void setPayments(List<Payment> payments) {
        mPayments = payments;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

}
