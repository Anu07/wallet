package com.src.paymemi.model.SendCredit;

/**
 * Created by insonix on 27/10/17.
 */

public class CreditModel {

    String userId="";
    String amount="";
    String TxnId="";


    public CreditModel(String userId, String amount, String txnId) {
        this.userId = userId;
        this.amount = amount;
        TxnId = txnId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTxnId() {
        return TxnId;
    }

    public void setTxnId(String txnId) {
        TxnId = txnId;
    }
}
