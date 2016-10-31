package edu.galileo.android.photofeed.domain;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import edu.galileo.android.photofeed.entities.Photo;

/**
 * Created by josediaz on 10/30/16.
 */

public class FirebaseAPI {

    private DatabaseReference dataReference;
    private ChildEventListener photosEventListener;

    public FirebaseAPI(DatabaseReference databaseReference){
        dataReference = databaseReference;
    }

    public void checkForData(final FirebaseActionListenerCallback listener){
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    listener.onSuccess();
                } else {
                    listener.onError(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError);
            }
        });
    }

    public void subscribe(final FirebaseEventListenerCallback listener) {
        if (photosEventListener == null) {

            photosEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    listener.onChildAdded(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    listener.onChildRemoved(dataSnapshot);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            };
            dataReference.addChildEventListener(photosEventListener);
        }
    }

    public void unsubscribe() {
        dataReference.removeEventListener(photosEventListener);
    }


    public String create() {
        return dataReference.push().getKey();
    }

    public void update(Photo photo) {
        DatabaseReference reference = dataReference.child(photo.getId());
        reference.setValue(photo);
    }


    public void remove(Photo photo, FirebaseActionListenerCallback listener) {
        dataReference.child(photo.getId()).removeValue();
        listener.onSuccess();
    }

    public String getAuthEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = null;
        if (user != null) {
            email = user.getEmail();
        }
        return email;
    }



    public void signUp(String email, String password, final FirebaseActionListenerCallback listener){

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    listener.onSuccess();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.onException(e);
                }
        });


    }


    public void login(String email, String password, final FirebaseActionListenerCallback listener){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onException(e);
                    }
                });


    }


    public void checkForSession(FirebaseActionListenerCallback listener) {
        if (FirebaseAuth.getInstance() != null) {
            listener.onSuccess();
        } else {
            listener.onError(null);
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

}
