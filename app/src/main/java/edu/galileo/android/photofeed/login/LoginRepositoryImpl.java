package edu.galileo.android.photofeed.login;

import com.google.firebase.database.DatabaseError;

import edu.galileo.android.photofeed.domain.FirebaseAPI;
import edu.galileo.android.photofeed.domain.FirebaseActionListenerCallback;
import edu.galileo.android.photofeed.lib.base.EventBus;
import edu.galileo.android.photofeed.login.events.LoginEvent;

/**
 * Created by ykro.
 * Updated by joedayz.
 */
public class LoginRepositoryImpl implements LoginRepository {
    private EventBus eventBus;
    private FirebaseAPI firebase;

    public LoginRepositoryImpl(FirebaseAPI firebaseAPI, EventBus eventBus) {
        this.eventBus = eventBus;
        firebase = firebaseAPI;
    }


    @Override
    public void signUp(final String email, final String password) {
        firebase.signUp(email, password, new FirebaseActionListenerCallback() {
            @Override
            public void onSuccess() {
                postEvent(LoginEvent.onSignUpSuccess);
                signIn(email, password);
            }

            @Override
            public void onException(Exception ex) {
                postEvent(LoginEvent.onSignUpError, ex.getMessage());
            }

            @Override
            public void onError(DatabaseError error) {
                postEvent(LoginEvent.onSignUpError, error.getMessage());
            }
        });
    }

    @Override
    public void signIn(String email, String password) {

        if (email != null && password != null) {
            firebase.login(email, password, new FirebaseActionListenerCallback() {
                @Override
                public void onSuccess() {
                    String email = firebase.getAuthEmail();
                    postEvent(LoginEvent.onSignInSuccess, null, email);
                }

                @Override
                public void onException(Exception ex) {
                    postEvent(LoginEvent.onSignInError, ex.getMessage());
                }

                @Override
                public void onError(DatabaseError error) {
                    postEvent(LoginEvent.onSignInError, error.getMessage());
                }

            });
        } else {
            firebase.checkForSession(new FirebaseActionListenerCallback() {
                @Override
                public void onSuccess() {
                    String email = firebase.getAuthEmail();
                    postEvent(LoginEvent.onSignInSuccess, null, email);
                }

                @Override
                public void onException(Exception ex) {
                    postEvent(LoginEvent.onFailedToRecoverSession);
                }

                @Override
                public void onError(DatabaseError databaseError) {
                    postEvent(LoginEvent.onFailedToRecoverSession);
                }

            });
        }

    }




    private void postEvent(int type) {
        postEvent(type, null);
    }

    private void postEvent(int type, String errorMessage) {
        postEvent(type, errorMessage, null);
    }

    private void postEvent(int type, String errorMessage, String loggedUserEmail) {
        LoginEvent loginEvent = new LoginEvent();
        loginEvent.setEventType(type);
        loginEvent.setErrorMesage(errorMessage);
        loginEvent.setLoggedUserEmail(loggedUserEmail);
        eventBus.post(loginEvent);
    }

}
