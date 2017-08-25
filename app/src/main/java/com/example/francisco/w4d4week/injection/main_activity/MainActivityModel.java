package com.example.francisco.w4d4week.injection.main_activity;

import com.example.francisco.w4d4week.view.main_activity.MainActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by FRANCISCO on 24/08/2017.
 */
@Module
public class MainActivityModel {

    @Provides
    MainActivityPresenter providesMainActivityPresenter(){
        return new MainActivityPresenter();
    }
}
