package com.zwh.quickstart.quickstart;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.provider.Contacts;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchActivity extends ListActivity {
    private Cursor cursor;
    private ImageSimpleAdapter mSchedule;
    private DatabaseTable db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        EditText searchView =
                (EditText) findViewById(R.id.search_view);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("name>>>", "—–onTextChanged:"+s );
                handleIntent(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                Log.e("name>>>", "—–onEditorAction :"+ actionId);

                if(event != null) {
                    Log.e("name>>>", "—–onEditorActio  :" + event.getKeyCode());
                }

                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    if( null!=cursor && cursor.getCount() >= 1 ){
                        cursor.moveToFirst();
                        startActivity(cursor.getString(3), cursor.getString(2));
                    }
                }
                return false;
            }
        });

        db = new DatabaseTable(this);
        cursor = db.getWordMatches(null, null);
        mSchedule = new ImageSimpleAdapter(this,cursor);
        this.setListAdapter(mSchedule);

        searchView.requestFocus();
    }
    /*@Override
    public void onStart(){
        super.onStart();
        InputMethodManager imm = (InputMethodManager)this.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }*/

    private void handleIntent(CharSequence query) {
            //得到输入要搜索的内容，然后进行分析展示
            Log.e("name>>>", "—–" + query);
            cursor = db.getWordMatches(query, null);
            mSchedule.changeCursor(cursor);

        if( null!=cursor && cursor.getCount() == 1 ){
            cursor.moveToFirst();
            startActivity(cursor.getString(3), cursor.getString(2));
        }
    }

    protected void onListItemClick (ListView l, View v, int position, long id){
        cursor.moveToPosition(position);
        startActivity(cursor.getString(3), cursor.getString(2));
    }

    private void startActivity(String packageName, String name){
        if (this.getPackageName().equals(packageName)){
            return;
        }

        Intent intent=new Intent();
        intent.setClassName(packageName, name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);

        System.exit(0);
    }
}
