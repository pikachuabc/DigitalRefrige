package com.example.digitalrefrige.model.dataQuery;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.ItemLabelCrossRef;
import com.example.digitalrefrige.model.dataHolder.Label;

import java.util.List;

public class LabelWithItems {
    @Embedded
    public Label label;
    @Relation(
            parentColumn = "labelId",
            entityColumn = "itemId",
            associateBy = @Junction(ItemLabelCrossRef.class)
    )
    public List<Item> items;
}
