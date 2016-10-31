package edu.galileo.android.photofeed.domain;

import com.google.firebase.database.DatabaseError;

/**
 * Created by josediaz on 10/31/16.
 */

public interface FirebaseActionListenerCallback {

    void onSuccess();
    void onException(Exception  ex);
    void onError(DatabaseError databaseError);
}
