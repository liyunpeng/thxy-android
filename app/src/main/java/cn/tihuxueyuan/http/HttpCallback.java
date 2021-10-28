package cn.tihuxueyuan.http;


import java.io.IOException;

public abstract class HttpCallback<T> {
    public abstract void onSuccess(T t);

    public abstract void onFail(Exception e);

    public void onFinish() {
    }
}
