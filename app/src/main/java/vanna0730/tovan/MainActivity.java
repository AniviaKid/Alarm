package vanna0730.tovan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    List<Map<String, String>> list = new ArrayList<>();
    public int add_remind_code=0;
    public int edit_remind_code=1;
    public RecyclerAdapter recyclerAdapter;
    public static MyDatabase myDatabase;
    public AlarmManager alarmManager;
    public long oneday=24*60*60*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);

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
                /*if(which==0) Toast.makeText(MainActivity.this,position+" ShortClick",Toast.LENGTH_SHORT).show();
                else if(which==1) Toast.makeText(MainActivity.this,"开关已开启",Toast.LENGTH_SHORT).show();
                else if(which==2) Toast.makeText(MainActivity.this,"开关已关闭",Toast.LENGTH_SHORT).show();*/
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
                IntentFilter intentFilter=new IntentFilter();
                intentFilter.addAction(remind);
                registerReceiver(broadcastReceiver,intentFilter);
                alarmOne(remind,list.size(),Integer.parseInt(hour),Integer.parseInt(minute));//第二个参数是用第几个item作为code发送
                UpdateView();
                Toast.makeText(MainActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void UpdateView(){
        list.clear();
        SQLiteDatabase db=myDatabase.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+MyDatabase.Table_name,null);
        if(cursor.moveToFirst()==false) return;
        else{
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

    public BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("test_received","已收到");
            for (int i=0;i<list.size();i++){
                if(list.get(i).get("instruction").equals(intent.getAction())){
                    Log.e("test_received",intent.getAction());
                    Toast.makeText(MainActivity.this,intent.getAction(),Toast.LENGTH_SHORT).show();
                    Intent alaramIntent = new Intent("test");
                    Bundle bundle=new Bundle();
                    bundle.putString("key",list.get(i).get("instruction"));
                    alaramIntent.putExtras(bundle);
                    alaramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(alaramIntent);
                    break;
                }
            }
        }
    };

    public void alarmOne(final String action, final int code,int hour,int minute){
        Intent intent=new Intent();
        intent.setAction(action);
        //将来时态的跳转
        PendingIntent pendingIntent=PendingIntent.getBroadcast(MainActivity.this,code,intent,0);
        //设置闹钟
        long aim_time=(hour*60+minute)*60*1000;
        Calendar calendar=Calendar.getInstance();
        int now_hour=calendar.get(Calendar.HOUR_OF_DAY);
        int now_minute=calendar.get(Calendar.MINUTE);
        long now_time=(now_hour*60+now_minute)*60*1000;
        long during=aim_time>=now_time?aim_time-now_time:aim_time-now_time+oneday;
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+5000,oneday,pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+during,pendingIntent);
        Log.e("test_sent","已发送");
        Log.e("test_sent",intent.getAction());
        Log.e("test_sent",hour+" "+minute);
        //Toast.makeText(this,now_hour+":"+now_minute+" "+hour+":"+minute+" "+during,Toast.LENGTH_SHORT).show();
    }
}
