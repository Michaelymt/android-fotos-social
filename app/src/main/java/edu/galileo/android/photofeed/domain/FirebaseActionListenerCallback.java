package edu.galileo.android.photofeed.domain;


import com.google.firebase.database.DatabaseError;

/**
 * Created by joedayz.
 */
public interface FirebaseActionListenerCallback {
    void onSuccess();
    void onError(DatabaseError error);
}
