package app.src.com.walletapp.model;

/**
 * Created by SONY on 4/5/2018.
 */

public class OfflineEvent {


    String send,recieve;

    public OfflineEvent(String send, String recieve) {
        this.send = send;
        this.recieve = recieve;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getRecieve() {
        return recieve;
    }

    public void setRecieve(String recieve) {
        this.recieve = recieve;
    }
}
