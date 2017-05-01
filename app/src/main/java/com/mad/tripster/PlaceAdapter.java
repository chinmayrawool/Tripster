package com.mad.tripster;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chinmay Rawool on 3/20/2017.
 */

public class PlaceAdapter extends ArrayAdapter<PlaceObject> {
    Context mContext;
    int mResource;
    ArrayList<PlaceObject> mData;
    PlaceObject place;
    //ListView listView;
    //PlaceAdapter placeAdapter;
    public PlaceAdapter(Context context, int resource, ArrayList<PlaceObject> places) {
        super(context, resource, places);
        this.mContext=context;
        this.mResource= resource;
        this.mData = places;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }
        place = mData.get(position);
        TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        tv_title.setText("Title: "+place.getPlace_name()+"");
        TextView tv_price = (TextView) convertView.findViewById(R.id.tv_price);
        tv_price.setText("LatLng: "+new LatLng(place.getPlace_lat(),place.getPlace_lng()));
        final ImageButton imageButton = (ImageButton)convertView.findViewById(R.id.iv_star);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "btn clicked"+position, Toast.LENGTH_SHORT).show();
                //Remove place from the database
                Log.d("demo","btn clicked:"+position);

            }
        });
        convertView.findViewById(R.id.iv_star).setFocusable(false);
        convertView.findViewById(R.id.iv_star).setFocusableInTouchMode(false);
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
