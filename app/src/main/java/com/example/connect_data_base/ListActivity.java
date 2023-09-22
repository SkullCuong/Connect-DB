package com.example.connect_data_base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import java.util.List;

public class ListActivity extends AppCompatActivity {
    private List<Person> people;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        people = PersonDB.getInstance(this).personDao().getListPerson();
        ListAdapter listAdapter = new ListAdapter(people,this);
        RecyclerView recyclerView = findViewById(R.id.listPerson);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                people = PersonDB.getInstance(ListActivity.this).personDao().getListPersonByName(newQuery);
                listAdapter.setFilteredList(people);
                return false;
            }
        });


    }

}