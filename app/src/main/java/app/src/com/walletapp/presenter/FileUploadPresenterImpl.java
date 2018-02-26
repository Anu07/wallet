package app.src.com.walletapp.presenter;

import app.src.com.walletapp.view.SyncTxnView;

/**
 * Created by Anu Bhalla on 23/02/18.
 */

public class FileUploadPresenterImpl implements FileUploadPresenter {

    SyncTxnView mView;

    public FileUploadPresenterImpl(SyncTxnView mView) {
        this.mView = mView;
    }


}
