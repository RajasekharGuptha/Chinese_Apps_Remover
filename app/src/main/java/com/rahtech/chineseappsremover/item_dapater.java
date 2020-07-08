package com.rahtech.chineseappsremover;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class item_dapater extends RecyclerView.Adapter<sub_viewHolder> {

    private MainActivity mainActivity;
    private ArrayList<item> items;

    public item_dapater(MainActivity mainActivity, ArrayList<item> items) {
        this.mainActivity = mainActivity;
        this.items = items;
    }

    @NonNull
    @Override
    public sub_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mainActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.list_item_layout,parent,false);

        return new sub_viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final sub_viewHolder holder, final int position) {

        if(items.get(position).getAppname().length()>20){
            holder.app_name.setTextSize(15);
        }
        holder.app_name.setText(items.get(position).getAppname());
        holder.icon.setBackground(items.get(position).getAppIcon());
        holder.pkgname.setText(items.get(position).getPackageName());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(mainActivity,holder.delete);
                //inflating menu from xml resource
                popup.inflate(R.menu.recycle_options);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.alternatives:
                                //handle menu1 click
                                Intent i=new Intent(mainActivity,suggestions.class);
                                i.putExtra("pkg",items.get(position).getAppname());
                                i.putExtra("suggs",items.get(position).getSimilar());
                                i.putExtra("app_name",items.get(position).getAppname());
                                i.putExtra("uninstall",false);
                                mainActivity.startActivity(i);
                                return true;
                            case R.id.uninstall:
                                //handle menu2 click
                                Intent i1=new Intent(mainActivity,suggestions.class);
                                i1.putExtra("pkg",items.get(position).getPackageName());
                                i1.putExtra("app_name",items.get(position).getAppname());
                                i1.putExtra("suggs",items.get(position).getSimilar());
                                i1.putExtra("uninstall",true);
                                mainActivity.startActivity(i1);

                                return true;

                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();


            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}
