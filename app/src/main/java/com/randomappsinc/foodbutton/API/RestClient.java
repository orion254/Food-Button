package com.randomappsinc.foodbutton.API;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.randomappsinc.foodbutton.Models.Filter;
import com.randomappsinc.foodbutton.Models.Restaurant;
import com.randomappsinc.foodbutton.Persistence.PreferencesManager;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public interface RestaurantsListener {
        void onRestaurantsFetched(ArrayList<Restaurant> restaurants);
    }

    private static final RestaurantsListener DUMMY_RESTAURANTS_LISTENER = new RestaurantsListener() {
        @Override
        public void onRestaurantsFetched(ArrayList<Restaurant> restaurants) {}
    };

    private static RestClient mInstance;

    private Retrofit mRetrofit;
    private YelpService mYelpService;
    private Handler mHandler;

    // Places
    @NonNull
    private RestaurantsListener mRestaurantsListener = DUMMY_RESTAURANTS_LISTENER;
    private Call<RestaurantSearchResults> currentFindRestaurantsCall;

    public static RestClient getInstance() {
        if (mInstance == null) {
            mInstance = new RestClient();
        }
        return mInstance;
    }

    private RestClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mYelpService = mRetrofit.create(YelpService.class);

        HandlerThread backgroundThread = new HandlerThread("");
        backgroundThread.start();
        mHandler = new Handler(backgroundThread.getLooper());
    }

    public void findRestaurants(final String location) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFindRestaurantsCall != null) {
                    currentFindRestaurantsCall.cancel();
                }

                Filter filter = PreferencesManager.get().getFilter();
                String searchTerm = TextUtils.isEmpty(filter.getSearchTerm())
                        ? ApiConstants.DEFAULT_SEARCH_TERM
                        : filter.getSearchTerm();

                currentFindRestaurantsCall = mYelpService.findRestaurants(
                        searchTerm,
                        location,
                        ApiConstants.DEFAULT_NUM_RESTAURANTS,
                        true);
                currentFindRestaurantsCall.enqueue(new FindRestaurantsCallback());
            }
        });
    }

    public void registerRestaurantsListener(RestaurantsListener restaurantsListener) {
        mRestaurantsListener = restaurantsListener;
    }

    public void unregisterRestaurantsListener() {
        mRestaurantsListener = DUMMY_RESTAURANTS_LISTENER;
    }

    void processRestaurants(ArrayList<Restaurant> restaurants) {
        mRestaurantsListener.onRestaurantsFetched(restaurants);
    }

    public void cancelRestaurantsFetch() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFindRestaurantsCall != null) {
                    currentFindRestaurantsCall.cancel();
                }
            }
        });
    }
}
