package com.cocosw.xteam.data;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Coco studio
 * <p/>
 * Created by kai on 1/12/2015.
 */

public class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (request.method().equals("GET")) {
                request.newBuilder()
                        .header("Cache-Control", "only-if-cached")
                        .build();
            }

            Response response = chain.proceed(request);

            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=86400") // 1 day
                    .build();
        }
}
