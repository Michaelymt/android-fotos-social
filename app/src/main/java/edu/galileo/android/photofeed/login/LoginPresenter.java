package edu.galileo.android.photofeed.login;

import edu.galileo.android.photofeed.login.events.LoginEvent;

/**
 * Created by ykro.
 */
public interface LoginPresenter {
    void onCreate();
    void onDestroy();
    void checkForAuthenticatedUser();
    void onEventMainThread(LoginEvent event);
    void validateLogin(String email, String password);
    void registerNewUser(String email, String password);
}
