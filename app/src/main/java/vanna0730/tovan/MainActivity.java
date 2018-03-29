package vanna0730.tovan;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    List<Map<String, String>> list = new ArrayList<>();
    public int add_remind_code=0;
    public int edit_remind_code=1;
    public RecyclerAdapter recyclerAdapter;
    public static MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init_RecyclerView();
        findViewById(R.id.add_new_remind).setOnClickListener(this);
        myDatabase=new MyDatabase(this,MyDatabase.Database_name);
        UpdateView();
    }

    public void Init_RecyclerView(){
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerAdapter=new RecyclerAdapter(MainActivity.this,list);
        recyclerAdapter.setItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View v, int position, int which) {
                if(which==0) Toast.makeText(MainActivity.this,position+" ShortClick",Toast.LENGTH_SHORT).show();
                else if(which==1) Toast.makeText(MainActivity.this,"开关已开启",Toast.LENGTH_SHORT).show();
                else if(which==2) Toast.makeText(MainActivity.this,"开关已关闭",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnLongClick(View v, final int position) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("确定删除这个提醒？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content=list.get(position).get("instruction");
                        myDatabase.delete(content);
                        UpdateView();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.add_new_remind){
            startActivityForResult(new Intent("android.intent.action.AddRemind"),add_remind_code);//添加提醒
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==add_remind_code){
            if(resultCode==1){//成功添加
                String hour=data.getStringExtra("hour");
                String minute=data.getStringExtra("minute");
                String remind=data.getStringExtra("remind");
                myDatabase.insert(hour,minute,remind);
                UpdateView();
            }
        }
    }

    public void UpdateView(){
        SQLiteDatabase db=myDatabase.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+MyDatabase.Table_name,null);
        if(cursor.moveToFirst()==false) return;
        else{
            list.clear();
            do{
                int hour_index=cursor.getColumnIndex("hour");
                String hour=cursor.getString(hour_index);
                Integer number_hour=Integer.parseInt(hour);
                if(number_hour<10) hour="0"+hour;
                int minute_index=cursor.getColumnIndex("minute");
                String minute=cursor.getString(minute_index);
                Integer number_minute=Integer.parseInt(minute);
                if(number_minute<10) minute="0"+minute;
                int content_index=cursor.getColumnIndex("content");
                String content=cursor.getString(content_index);
                Map<String,String> tmp=new HashMap<>();
                tmp.put("time",hour+":"+minute);
                tmp.put("instruction",content);
                list.add(tmp);
            }while (cursor.moveToNext());
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}
