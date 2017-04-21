package com.mad.tripster;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

/**
 * Created by Chinmay Rawool on 4/20/2017.
 */

public class FriendAdapter extends ArrayAdapter<User> {
    Context mContext;
    int mResource;
    List<User> mData;
    User user;
    ListAdapter listAdapter;

    public FriendAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource =resource;
        mData = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }
        user = mData.get(position);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imageViewIcon);
        Picasso.with(mContext).load(user.getImage_url()).into(imageView);
        TextView tv_title = (TextView) convertView.findViewById(R.id.textViewContent);
        tv_title.setText("Name: "+user.getUserfirstname()+" "+user.getUserlastname()+"\n"+"Gender: "+user.getGender());

        convertView.setClickable(true);
        convertView.setLongClickable(true);
        return convertView;
    }
    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }
}
