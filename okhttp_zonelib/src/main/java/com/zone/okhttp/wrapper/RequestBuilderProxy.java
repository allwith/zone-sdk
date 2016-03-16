package com.zone.okhttp.wrapper;
import com.zone.okhttp.OkHttpUtils;
import com.zone.okhttp.entity.RequestParams;
import java.io.IOException;
import java.net.URL;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * Created by Zone on 2016/2/10.
 */
public class RequestBuilderProxy extends Request.Builder {
    private RequestParams requestParams;
    private zone.Callback.CommonCallback mOkHttpListener;

    public RequestBuilderProxy() {
        super();
    }

    @Override
    public RequestBuilderProxy url(HttpUrl url) {
        super.url(url);
        return this;
    }

    @Override
    public RequestBuilderProxy url(String url) {
       super.url(url);
      return this;
    }

    @Override
    public RequestBuilderProxy url(URL url) {
        super.url(url);
        return this;
    }

    @Override
    public RequestBuilderProxy header(String name, String value) {
        super.header(name, value);
        return this;
    }

    @Override
    public RequestBuilderProxy addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public RequestBuilderProxy removeHeader(String name) {
        super.removeHeader(name);
        return this;
    }

    @Override
    public RequestBuilderProxy headers(Headers headers) {
        super.headers(headers);
        return this;
    }

    @Override
    public RequestBuilderProxy cacheControl(CacheControl cacheControl) {
        super.cacheControl(cacheControl);
        return this;
    }

    @Override
    public RequestBuilderProxy get() {
        super.get();
        return this;
    }

    @Override
    public RequestBuilderProxy head() {
        super.head();
        return this;
    }

    @Override
    public RequestBuilderProxy post(RequestBody body) {
        super.post(body);
        return this;
    }

    @Override
    public RequestBuilderProxy delete(RequestBody body) {
        super.delete(body);
        return this;
    }

    @Override
    public RequestBuilderProxy delete() {
        super.delete();
        return this;
    }

    @Override
    public RequestBuilderProxy put(RequestBody body) {
        super.put(body);
        return this;
    }

    @Override
    public RequestBuilderProxy patch(RequestBody body) {
        super.patch(body);
        return this;
    }

    @Override
    public RequestBuilderProxy method(String method, RequestBody body) {
        super.method(method, body);
        return this;
    }

    @Override
    public RequestBuilderProxy tag(Object tag) {
         super.tag(tag);
        return this;
    }

    //TODO 除了build全部返回 this
    @Override
    public Request build() {
        return super.build();
    }

    //这个不怎么用。。。   不是异步
    public Response execute() {
        Call call = OkHttpUtils.getClient().newCall(this.build());
        Response temp = null;
        try {
            temp = call.execute();
        } catch (IOException e) {
//            e.printStackTrace();
            System.err.println("cause:" + e.getCause() + "\t message:" + e.getMessage());
        }
        return temp;
    };

    public Call executeAsy() {
        Call call = OkHttpUtils.getClient().newCall(this.build());
        if(mOkHttpListener!=null)
            onStartMainCall(mOkHttpListener);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (mOkHttpListener != null){
                    onFailureMainCall(mOkHttpListener, call, e);
                    onFinishedMainCall(mOkHttpListener);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (mOkHttpListener != null){
                    onResponseMainCall(mOkHttpListener, call, response, response.body().string());
                    onFinishedMainCall(mOkHttpListener);
                }
            }
        });
        return call;
    };


    public RequestParams getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(RequestParams requestParams) {
        this.requestParams = requestParams;
        this.requestParams.setmRequestBuilder(this);
    }



    public zone.Callback.CommonCallback getmOkHttpListener() {
        return mOkHttpListener;
    }

    public void setmOkHttpListener(zone.Callback.CommonCallback mOkHttpListener) {
        this.mOkHttpListener = mOkHttpListener;
    }


    private void onStartMainCall(zone.Callback.CommonCallback listener){
        OkHttpUtils.getmHandler().post(new Runnable() {
            @Override
            public void run() {
                 mOkHttpListener.onStart();
            }
        });
    }
    private void onFailureMainCall(zone.Callback.CommonCallback listener, final Call call, final IOException e){
        OkHttpUtils.getmHandler().post(new Runnable() {
            @Override
            public void run() {
                mOkHttpListener.onError(call, e);
            }
        });
    }
    private void onFinishedMainCall(zone.Callback.CommonCallback listener){
        OkHttpUtils.getmHandler().post(new Runnable() {
            @Override
            public void run() {
                mOkHttpListener.onFinished();
            }
        });
    }
    private void onResponseMainCall(zone.Callback.CommonCallback listener, final Call call, final  Response response, final String result){
        OkHttpUtils.getmHandler().post(new Runnable() {
            @Override
            public void run() {
                mOkHttpListener.onSuccess(result,call, response);
            }
        });
    }
}
