package edu.galileo.android.photofeed.main;

import android.location.Location;

import java.io.File;

import edu.galileo.android.photofeed.domain.FirebaseHelper;
import edu.galileo.android.photofeed.entities.Photo;
import edu.galileo.android.photofeed.lib.base.EventBus;
import edu.galileo.android.photofeed.lib.base.ImageStorage;
import edu.galileo.android.photofeed.lib.base.ImageStorageFinishedListener;
import edu.galileo.android.photofeed.main.events.MainEvent;

/**
 * Created by ykro.
 */
public class MainRepositoryImpl implements MainRepository {
    private EventBus eventBus;
    FirebaseHelper helper;
    private ImageStorage imageStorage;

    public MainRepositoryImpl(EventBus eventBus,  ImageStorage imageStorage) {
        this.eventBus = eventBus;
        helper = FirebaseHelper.getInstance();
        this.imageStorage = imageStorage;
    }

    @Override
    public void logout() {
        helper.signOff();
    }

    @Override
    public void uploadPhoto(Location location, String path) {
        final String newPhotoId = helper.getDataReference().push().getKey();
        final Photo photo = new Photo();
        photo.setId(newPhotoId);
        photo.setEmail(helper.getAuthUserEmail());

        if (location != null) {
            photo.setLatitutde(location.getLatitude());
            photo.setLongitude(location.getLongitude());
        }

        post(MainEvent.UPLOAD_INIT);
        imageStorage.upload(new File(path), photo.getId(), new ImageStorageFinishedListener(){

            @Override
            public void onSuccess() {
                String url = imageStorage.getImageUrl(photo.getId());
                photo.setUrl(url);

                helper.update(photo);

                post(MainEvent.UPLOAD_COMPLETE);
            }

            @Override
            public void onError(String error) {
                post(MainEvent.UPLOAD_ERROR, error);
            }
        });
    }

    private void post(int type){
        post(type, null);
    }

    private void post(int type, String error){
        MainEvent event = new MainEvent();
        event.setType(type);
        event.setError(error);
        eventBus.post(event);
    }
}
