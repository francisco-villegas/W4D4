package com.example.francisco.w4d4week.injection.maps_activity;

import com.example.francisco.w4d4week.view.maps_activity.MapsActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by FRANCISCO on 24/08/2017.
 */
@Module
public class MapsActivityModel {

    @Provides
    MapsActivityPresenter providesMapsActivityPresenter(){
        return new MapsActivityPresenter();
    }
}
