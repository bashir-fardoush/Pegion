package com.example.dell.pegion;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DELL on 5/30/2018.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {
   public View view;
   public CircleImageView thumIV;
   public TextView nameTV;
   public TextView statusTV;

    public UserViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    thumIV = itemView.findViewById(R.id.profile_image);
    nameTV = itemView.findViewById(R.id.user_name);
    statusTV = itemView.findViewById(R.id.user_status);
    }
   public void setUserImage(String imageUrl, Context context){
       if (imageUrl!="link"){
           Picasso.get().load(imageUrl).placeholder(R.drawable.default_person_image).into(thumIV);
       }
    }
    public void setUserName(String name){
        nameTV.setText(name);
    }
    public void setUserStatus(String status){

        statusTV.setText(status);
    }
}
