package com.example.digitalrefrige.model.dataSource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.digitalrefrige.model.dataHolder.Item;

import org.jetbrains.annotations.NotNull;

@Database(entities = Item.class, version = 1)
public abstract class LocalDataBase extends RoomDatabase{

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
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull @NotNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    new Thread(() -> {
                                        ItemDAO itemDAO = instance.itemDAO();
                                        itemDAO.insertItem(new Item("item1", "description1"));
                                        itemDAO.insertItem(new Item("item2", "description2"));
                                        itemDAO.insertItem(new Item("item3", "description3"));
                                        itemDAO.insertItem(new Item("item1", "description1"));
                                        itemDAO.insertItem(new Item("item2", "description2"));
                                        itemDAO.insertItem(new Item("item3", "description3"));
                                        itemDAO.insertItem(new Item("item1", "description1"));
                                        itemDAO.insertItem(new Item("item2", "description2"));
                                        itemDAO.insertItem(new Item("item3", "description3"));
                                        itemDAO.insertItem(new Item("item1", "description1"));
                                        itemDAO.insertItem(new Item("item2", "description2"));
                                        itemDAO.insertItem(new Item("item3", "description3"));
                                        itemDAO.insertItem(new Item("item1", "description1"));
                                        itemDAO.insertItem(new Item("item2", "description2"));
                                        itemDAO.insertItem(new Item("item3", "description3"));
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
