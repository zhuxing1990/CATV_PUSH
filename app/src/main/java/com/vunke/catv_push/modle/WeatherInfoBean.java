package com.vunke.catv_push.modle;

import io.reactivex.observers.DisposableObserver;

public class WeatherInfoBean {
    private PushInfoBean pushInfoBean;
    private DisposableObserver<Long> showDataObserver;
    private boolean isShow;
    private DisposableObserver<Long> expirePbservable;
    public PushInfoBean getPushInfoBean() {
        return pushInfoBean;
    }

    public void setPushInfoBean(PushInfoBean pushInfoBean) {
        this.pushInfoBean = pushInfoBean;
    }

    public DisposableObserver<Long> getShowDataObserver() {
        return showDataObserver;
    }

    public void setShowDataObserver(DisposableObserver<Long> showDataObserver) {
        this.showDataObserver = showDataObserver;
    }

    public DisposableObserver<Long> getExpirePbservable() {
        return expirePbservable;
    }

    public void setExpirePbservable(DisposableObserver<Long> expirePbservable) {
        this.expirePbservable = expirePbservable;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
