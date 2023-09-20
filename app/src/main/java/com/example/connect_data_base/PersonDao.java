package com.example.connect_data_base;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PersonDao {

    @Insert
    long insertPerson(Person person);

    @Query("SELECT * FROM person ORDER BY id DESC ")
    List<Person> getListPerson();
}
