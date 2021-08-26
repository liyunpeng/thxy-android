package cn.tihuxueyuan.http;

import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import static cn.tihuxueyuan.utils.Constant.TAG;
import okhttp3.Response;


public abstract class JsonCallback<T> extends Callback<T> {
    private Class<T> clazz;
    private Gson gson;

    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
        gson = new Gson();
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        try {
            String jsonString = response.body().string();
            Log.d(TAG, "parseNetworkResponse: "  +  jsonString);

            return gson.fromJson(jsonString, clazz);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
