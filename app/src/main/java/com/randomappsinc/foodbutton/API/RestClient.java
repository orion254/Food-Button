package com.randomappsinc.foodbutton.API;

import android.os.AsyncTask;

import com.randomappsinc.foodbutton.API.OAuth.DecodeInterceptor;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexanderchiou on 3/26/16.
 */
public class RestClient {
    private static final String APP_ID = "Y6mN70GyUV5fdqOvceOrVQ";
    private static final String APP_SECRET = "I9mmDM1JqyqijjY1JR9i0XYMPEeiUZQCQIc0vSY0iaoah2rm90mfisHYV1oPVtwl";

    private static RestClient restClient;

    public static RestClient get() {
        if (restClient == null) {
            restClient = new RestClient();
        }
        return restClient;
    }

    private YelpFusionApi yelpFusionApi;

    private RestClient() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    yelpFusionApi = new YelpFusionApiFactory().createAPI(APP_ID, APP_SECRET);
                } catch (IOException ignored) {}
            }
        });
    }

    public void doSearch() {
        if (yelpFusionApi != null) {
            Map<String, String> params = new HashMap<>();

            params.put("location", "San Francisco, CA");

            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    if (response.code() == 200) {

                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {

                }
            });
        }
    }
}
