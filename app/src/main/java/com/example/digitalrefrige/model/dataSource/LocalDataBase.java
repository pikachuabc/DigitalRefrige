package com.example.digitalrefrige.model.dataSource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.digitalrefrige.model.dataHolder.Item;

import org.jetbrains.annotations.NotNull;

/**
 * Establishing local database with some dummy data
 */
@Database(entities = Item.class, version = 1)
public abstract class LocalDataBase extends RoomDatabase {

    private static LocalDataBase instance;

    public abstract ItemDAO itemDAO();

    public static LocalDataBase getInstance(Context context) {
        if (instance != null) {
            return instance;
        } else {
            synchronized (Context.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), LocalDataBase.class, "note_database")
                            .fallbackToDestructiveMigration()
                            // pre-define data in our database
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull @NotNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    new Thread(() -> {
                                        ItemDAO itemDAO = instance.itemDAO();
                                        for (int i = 0; i < 15; i++) {
                                            itemDAO.insertItem(new Item("item" + i, "description" + i));
                                        }
                                    }).start();
                                }
                            })
                            .build();
                }
                return instance;
            }
        }
    }
}
