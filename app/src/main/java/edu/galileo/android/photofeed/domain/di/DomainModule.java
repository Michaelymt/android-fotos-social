package edu.galileo.android.photofeed.domain.di;

import android.content.Context;
import android.location.Geocoder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.galileo.android.photofeed.domain.Util;

/**
 * Created by joedayz.
 */
@Module
public class DomainModule {



    @Provides
    @Singleton
    Util providesUtil(Geocoder geocoder) {
        return new Util(geocoder);
    }

    @Provides
    @Singleton
    Geocoder providesGeocoder(Context context) {
        return new Geocoder(context);
    }
}
