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
 * Created by joedayz.
 */
public class FirebaseAPI {
    private DatabaseReference firebase;
    private ChildEventListener photosEventListener;

    public FirebaseAPI(DatabaseReference firebase) {
        this.firebase = firebase;
    }

    public void checkForData(final FirebaseActionListenerCallback listener){
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    listener.onSuccess();
                } else {
                    listener.onError(null);
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                listener.onError(firebaseError);
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
                public void onCancelled(DatabaseError firebaseError) {
                    listener.onCancelled(firebaseError);
                }
            };
            firebase.addChildEventListener(photosEventListener);
        }
    }

    public void unsubscribe() {
        firebase.removeEventListener(photosEventListener);
    }

    public String create() {
        return firebase.push().getKey();
    }

    public void update(Photo photo) {
        DatabaseReference reference = this.firebase.child(photo.getId());
        reference.setValue(photo);
    }

    public void remove(Photo photo, FirebaseActionListenerCallback listener) {
        firebase.child(photo.getId()).removeValue();
        listener.onSuccess();
    }

    public String getAuthEmail(){
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(null);
                    }
                });

    }

    public void login(String email, String password, final FirebaseActionListenerCallback listener){
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //myUserReference = helper.getMyUserReference();
                            myUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                   listener.onSuccess();
                                }
                                @Override
                                public void onCancelled(DatabaseError firebaseError) {
                                   listener.onError(firebaseError);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //postEvent(LoginEvent.onSignInError, e.getMessage());
                        }
                    });
        } catch (Exception e) {
            //postEvent(LoginEvent.onSignInError, e.getMessage());
        }

    }

    public void checkForSession(FirebaseActionListenerCallback listener) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            listener.onSuccess();
        } else {
            listener.onError(null);
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}
