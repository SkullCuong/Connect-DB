package com.example.connect_data_base;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {Person.class}, version = 1)

public abstract class PersonDB extends RoomDatabase {

    private static final String Database_name = "person.db";
    private static PersonDB instance;

    public static synchronized PersonDB getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),PersonDB.class,Database_name)
                    .allowMainThreadQueries()
                    .build();
        };
        return instance;
    }
    public abstract  PersonDao personDao();
}
