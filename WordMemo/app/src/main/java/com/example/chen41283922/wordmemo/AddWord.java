package com.example.chen41283922.wordmemo;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;


public class AddWord extends AppCompatActivity {

    private MyDB db = null;

    EditText edtWord,edtDef;
    Button btnAdd,btnChange,btnDel,btnClear;
    ListView listWord;
    Cursor cursor;
    //----------------------------
    Spinner spnType;
    CharSequence[] WordType = {"(n.)","(v.)","(adj.)","(adv.)","(prep.)","(conj.)"};
    CharSequence WordTypeOpt;
    //----------------------------
    CharSequence[] time = {"五秒","十五分鐘","半小時","一小時","十二小時"};
    int delFlag = 0;
    long myid;
//------------------------------------ALARM-------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtWord = (EditText) findViewById(R.id.edtWord);
        edtDef = (EditText) findViewById(R.id.edtDef);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnChange = (Button) findViewById(R.id.btnChange);
        btnDel = (Button) findViewById(R.id.btnDel);
        btnClear = (Button)findViewById(R.id.btnClear);
        spnType = (Spinner) findViewById(R.id.spnType);
        listWord = (ListView) findViewById(R.id.listWord);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnAdd.setOnClickListener(myListener);
        btnChange.setOnClickListener(myListener) ;
        btnDel.setOnClickListener(myListener) ;
        btnClear.setOnClickListener(myListener) ;
        fab.setOnClickListener(myListener);

        db = new MyDB(this);
        db.open();
        cursor = db.getAll();
        UpdateAdapter(cursor);

//-----------------------------------SPINNER---------------------------------------------
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, WordType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adapter);
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                WordTypeOpt = WordType[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Log.e("no_choice", "請選擇詞性");
            }
        });

//--------------------------------LISTVIEW  ---------------------------

        listWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowData(id);
                cursor.moveToPosition(position);
                delFlag = 1;
            }
        });
    }//onCreate


//-----------------------------------------BUTTON------------------------------------------------------------------------------
    private Button.OnClickListener myListener = new Button.OnClickListener(){

        String INTERVAL ;

        public void onClick(View v){
                switch (v.getId()) {
                    case R.id.btnAdd: {
                        String word = edtWord.getText().toString();
                        String def = edtDef.getText().toString();
                        String type = spnType.getSelectedItem().toString();
                        if (word.isEmpty() || def.isEmpty()) {
                            Toast.makeText(AddWord.this,"欄位不得為空值",Toast.LENGTH_SHORT).show();
                        }else{
                            db.append(word,def,type);
                            cursor = db.getAll();
                            UpdateAdapter(cursor);
                            ClearEdit();
                        }
                        break;
                    }
                    case R.id.btnChange: {
                        String word = edtWord.getText().toString();
                        String def = edtDef.getText().toString();
                        String type = spnType.getSelectedItem().toString();
                        if (word.isEmpty() || def.isEmpty()) {
                            Toast.makeText(AddWord.this,"請選取資料",Toast.LENGTH_SHORT).show();
                        }else{
                            db.update(myid, word, def,type);
                            cursor = db.getAll();
                            UpdateAdapter(cursor);
                        }
                        break;
                    }
                    case R.id.btnDel: {
                        if (cursor != null && cursor.getCount() > 0 && delFlag != 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddWord.this);
                            builder.setTitle("確定刪除");
                            builder.setMessage("確定刪除 " + edtWord.getText() + " 這筆資料？");
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (db.delete(myid)) {
                                        cursor = db.getAll();
                                        UpdateAdapter(cursor);
                                        ClearEdit();
                                        Toast.makeText(AddWord.this, "刪除完畢", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.show();
                        }//if
                        break;
                    }
                    case R.id.btnClear: {
                        ClearEdit();
                        delFlag = 0;
                        break;
                    }
                    case R.id.fab:{
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddWord.this);
                        builder.setTitle("設定提醒間隔");
                        builder.setSingleChoiceItems(time, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: {//5 秒
                                        INTERVAL = "5 seconds";
                                        break;
                                    }
                                    case 1: {//15 分鐘
                                        INTERVAL = "15 minutes";
                                        break;
                                    }
                                    case 2: {//30 分鐘
                                        INTERVAL = "30 minutes";
                                        break;
                                    }
                                    case 3: {//1 小時
                                        INTERVAL = "1 hours";
                                        break;
                                    }
                                    case 4: {//12 小時
                                        INTERVAL = "12 hours";
                                        break;
                                    }
                                }
                            }
                        });
                        builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setNeutralButton("取消鬧鐘", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CreateOrCancelAlarm("cancel");
                                Toast.makeText(AddWord.this, "取消鬧鐘", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CreateOrCancelAlarm(INTERVAL);
                                Toast.makeText(AddWord.this, "設定成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                    }
                }// switch
        }//onClick
    };

//----------------------------------------------------------------------------------------------

    private void ShowData(long id){
        ArrayAdapter<CharSequence> selected = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, WordType);
        Cursor c =db.get(id);
        myid = id;
        edtWord.setText(c.getString(1));
        edtDef.setText(c.getString(2));
        spnType.setSelection(selected.getPosition(c.getString(3)));

    }

    public void ClearEdit(){
        edtWord.setText("");
        edtDef.setText("");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }

    public void UpdateAdapter (Cursor cursor){
        if ( cursor != null && cursor.getCount() >= 0){
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.mylayout,
                    cursor,
                    new String[] {"word","type","def"},
                    new int[] {R.id.word ,R.id.type, R.id.def},
                    0);
            listWord.setAdapter(adapter);
        }
    }

    private void CreateOrCancelAlarm(String INTERVAL){
        Intent intent = new Intent(AddWord.this, PlayReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(AddWord.this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        switch (INTERVAL) {
            case "5 seconds":{am.setRepeating(AlarmManager.RTC_WAKEUP, 5*1000, 5*1000, pi);
                break;}
            case "15 minutes":{
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                        AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
                break;}
            case "30 minutes":{
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        AlarmManager.INTERVAL_HALF_HOUR,
                        AlarmManager.INTERVAL_HALF_HOUR, pi);
                break;}
            case "60 minutes":{
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        AlarmManager.INTERVAL_HOUR,
                        AlarmManager.INTERVAL_HOUR, pi);
                break;}
            case "12 hours":{
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        AlarmManager.INTERVAL_HALF_DAY,
                        AlarmManager.INTERVAL_HALF_DAY, pi);}
                break;
            case "cancel":{am.cancel(pi);
                break;}
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}//AddWord
