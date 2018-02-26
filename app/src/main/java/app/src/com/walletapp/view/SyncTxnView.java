package app.src.com.walletapp.view;


import app.src.com.walletapp.model.generic.GenericResponse;
import app.src.com.walletapp.view.BaseView;

/**
 * Created by Anu Bhalla on 25/02/18.
 */


public interface SyncTxnView extends BaseView {

    void onSuccessfullySyncing(GenericResponse response);

}
