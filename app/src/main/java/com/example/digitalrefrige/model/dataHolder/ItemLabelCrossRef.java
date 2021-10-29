package com.example.digitalrefrige.model.dataHolder;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"itemId", "labelId"},
        foreignKeys = {
                @ForeignKey(entity = Item.class,
                        parentColumns = {"itemId"},
                        childColumns = {"itemId"},
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Label.class,
                        parentColumns = {"labelId"},
                        childColumns = {"labelId"},
                        onDelete = ForeignKey.CASCADE)
        })
public class ItemLabelCrossRef {

    private long itemId;
    private long labelId;

    public ItemLabelCrossRef(Long itemId, Long labelId) {
        this.itemId = itemId;
        this.labelId = labelId;
    }

    public ItemLabelCrossRef() {};


    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getLabelId() {
        return labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }
}
