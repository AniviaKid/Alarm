package vanna0730.tovan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 2018/3/26.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.mViewHolder> {

    private Context mContext;
    private List<Map<String,String>> mdata;
    private OnItemClickListener mOnItemClickListener;

    public void setItemClickListener(OnItemClickListener listener){
        mOnItemClickListener=listener;
    }
    public RecyclerAdapter(Context  context,List<Map<String,String>> datas)
    {
        this.mContext=context;
        this.mdata=datas;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(mContext).inflate(R.layout.recycler_item,parent,false);
        mViewHolder holder=new mViewHolder(v,mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        holder.time.setText(mdata.get(position).get("time"));
        holder.instruction.setText(mdata.get(position).get("instruction"));
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public interface OnItemClickListener{
        void OnClick(View v,int position,int which);
        void OnLongClick(View v,int position);
    }

    public class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        TextView time;
        TextView instruction;
        Switch aSwitch;
        OnItemClickListener mListener;
        int turn_state;//1为开启，0为关闭

        public mViewHolder(View v,OnItemClickListener tmp) {
            super(v);
            time=(TextView)v.findViewById(R.id.time);
            instruction=(TextView)v.findViewById(R.id.instruction);
            aSwitch=(Switch)v.findViewById(R.id.turn);
            this.mListener=tmp;
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
            aSwitch.setOnClickListener(this);
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) turn_state=1;
                    else turn_state=0;
                }
            });
        }

        @Override
        public void onClick(View v) {
            if(mListener!=null){
                if(v.getId()==R.id.turn){
                    if(turn_state==1) mListener.OnClick(v,getPosition(),1);//告诉外部开关处于开启状态
                    else if(turn_state==0) mListener.OnClick(v,getPosition(),2);//告诉外部开关处于关闭状态
                }
                else mListener.OnClick(v,getPosition(),0);//点击的是整个提醒，需要转入设置页面
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(mListener!=null){
                mListener.OnLongClick(v,getPosition());
            }
            return true;
        }
    }
}
