package edu.galileo.android.photofeed.domain;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by josediaz on 10/31/16.
 */

public interface FirebaseEventListenerCallback {

    void onChildAdded(DataSnapshot dataSnapshot);
    void onChildRemoved(DataSnapshot dataSnapshot);
    void onCancelled(DatabaseError error);
}
