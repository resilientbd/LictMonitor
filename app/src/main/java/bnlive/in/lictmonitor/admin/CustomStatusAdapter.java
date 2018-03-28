package bnlive.in.lictmonitor.admin;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import bnlive.in.lictmonitor.R;
import bnlive.in.lictmonitor.model.BatchStatusModel;

import static android.content.ContentValues.TAG;

/**
 * Created by Sk Faisal on 3/26/2018.
 */

public class CustomStatusAdapter extends RecyclerView.Adapter<CustomStatusAdapter.ViewHolder>{
    private String TAG="batchstatusadapter";
    private List<BatchStatusModel> mDataset;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView batchCodeText;
        public TextView statusText;
        public TextView dateText;
        public ViewHolder(View itemView) {

            super(itemView);
            batchCodeText=itemView.findViewById(R.id.batchcode);
            statusText=itemView.findViewById(R.id.status);
            dateText=itemView.findViewById(R.id.date);
        }
    }
    public CustomStatusAdapter(List<BatchStatusModel> myDataset)
    {

        mDataset=myDataset;
        Log.d(TAG,"DatasetSize: "+mDataset.size());
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.statusrecycleitem, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        Log.d(TAG,"Debugger in viewholder");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    holder.batchCodeText.setText("Batch Code: "+mDataset.get(position).getBatch_code());
    holder.statusText.setText("Status: "+mDataset.get(position).getStatus());
    holder.dateText.setText("Date: "+mDataset.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG,"Size: "+mDataset.size());
        return mDataset.size();
    }


}
