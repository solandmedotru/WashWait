package ru.solandme.washwait.versionOne.main;

public interface IMainPresenter {

    void attachView(IMainView mainView);
    void detachView();
    void load();
}
