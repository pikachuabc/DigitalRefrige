package com.example.digitalrefrige.model.dataSource;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.ItemLabelCrossRef;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.ItemWithLabels;
import com.example.digitalrefrige.model.dataQuery.LabelWithItems;

import java.util.List;

@Dao
public interface ItemLabelCrossRefDAO {

    @Transaction
    @Query("SELECT * FROM label_table")
    LiveData<List<LabelWithItems>> getLabelOfItems();

    @Transaction
    @Query("SELECT * FROM item_table")
    LiveData<List<ItemWithLabels>> getItemOfLabels();

    @Transaction
    @Query("SELECT * FROM ItemLabelCrossRef")
    LiveData<List<ItemLabelCrossRef>> getAllCrossRefs();

    @Query("SELECT * FROM label_table WHERE label_table.labelId IN (SELECT itemlabelcrossref.labelId FROM itemlabelcrossref WHERE itemlabelcrossref.itemId=:itemId)")
    List<Label> getLabelsByItem(long itemId);

    @Transaction
    default void updateItemLabels(ItemWithLabels itemWithLabels) {
        long itemId = itemWithLabels.item.getItemId();
        deleteItemLabelCrossRef(itemId);
        for (Label label : itemWithLabels.labels) {
            insertItemLabelCrossRef(new ItemLabelCrossRef(itemId, label.getLabelId()));
        }
    }


    @Insert
    long insertItemLabelCrossRef(ItemLabelCrossRef itemLabelCrossRef);


    @Query("DELETE from itemlabelcrossref where itemId=:itemId")
    void deleteItemLabelCrossRef(long itemId);
}
