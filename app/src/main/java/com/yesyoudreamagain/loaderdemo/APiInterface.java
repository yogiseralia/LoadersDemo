package com.yesyoudreamagain.loaderdemo;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Yogesh Seralia on 6/17/2019.
 */
public interface APiInterface {

    @GET("todos/{todoNo}")
    Call<ResponseData> todo(@Path("todoNo") int todoNo);


    class Builder {

        private static APiInterface aPiInterface;

        static APiInterface getInstance() {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl("https://jsonplaceholder.typicode.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            if (aPiInterface == null)
                aPiInterface = retrofit.create(APiInterface.class);

            return aPiInterface;
        }
    }
}
