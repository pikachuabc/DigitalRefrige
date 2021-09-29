package com.example.digitalrefrige.utils.di;

import android.content.Context;

import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.LabelRepository;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataSource.ItemDAO;
import com.example.digitalrefrige.model.dataSource.LabelDAO;
import com.example.digitalrefrige.model.dataSource.LocalDataBase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class LocalDatabaseModule {

    @Provides
    @Singleton
    LocalDataBase provideLocalDatabase(@ApplicationContext Context context) {
        return LocalDataBase.getInstance(context);
    }

    @Provides
    @Singleton
    ItemDAO provideItemDao(LocalDataBase dataBase) {
        return dataBase.itemDAO();
    }

    @Provides
    @Singleton
    LabelDAO provideLabelDao(LocalDataBase dataBase) {
        return dataBase.labelDAO();
    }



    @Provides
    @Singleton
    ItemRepository provideItemRepo(ItemDAO itemDAO) {
        return new ItemRepository(itemDAO);
    }

    @Provides
    @Singleton
    LabelRepository provideLabelRepo(LabelDAO labelDAO){
        return  new LabelRepository(labelDAO);
    }

}
