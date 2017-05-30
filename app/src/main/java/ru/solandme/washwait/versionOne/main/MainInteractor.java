package ru.solandme.washwait.versionOne.main;

import android.content.Context;

class MainInteractor implements IMainInteractor {

    private OnUpdatedListener onUpdatedListener;
    private IMainRepository mainRepository;

    public MainInteractor(Context context, OnUpdatedListener onUpdatedListener) {
        this.onUpdatedListener = onUpdatedListener;
        this.mainRepository = new MainRepository(context);
    }


    @Override
    public void loadWeather() {

    }
}
