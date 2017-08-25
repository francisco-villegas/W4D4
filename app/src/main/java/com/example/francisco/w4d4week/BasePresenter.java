package com.example.francisco.w4d4week;

/**
 * Created by FRANCISCO on 24/08/2017.
 */

public interface BasePresenter <V extends BaseView>{

    void attachView(V view);
    void detachView();
}
