package edu.galileo.android.photofeed.login.ui;

/**
 * Created by joedayz.
 */
public interface LoginView {
    void enableInputs();
    void disableInputs();
    void showProgress();
    void hideProgress();

    void handleSignUp();
    void handleSignIn();


    void navigateToMainScreen();
    void loginError(String error);

    void newUserSuccess();
    void newUserError(String error);


    void setUserEmail(String email);
}
