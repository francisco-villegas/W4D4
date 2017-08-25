package com.example.francisco.w4d4week.view.main_activity;

import android.content.Context;
import android.location.Location;

import com.example.francisco.w4d4week.BasePresenter;
import com.example.francisco.w4d4week.BaseView;

/**
 * Created by FRANCISCO on 24/08/2017.
 */

public interface MainActivityContract {

    interface View extends BaseView {
        void SendResultRestCall(String address);

        void sendLocation(Location location);
    }

    interface Presenter extends BasePresenter<View> {
        void setContext(Context context);
        void MakeRestCall(Location location);

        void checkPermissions(int MY_PERMISSIONS_REQUEST_LOCATION);
    }
}
