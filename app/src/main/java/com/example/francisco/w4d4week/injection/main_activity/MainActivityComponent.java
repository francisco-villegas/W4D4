package com.example.francisco.w4d4week.injection.main_activity;

import com.example.francisco.w4d4week.view.main_activity.MainActivity;

import dagger.Component;

/**
 * Created by FRANCISCO on 24/08/2017.
 */

@Component(modules = MainActivityModel.class)
public interface MainActivityComponent {

    void inject(MainActivity mainActivity);
}
