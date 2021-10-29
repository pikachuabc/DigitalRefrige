package com.example.digitalrefrige.model.dataSource;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.digitalrefrige.R;
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

                                        Uri appleImg = Uri.parse("android.resource://com.example.digitalrefrige/" + R.drawable.apple);
                                        Uri bananaImg = Uri.parse("android.resource://com.example.digitalrefrige/" + R.drawable.banana);
                                        Uri yogurtImg = Uri.parse("android.resource://com.example.digitalrefrige/" + R.drawable.yogurt);


                                        long appleID = itemDAO.insertItem(new Item("apple", "this is an apple", calendar.getTime(), appleImg.toString()));
                                        long bananaID = itemDAO.insertItem(new Item("banana", "this is a banana", calendar.getTime(), bananaImg.toString()));
                                        long yogurtID = itemDAO.insertItem(new Item("yogurt", "a cup of yogurt", calendar.getTime(), yogurtImg.toString()));

                                        long fruitID = labelDAO.insertLabel(new Label("fruit"));
                                        long drinkID = labelDAO.insertLabel(new Label("drink"));

                                        itemLabelCrossRefDAO.insertItemLabelCrossRef(new ItemLabelCrossRef(appleID, fruitID));
                                        itemLabelCrossRefDAO.insertItemLabelCrossRef(new ItemLabelCrossRef(bananaID, fruitID));
                                        itemLabelCrossRefDAO.insertItemLabelCrossRef(new ItemLabelCrossRef(yogurtID, drinkID));

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
