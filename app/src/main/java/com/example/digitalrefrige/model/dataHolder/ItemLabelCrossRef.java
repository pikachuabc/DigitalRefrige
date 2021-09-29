package com.example.digitalrefrige.model.dataHolder;

import androidx.room.Entity;

@Entity(primaryKeys = {"itemId", "labelId"})
public class ItemLabelCrossRef {
    private int itemId;
    private int labelId;

}
