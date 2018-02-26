package app.src.com.walletapp.view;


import app.src.com.walletapp.model.login.LoginResponse;

/**
 * Created by Anu Bhalla on 25/02/18.
 */


public interface LoginView extends BaseView {

    void onSuccess(LoginResponse msg);
}
