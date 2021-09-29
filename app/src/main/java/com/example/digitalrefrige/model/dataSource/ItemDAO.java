package com.example.digitalrefrige.model.dataSource;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.digitalrefrige.model.dataHolder.Item;

import java.util.List;

@Dao
public interface ItemDAO {
    @Query("select * from item_table order by createDate")
    LiveData<List<Item>> getAllItems();

    @Insert
    void insertItem(Item item);

    @Update
    void updateItem(Item item);

    @Delete
    void deleteItem(Item item);

    @Query("select * from item_table where itemId=:id")
    Item findItemById(int id);

}
