package com.jakewharton.u2020.data.api;

import com.cocosw.xteam.data.CacheInterceptor;
import com.cocosw.xteam.data.NDJsonFactory;
import com.cocosw.xteam.data.WarehouseService;
import com.squareup.moshi.Moshi;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

@Module(
    complete = false,
    library = true
)
public final class ApiModule {
  public static final HttpUrl PRODUCTION_API_URL = HttpUrl.parse("http://74.50.59.155:5000/api/search");

  @Provides @Singleton HttpUrl provideBaseUrl() {
    return PRODUCTION_API_URL;
  }

  @Provides @Singleton @Named("Api") OkHttpClient provideApiClient(OkHttpClient client) {
    return createApiClient(client);
  }

  @Provides @Singleton
  Retrofit provideRetrofit(HttpUrl baseUrl, @Named("Api") OkHttpClient client, Moshi moshi) {
    return new Retrofit.Builder() //
        .client(client) //
        .baseUrl(baseUrl) //
        //.addConverterFactory(MoshiConverterFactory.create(moshi)) //
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //
        .addConverterFactory(new NDJsonFactory(moshi))
        .build();
  }

  @Provides @Singleton
  WarehouseService provideService(Retrofit retrofit) {
    return retrofit.create(WarehouseService.class);
  }

  static OkHttpClient createApiClient(OkHttpClient client) {
    client = client.clone();
    client.interceptors().add(new CacheInterceptor());
    return client;
  }
}
