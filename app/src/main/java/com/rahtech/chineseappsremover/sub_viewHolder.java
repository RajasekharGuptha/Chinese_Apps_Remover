package com.rahtech.chineseappsremover;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class sub_viewHolder extends RecyclerView.ViewHolder
{
      TextView suggs_tv;
     TextView app_name;
     TextView pkgname;
     Button delete;
     ImageView icon;
     RelativeLayout sub_background;
    sub_viewHolder(@NonNull View itemView) {
        super(itemView);
        this.icon=itemView.findViewById(R.id.app_icon);
        this.delete=itemView.findViewById(R.id.delete_btn);
        this.app_name=itemView.findViewById(R.id.app_name);
        this.sub_background=itemView.findViewById(R.id.sub_item_layout);
        this.pkgname=itemView.findViewById(R.id.packagename);
    }
}
