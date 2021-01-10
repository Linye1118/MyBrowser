package com.example.mybrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ArrayList<String> histories= new ArrayList<>();
    ArrayAdapter<String> itemAdapter;
    ListView listView;
    ImageButton backToMainBtn;
    Button clearHistoryBtn;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_page);

        backToMainBtn = (ImageButton) findViewById(R.id.back_to_main_btn);
        clearHistoryBtn = (Button) findViewById(R.id.clear_history_btn);

        //retrieve data from intent
        Intent intent = getIntent();
        Bundle stringArrayList = getIntent().getExtras();
        if (stringArrayList==null)
        {
            Log.d("TestIntentOnHistory", "no history...");
        }
        else
            {
                histories = stringArrayList.getStringArrayList("History_URL");
                Log.d("Logs we have ", "history "+ histories);
                itemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, histories);
                listView = (ListView)findViewById(R.id.history_listView);
                listView.setAdapter(itemAdapter);
                listView.setOnItemClickListener(this);
            }

        backToMainBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intentBackToMain = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intentBackToMain);
            }
        });

        clearHistoryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences clearPrefers = getSharedPreferences("History_Log", MODE_PRIVATE);
                clearPrefers.edit().clear().apply();
                histories.clear();
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    //go to the item url in MainActivity when clicked
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = parent.getItemAtPosition(position).toString();
        Toast.makeText(getApplicationContext(), "Clicked: " + url, Toast.LENGTH_SHORT).show();
        Intent openUrlIntent = new Intent(HistoryActivity.this, MainActivity.class);
        Intent intent = openUrlIntent.putExtra("URL_His",url); //pass url to Main through intent
        startActivity(openUrlIntent);
    }

}
