package com.example.digitalrefrige.model.dataQuery;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.ItemLabelCrossRef;
import com.example.digitalrefrige.model.dataHolder.Label;

import java.util.List;

public class ItemWithLabels {
    @Embedded public Item item;
    @Relation(
            parentColumn = "itemId",
            entityColumn = "labelId",
            associateBy = @Junction(ItemLabelCrossRef.class)
    )
    public List<Label> labels;
}
