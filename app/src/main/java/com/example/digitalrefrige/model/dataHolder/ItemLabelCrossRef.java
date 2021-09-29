package com.example.digitalrefrige.model.dataHolder;

import androidx.room.Entity;

@Entity(primaryKeys = {"itemId", "labelId"})
public class ItemLabelCrossRef {
    private int itemId;
    private int labelId;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }
}
