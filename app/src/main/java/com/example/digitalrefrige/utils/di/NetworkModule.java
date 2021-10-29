package com.example.digitalrefrige.utils.di;

import com.example.digitalrefrige.services.DigitalFridgeService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    DigitalFridgeService getDigitalFridgeService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pukaishen.asuscomm.com:8080/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        return retrofit.create(DigitalFridgeService.class);
    }
}
