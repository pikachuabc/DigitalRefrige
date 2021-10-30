package com.example.digitalrefrige.model.dataSource;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.digitalrefrige.model.dataHolder.Item;

import java.util.Date;
import java.util.List;

@Dao
public interface ItemDAO {
    @Query("select * from item_table order by expireDate")
    LiveData<List<Item>> getAllItems();

    @Insert
    long insertItem(Item item);

    /**
     * Updating each field but not imageURL
     */
    @Query("UPDATE item_table SET name = :name, description = :description, expireDate =:expireDate, quantity = :quantity WHERE itemId = :itemId")
    void updateNoURL(String name, String description, Date expireDate, Integer quantity, long itemId);

    @Update
    void updateItem(Item item);

    @Delete
    void deleteItem(Item item);

    @Delete
    @Transaction
    default void deleteSelectedItems(List<Item> items){
        for (Item item : items) {
            deleteItem(item);
        }
    }

    @Query("select * from item_table where itemId=:id")
    Item findItemById(long id);

    @Query("select * from item_table where julianday(date(expireDate/1000, 'unixepoch'))-julianday('now') between 0 and :interval")
    List<Item> getExpiringItems(int interval);

    @Query("select * from item_table where julianday(date(expireDate/1000, 'unixepoch'))-julianday('now')<0")
    List<Item> getExpiredItems();

}
