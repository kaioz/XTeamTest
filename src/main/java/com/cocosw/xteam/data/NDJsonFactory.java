package com.cocosw.xteam.data;

import android.text.TextUtils;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import retrofit.Converter;

/**
 * Coco studio
 * <p/>
 * Created by kai on 1/12/2015.
 */
public class NDJsonFactory extends Converter.Factory {

    private final Moshi moshi;

    public NDJsonFactory(Moshi moshi) {
        if (moshi == null) throw new NullPointerException("moshi == null");
        this.moshi = moshi;
    }

    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        JsonAdapter<?> adapter = moshi.adapter(type);
        return new NDJsonResponseConverter<>(adapter);
    }

    @Override public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        return null;
    }


    static final class NDJsonResponseConverter<T> implements Converter<ResponseBody, T> {

        private final JsonAdapter<T> adapter;

        public NDJsonResponseConverter(JsonAdapter<T> adapter) {
            this.adapter = adapter;
        }

        @Override public T convert(ResponseBody value) throws IOException {
            String jsonResponseBodyString = value.string().replaceAll("\n", ",");
            if (TextUtils.isEmpty(jsonResponseBodyString))
                return adapter.fromJson("[]");
            jsonResponseBodyString = jsonResponseBodyString.substring(0, jsonResponseBodyString.length() - 1);
            return adapter.fromJson("["+jsonResponseBodyString+"]");
        }
    }


}
