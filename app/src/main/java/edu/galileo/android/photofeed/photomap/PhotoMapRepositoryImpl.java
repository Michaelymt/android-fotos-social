package edu.galileo.android.photofeed.photomap;



import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import edu.galileo.android.photofeed.domain.FirebaseAPI;
import edu.galileo.android.photofeed.domain.FirebaseEventListenerCallback;
import edu.galileo.android.photofeed.entities.Photo;
import edu.galileo.android.photofeed.lib.base.EventBus;
import edu.galileo.android.photofeed.photomap.events.PhotoMapEvent;

/**
 * Created by ykro.
 */
public class PhotoMapRepositoryImpl implements PhotoMapRepository {
    private EventBus eventBus;
    private FirebaseAPI firebase;

    public PhotoMapRepositoryImpl(FirebaseAPI firebase, EventBus eventBus) {
        this.firebase = firebase;
        this.eventBus = eventBus;
    }

    @Override
    public void subscribe() {
        firebase.subscribe(new FirebaseEventListenerCallback() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot) {
                Photo photo = dataSnapshot.getValue(Photo.class);
                photo.setId(dataSnapshot.getKey());

                String email = firebase.getAuthEmail();

                boolean publishedByMy = photo.getEmail()!=null?photo.getEmail().equals(email):false;
                photo.setPublishedByMe(publishedByMy);
                postEvent(PhotoMapEvent.READ_EVENT, photo);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Photo photo = dataSnapshot.getValue(Photo.class);
                photo.setId(dataSnapshot.getKey());

                postEvent(PhotoMapEvent.DELETE_EVENT, photo);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                postEvent(error.getMessage());
            }


        });
    }

    @Override
    public void unsubscribe() {

        firebase.unsubscribe();
    }

    private void postEvent(int type, Photo photo){
        postEvent(type, photo, null);
    }

    private void postEvent(String error){
        postEvent(0, null, error);
    }

    private void postEvent(int type, Photo photo, String error){
        PhotoMapEvent event = new PhotoMapEvent();
        event.setType(type);
        event.setError(error);
        event.setPhoto(photo);
        eventBus.post(event);
    }
}
