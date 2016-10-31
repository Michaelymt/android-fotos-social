package edu.galileo.android.photofeed.photolist;



import com.google.firebase.database.DatabaseError;

import edu.galileo.android.photofeed.domain.FirebaseAPI;
import edu.galileo.android.photofeed.domain.FirebaseActionListenerCallback;
import edu.galileo.android.photofeed.entities.Photo;
import edu.galileo.android.photofeed.lib.base.EventBus;
import edu.galileo.android.photofeed.photolist.events.PhotoListEvent;

/**
 * Created by ykro.
 * Updated by joedayz.
 */
public class PhotoListRepositoryImpl implements PhotoListRepository {
    private EventBus eventBus;
    private FirebaseAPI firebase;


    public PhotoListRepositoryImpl( FirebaseAPI firebaseAPI, EventBus eventBus) {
        this.firebase = firebaseAPI;
        this.eventBus = eventBus;
    }

    @Override
    public void subscribe() {

        firebase.checkForData(new FirebaseActionListenerCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onException(Exception ex) {
                if (ex != null) {
                    postEvent(PhotoListEvent.READ_EVENT, ex.getMessage());
                } else {
                    postEvent(PhotoListEvent.READ_EVENT, "");
                }
            }

            @Override
            public void onError(DatabaseError error) {
                if (error != null) {
                    postEvent(PhotoListEvent.READ_EVENT, error.getMessage());
                } else {
                    postEvent(PhotoListEvent.READ_EVENT, "");
                }

            }
        });
    }

    @Override
    public void unsubscribe() {

        firebase.unsubscribe();
    }

    @Override
    public void remove(final Photo photo) {

        firebase.remove(photo, new FirebaseActionListenerCallback() {
            @Override
            public void onSuccess() {
                postEvent(PhotoListEvent.DELETE_EVENT, photo);
            }

            @Override
            public void onException(Exception ex) {

            }

            @Override
            public void onError(DatabaseError databaseError) {

            }


        });
    }

    private void postEvent(int type, Photo photo){
        postEvent(type, photo, null);
    }

    private void postEvent(int type, String error){
        postEvent(type, null, error);
    }

    private void postEvent(int type, Photo photo, String error){
        PhotoListEvent event = new PhotoListEvent();
        event.setType(type);
        event.setError(error);
        event.setPhoto(photo);
        eventBus.post(event);
    }
}
