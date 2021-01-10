package com.example.mybrowser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ImageButton homeButton, goButton, back, forward, bookmark, history, setting;
    EditText urlText;
    WebView myWebView;
    WebSettings webSettings;
    ArrayList<String> histories= new ArrayList<>();
    List<Star> bookmarks = new ArrayList<>();

    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeButton = (ImageButton) findViewById(R.id.home_btn);
        urlText = (EditText) findViewById(R.id.url_txt);
        goButton = (ImageButton)findViewById(R.id.search_btn);
        back = (ImageButton) findViewById(R.id.back_btn);
        forward = (ImageButton) findViewById(R.id.forward_btn);
        bookmark = (ImageButton)findViewById(R.id.bookmark_btn);
        history = (ImageButton) findViewById(R.id.viewHistory_btn);
        setting = (ImageButton)findViewById(R.id.userSetting_btn);

        myWebView = (WebView) findViewById(R.id.web_view);
        webSettings=myWebView.getSettings();

        if(savedInstanceState!=null){
            myWebView.restoreState(savedInstanceState);
            String savedURL = savedInstanceState.getString("currentURL");
            Log.d("TestRetrieveInstance", "currentURL");
            myWebView.loadUrl(savedURL);
        }else{
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setSupportMultipleWindows(true);
            myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            Log.d("TestRetrieveInstance", "saved-null ");
        }
        myWebView.setWebViewClient(new IgnoreSSLErrorWebViewClient());
        initialData();

        //search button function
        goButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    if(!connectionStatus(MainActivity.this)){
                        Toast.makeText(MainActivity.this,
                                R.string.check_connection, Toast.LENGTH_SHORT).show();
                    }else{
                        InputMethodManager inputMethodManager =
                                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(urlText.getWindowToken(), 0);
                        myWebView.loadUrl("https://" + urlText.getText().toString());
                        urlText.setText("");
                    }
                }
                catch (Exception e){
                    // catch error
                    Log.d("TestGoButton", "something goes wrong");
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myWebView.canGoBack()) {
                    myWebView.goBack();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Cannot go back", Toast.LENGTH_SHORT).show();
                }
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myWebView.canGoForward())
                    myWebView.goForward();
                else
                    Toast.makeText(getApplicationContext(), "Cannot go forward", Toast.LENGTH_SHORT).show();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if sharedPreference homePage is null
                SharedPreferences homePagePrefers = getSharedPreferences("HomePage", MODE_PRIVATE);
                String hp = homePagePrefers.getString("homePageKey", null);
                if (hp==null){
                    myWebView.loadUrl("https://google.com");
                }else{
                    Log.d("TestHomePage", "A HomePage has been set by user");
                    myWebView.loadUrl(hp);
                }
            }
        });

        bookmark.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                // This activity implements OnMenuItemClickListener
                popup.setOnMenuItemClickListener(MainActivity.this);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.bookmarkmenu, popup.getMenu());
                showMenuIcons(popup);
                popup.show();
            }
        }));
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                // This activity implements OnMenuItemClickListener
                popup.setOnMenuItemClickListener(MainActivity.this);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.settingmenu, popup.getMenu());
                showMenuIcons(popup);
                popup.show();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            //show history when clicked
            public void onClick(View v) {
                WebBackForwardList BackForwardList = myWebView.copyBackForwardList();
                int currentSize = BackForwardList.getSize();
                ArrayList<String> HistoryLog = new ArrayList<>();
                for(int i = 0; i < currentSize; ++i)
                {
                    String url = BackForwardList.getItemAtIndex(i).getUrl();
                    String title = BackForwardList.getItemAtIndex(i).getTitle();
                    Log.d("TestGo", "GetPageTitle: "+ title);
                    Log.d("TestNewLog", "The URL at index: " + Integer.toString(i) + " is " + url );
                    HistoryLog.add(url);
                }
                if (HistoryLog.size()!=0){
                    historiesManager(HistoryLog); //save data in "History_Log" sharedPreference
                    //save currentURL to shared preference
                    backupCurrentPage();
                    //send history data using intent to HistoryActivity
                    Intent intentToHistory = new Intent(MainActivity.this, HistoryActivity.class);
                    Intent intent = intentToHistory.putStringArrayListExtra("History_URL", histories);
                    startActivity(intentToHistory);
                }
                else {
                    Toast.makeText(getApplicationContext(), "No History Data ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void historiesManager(ArrayList<String> log) {
        SharedPreferences hisPrefers = getSharedPreferences("History_Log", MODE_PRIVATE);
        SharedPreferences.Editor myEditor;
        String list = hisPrefers.getString("historyList", null);

        if (list == null){
            Log.d("TestHistoryManager", "Not Initialized");
            histories = log;
        }
        else {
            Log.d("TestHistoryManager", "Initialized, add new historyLog");
            Gson gsonExist = new Gson();
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            ArrayList<String> oldLog = gsonExist.fromJson(list, type);
            oldLog.addAll(log);
            int count = oldLog.size();
            if (count>10){
                for (int i=count-1; i>count-10; i--){
                    histories.add(oldLog.get(i));
                }
            }
            else {
                histories=oldLog;
            }
        }
        myEditor = hisPrefers.edit();
        Gson gsonNew = new Gson();
        String jsonC = gsonNew.toJson(histories);
        myEditor.putString("historyList", jsonC);
        myEditor.apply();
    }

    //following codes force icon to show on popup menu
    private void showMenuIcons(PopupMenu popup){
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            Log.d("TestMenu", "menu icon problem...");
            e.printStackTrace();
        }
    }

    // preserve user's UI state before activity destroyed
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        myWebView.saveState(outState);
        Log.d("TestOnSaveInstanceState", "currentURL"+myWebView.getUrl());
        outState.putString("currentURL", myWebView.getUrl());
        backupCurrentPage();
        super.onSaveInstanceState(outState);
    }

    //save latest page in sharedPreference when save instance does not work or move to other activity
    public void backupCurrentPage(){
        SharedPreferences currentSP = getSharedPreferences("CurrentPage",MODE_PRIVATE);
        SharedPreferences.Editor ed = currentSP.edit();
        ed.putString("currentKey", myWebView.getUrl());
        ed.apply();
    }

    //check connection status
    public boolean connectionStatus(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() !=null;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.star:
                addBookmark(item);
                return true;
            case R.id.collection:
                viewCollection(item);
                return true;
            case R.id.setHomePage:
                setHomePage(item);

                default:
                return false;
        }
    }

    //popup menu setting setHomePage
    private void setHomePage(MenuItem item) {
        Log.d("testSetting", "setHomepage...");
        SharedPreferences homePageSP = getSharedPreferences("HomePage",MODE_PRIVATE);
        SharedPreferences.Editor ed = homePageSP.edit();
        ed.putString("homePageKey", myWebView.getUrl());
        ed.apply();
    }

    //popup menu view bookmarked page
    private void viewCollection(MenuItem item) {
        backupCurrentPage();
        Intent intentGoBookMark = new Intent(this, BookmarkActivity2.class);
        startActivity(intentGoBookMark);
    }

    //popup menu add page in bookmark
    private void addBookmark(MenuItem item) {
        Star star = new Star(myWebView.getTitle(),myWebView.getUrl());
        SharedPreferences collectionPrefers = getSharedPreferences("BOOKMARKS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        String list = collectionPrefers.getString("collections", null);
        if (list==null){
            Log.d("TestAddBookmark", "Not Initialized");
        }
        else {
            //load bookmark data
            Log.d("TestAddBookmark", "Initialized checked add new bookmark");
            Gson gsonExist = new Gson();
            Type type = new TypeToken<List<Star>>(){}.getType();
            bookmarks = gsonExist.fromJson(list, type);
            int size = bookmarks.size();
            for (int i =0; i<size; i++){
                Log.d("TestAddBookmark", "Index "+ Integer.toString(i) + bookmarks.get(i).getTitle());
                if(bookmarks.get(i).getTitle().equals(star.getTitle()) && bookmarks.get(i).getUrl().equals(star.getUrl())){
                    Toast.makeText(this, "This page already bookmarked.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (size>50){
                Toast.makeText(this, "BookMarks over 50, please delete unused.", Toast.LENGTH_SHORT).show();
                Log.d("TestAddBookmark", "bookmark > 50");
                return;
            }
        }
        editor = collectionPrefers.edit();
        bookmarks.add(star);
        Gson gson = new Gson();
        String json = gson.toJson(bookmarks);
        editor.putString("collections", json);
        editor.apply();
    }

    //retrieve the url from intent (bookmark or history)
    private void initialData(){
        Intent intent = getIntent();
        String urlFromHistory = getIntent().getStringExtra("URL_His");
        String urlFromBookMark = getIntent().getStringExtra("URL_BM");
        if (urlFromHistory!=null){
            myWebView.loadUrl(urlFromHistory);
            Log.d("TestIntentData","GobackFromHistory to "+myWebView.getUrl());
        }
        else if(urlFromBookMark!=null) {
            myWebView.loadUrl(urlFromBookMark);
            Log.d("TestIntentData", "GobackFromBookmark to "+myWebView.getUrl());
        }
        else {
            Log.d("TestIntentData","GobackWithoutURL");
            //retrieve shared preference for last url
            SharedPreferences currentSP = getSharedPreferences("CurrentPage",MODE_PRIVATE);
            String lastURL = currentSP.getString("currentKey", null);
            if (lastURL==null){
                SharedPreferences homePageSP = getSharedPreferences("HomePage",MODE_PRIVATE);
                String hpURL = currentSP.getString("homePageKey", null);
                if (hpURL==null){
                    myWebView.loadUrl("https://google.com");
                }else{
                    myWebView.loadUrl(hpURL);
                }
            }
            else{
                myWebView.loadUrl(lastURL);
                currentSP.edit().clear().apply();
            }
        }
    }
}