package ru.solandme.washwait.mvp.main.presentation.presenter;

import ru.solandme.washwait.mvp.main.presentation.view.IMainView;

public interface IMainPresenter {

    void attachView(IMainView mainView);
    void detachView();
    void onRefresh();
}
