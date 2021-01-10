package com.example.mybrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BookmarkActivity2 extends AppCompatActivity {
    ImageButton backToMainBtn;
    Button clearBookmarkBtn;
    RecyclerView bookMarksRecyclerView;
    List<Star> bookMarks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        backToMainBtn = (ImageButton) findViewById(R.id.bookmark_to_main_btn);
        clearBookmarkBtn = (Button) findViewById(R.id.clear_collection_btn);

        loadBookmarks(); //Retrieve data from sharedPreference
        bookMarksRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        RecycleViewAdapter myAdapter = new RecycleViewAdapter(this, bookMarks);
        bookMarksRecyclerView.setAdapter(myAdapter);
        bookMarksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backToMainBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intentBackToMain = new Intent(BookmarkActivity2.this, MainActivity.class);
                startActivity(intentBackToMain);
            }
        });

        clearBookmarkBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences clearPrefers = getSharedPreferences("BOOKMARKS", MODE_PRIVATE);
                clearPrefers.edit().clear().apply();
                bookMarks.clear();
                myAdapter.clearInAdapter();
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadBookmarks(){
        SharedPreferences bookmarkPrefers = getSharedPreferences("BOOKMARKS", Context.MODE_PRIVATE);
        String json = bookmarkPrefers.getString("collections", null);

        if (json==null){
            Log.d("TestLoadBookmark", "Not Initialized");
            Toast.makeText(this, "No Bookmarked page.", Toast.LENGTH_SHORT).show();
        }
        else {
            Gson gson = new Gson();
            bookMarks = gson.fromJson(json, new TypeToken<List<Star>>(){}.getType());
            for (int i = 0; i<bookMarks.size(); i++){
                Log.d("TestLoadBookmarks", "Index "+ Integer.toString(i) + bookMarks.get(i).getTitle());
            }
        }
    }
}