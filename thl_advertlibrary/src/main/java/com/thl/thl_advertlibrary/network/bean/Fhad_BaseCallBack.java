package com.thl.thl_advertlibrary.network.bean;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author ${dell}
 * @time 2020/1/10 17
 */
public abstract class Fhad_BaseCallBack<T> extends Callback<T> {
    public Type mType;

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public Fhad_BaseCallBack() {
        mType = getSuperclassTypeParameter(getClass());
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        Log.d("Fhad_BaseCallBack", "parseNetworkResponse:" + response.request().url() + "---------" + result);
        return new Gson().fromJson(result, mType);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        Log.d("Fhad_BaseCallBack", "onError:" + call.request().url());
        onFailed(e);
    }

    @Override
    public void onResponse(T response, int id) {
        if (response instanceof Fhad_BaseModel) {
            Fhad_BaseModel res = (Fhad_BaseModel) response;
            if (res.isSuccess()) {
                success(response);
            } else {
                onFailed(new Exception(res.getInfo()));
            }
        } else {
            onFailed(new Exception("类型错误"));
        }
    }

    public void onFailed(Exception e) {
        Log.d("Fhad_BaseCallBack", "onFailed:" + e.getMessage());

    }

    public abstract void success(T result);
}
