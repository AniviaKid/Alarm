package vanna0730.tovan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ASUS on 2018/3/26.
 */

public class AddRemind extends AppCompatActivity implements View.OnClickListener{

    final public int cancel_code=0;
    final public int save_code=1;
    public String remind_content="test";
    TextView content_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_remind);

        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.content).setOnClickListener(this);
        content_text=(TextView)findViewById(R.id.content_text);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.cancel){
            this.setResult(cancel_code);
            this.finish();
        }
        else if(v.getId()==R.id.save){
            TimePicker timePicker=(TimePicker)findViewById(R.id.time_picker);
            String hour=timePicker.getCurrentHour().toString();
            String minute=timePicker.getCurrentMinute().toString();
            Intent intent=new Intent();
            intent.putExtra("hour",hour);
            intent.putExtra("minute",minute);
            intent.putExtra("remind",remind_content);
            this.setResult(save_code,intent);
            this.finish();
        }
        else if(v.getId()==R.id.content){
            LayoutInflater factory=LayoutInflater.from(this);
            View EntryView=factory.inflate(R.layout.edit_content,null);
            final EditText editText=(EditText) EntryView.findViewById(R.id.input);
            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setView(EntryView);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    remind_content=editText.getText().toString();
                    content_text.setText(remind_content);
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        }
    }
}
