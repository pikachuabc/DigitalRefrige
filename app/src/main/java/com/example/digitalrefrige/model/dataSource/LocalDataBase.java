package com.example.digitalrefrige.model.dataSource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.ItemLabelCrossRef;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.utils.Converters;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Establishing local database with some dummy data
 */
@Database(entities = {Item.class, Label.class, ItemLabelCrossRef.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class LocalDataBase extends RoomDatabase {

    private static LocalDataBase instance;

    public abstract ItemDAO itemDAO();

    public abstract LabelDAO labelDAO();

    public abstract ItemLabelCrossRefDAO itemLabelCrossRefDAO();


    public static LocalDataBase getInstance(Context context) {
        if (instance != null) {
            return instance;
        } else {
            synchronized (Context.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), LocalDataBase.class, "local_database")
                            .fallbackToDestructiveMigration()
                            // pre-define data in our database
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull @NotNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    new Thread(() -> {
                                        ItemDAO itemDAO = instance.itemDAO();
                                        LabelDAO labelDAO = instance.labelDAO();
                                        ItemLabelCrossRefDAO itemLabelCrossRefDAO = instance.itemLabelCrossRefDAO();
                                        Calendar calendar = Calendar.getInstance();
                                        for (int i = 1; i <= 5; i++) {
                                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                                            itemDAO.insertItem(new Item("item" + i, "description" + i, calendar.getTime(),""));
                                        }
                                        for (int i = 1; i <= 5; i++) {
                                            labelDAO.insertLabel(new Label("lABEL_" + i));
                                        }
                                        long tempLabelId = 1;
                                        for (int i = 1; i <= 15; i++) {
                                            if (tempLabelId == 5) tempLabelId = 1;
                                            itemLabelCrossRefDAO.insertItemLabelCrossRef(new ItemLabelCrossRef((long) i, tempLabelId));
                                            itemLabelCrossRefDAO.insertItemLabelCrossRef(new ItemLabelCrossRef((long) i, tempLabelId + 1));
                                            tempLabelId++;
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
