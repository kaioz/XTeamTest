package com.jakewharton.u2020.data.api;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.cocosw.xteam.data.Emotion;
import com.cocosw.xteam.data.MockResponse;
import com.cocosw.xteam.data.NDJsonFactory;
import com.cocosw.xteam.data.WarehouseService;
import com.jakewharton.u2020.util.EnumPreferences;
import com.squareup.moshi.Types;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Converter;
import retrofit.Retrofit;
import retrofit.http.Query;
import rx.Observable;

@Singleton
public final class MockGithubService implements WarehouseService {
  private final SharedPreferences preferences;
  private final Map<Class<? extends Enum<?>>, Enum<?>> responses = new LinkedHashMap<>();
  private final Converter.Factory factory;
  private final Context context;

  @Inject MockGithubService(SharedPreferences preferences, Retrofit retrofit,Application context) {
    this.preferences = preferences;
    this.context = context;
    // Initialize mock responses.
    loadResponse(MockResponse.class, MockResponse.UNLIMITED);
    Converter.Factory convert = null;
    for (Converter.Factory factory : retrofit.converterFactories()) {
      if (factory instanceof NDJsonFactory)
        convert = factory;
    }
    if (convert==null)
      throw new IllegalArgumentException("No valid NDJsonFactory");

    factory = convert;
  }

  /**
   * Initializes the current response for {@code responseClass} from {@code SharedPreferences}, or
   * uses {@code defaultValue} if a response was not found.
   */
  private <T extends Enum<T>> void loadResponse(Class<T> responseClass, T defaultValue) {
    responses.put(responseClass, EnumPreferences.getEnumValue(preferences, responseClass, //
        responseClass.getCanonicalName(), defaultValue));
  }

  public <T extends Enum<T>> T getResponse(Class<T> responseClass) {
    return responseClass.cast(responses.get(responseClass));
  }

  public <T extends Enum<T>> void setResponse(Class<T> responseClass, T value) {
    responses.put(responseClass, value);
    EnumPreferences.saveEnumValue(preferences, responseClass.getCanonicalName(), value);
  }

  @Override
  public Observable<List<Emotion>> fetch(@Query("limit") int limit, @Query("skip") long skip, @Query("q") String searchFromTags, @Query("onlyInStock") int inStock) {
    MockResponse response = getResponse(MockResponse.class);
    switch (response) {
      case SINGLE:
      case UNLIMITED:
        return Observable.just(mock(response.json));
      default:
        return Observable.error(new IOException("Mock failure"));
    }
  }

  private List<Emotion> mock(String json) {
    List<Emotion> obj = new ArrayList<>();
    try {
      Type listMyData = Types.newParameterizedType(List.class, Emotion.class);

      return (List<Emotion>) factory.fromResponseBody(listMyData,null).convert(ResponseBody.create(MediaType.parse("plain/json"), loadAsset(context,json)));
    } catch (IOException e) {
      return obj;
    }
  }

  private static String loadAsset(Context context, String path) {
    try {
      InputStream input = context.getResources().getAssets().open(path);
      int size = input.available();
      byte[] buffer = new byte[size];
      input.read(buffer);
      input.close();
      return new String(buffer);
    } catch (IOException e) {
      return "";
    }
  }
}
