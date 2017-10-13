package com.abhishek.aspmanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ABHISHEK on 07-08-2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    List<AppInfo> MyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv,appversion;
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.idTitle);
            imageView = (ImageView) itemView.findViewById(R.id.idPic);
            appversion = (TextView) itemView.findViewById(R.id.idVersion);
        }
    }

    MyAdapter(List<AppInfo> applist)
    {
        this.MyList = applist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_list, parent, false);
        return new MyViewHolder(ItemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.imageView.setImageDrawable(MyList.get(position).getIcon());
        holder.tv.setText(MyList.get(position).getName());
        holder.appversion.setText(MyList.get(position).getVersion());
    }

    @Override
    public int getItemCount() {
        return MyList.size();
    }
}
