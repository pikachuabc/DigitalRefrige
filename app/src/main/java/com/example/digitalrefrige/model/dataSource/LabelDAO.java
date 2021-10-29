package com.example.digitalrefrige.model.dataSource;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.digitalrefrige.model.dataHolder.Label;

import java.util.List;

@Dao
public interface LabelDAO {
    @Query("select * from label_table")
    LiveData<List<Label>> getAllLabels();

    @Insert
    long insertLabel(Label label);

    @Update
    void updateLabel(Label label);

    @Delete
    void deleteLabel(Label label);

    @Query("select * from label_table where labelId=:id")
    Label findLabelById(long id);

    @Query("select * from label_table where title=:name")
    Label findLabelByTitle(String name);
}
