package app.src.com.walletapp.view;
/**
 * Created by Anu Bhalla on 25/02/18.
 */

public interface BaseView {

    void showProgress();

    void hideProgress();

    void showError(String error);
}
