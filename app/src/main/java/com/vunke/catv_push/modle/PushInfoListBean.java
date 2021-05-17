package com.vunke.catv_push.modle;

import io.reactivex.observers.DisposableObserver;

public class PushInfoListBean {
    private DisposableObserver<Long> timeObservable;
    private PushInfoBean pushInfoBean;

    public DisposableObserver<Long> getTimeObservable() {
        return timeObservable;
    }

    public void setTimeObservable(DisposableObserver<Long> timeObservable) {
        this.timeObservable = timeObservable;
    }

    public PushInfoBean getPushInfoBean() {
        return pushInfoBean;
    }

    public void setPushInfoBean(PushInfoBean pushInfoBean) {
        this.pushInfoBean = pushInfoBean;
    }

}
