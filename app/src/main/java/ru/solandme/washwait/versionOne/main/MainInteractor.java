package ru.solandme.washwait.versionOne.main;

class MainInteractor implements IMainInteractor {

    private OnUpdatedListener onUpdatedListener;

    public MainInteractor(OnUpdatedListener onUpdatedListener) {
        this.onUpdatedListener = onUpdatedListener;
    }


    @Override
    public void loadWeather() {

    }
}
