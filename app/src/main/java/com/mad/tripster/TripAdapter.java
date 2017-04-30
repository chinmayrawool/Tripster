package com.mad.tripster;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Chinmay Rawool on 4/20/2017.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private List<Trip> mData;
    // Store the context for easy access
    private Context mContext;
    private String userID;

    FirebaseStorage storage;
    StorageReference imageRef;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mTripsRef;

    public TripAdapter(Context mContext,List<Trip> mData, String userID) {
        this.mData = mData;
        this.mContext = mContext;
        this.userID = userID;

        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference();

        mDatabase = FirebaseDatabase.getInstance();
        mTripsRef = mDatabase.getReference().child("trips");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView titleTextView;
        public TextView locationTextView;
        public ImageView ivDeleteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            //list view
            /*imageView = (ImageView) itemView.findViewById(R.id.iv_thumb);
            nameTextView = (TextView) itemView.findViewById(R.id.textView1);
            descriptionTextView = (TextView) itemView.findViewById(R.id.textView2);
            linear = (LinearLayout) itemView.findViewById(R.id.linear_insert);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);
*/

            //grid view
            imageView = (ImageView) itemView.findViewById(R.id.imageViewCover);
            titleTextView = (TextView) itemView.findViewById(R.id.textViewTitle);
            locationTextView = (TextView) itemView.findViewById(R.id.textView2);
            ivDeleteIcon = (ImageView) itemView.findViewById(R.id.iv_delete_icon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Trip trip = mData.get(position);
        TextView tv1 = holder.titleTextView;
        TextView tv2 = holder.locationTextView;
        ImageView iv = holder.imageView;
        ImageView ivDelete = holder.ivDeleteIcon;
        ivDelete.setEnabled(false);

        if(trip.getCreated_by().equals(userID)){
            ivDelete.setEnabled(true);
            ivDelete.setVisibility(View.VISIBLE);
        }

        try {
            tv1.setText(trip.getTitle());
            tv2.setText(trip.getLocation());
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        //Picasso.with(mContext).load(trip.getImage_url()).into(iv);
        Glide.with(mContext).using(new FirebaseImageLoader()).load(imageRef.child(trip.getImage_url())).into(iv);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,TripShowActivity.class);
                intent.putExtra("TripID",trip.getTrip_id());
                mContext.startActivity(intent);
                /*Intent intent = new Intent(mContext,ChatRoomActivity.class);
                intent.putExtra("TripID",trip.getTrip_id());
                mContext.startActivity(intent);*/
            }
        });


        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("demo","Delete trip clicked");
                //make view invisible, delete trip from firebase db
                holder.itemView.setVisibility(View.INVISIBLE);
                mTripsRef.child(trip.getTrip_id()).removeValue();

            }
        });
    }

  /*  @Override
    public void onBindViewHolder(ViewHolder holder, int position) {




    }
*/
    @Override
    public int getItemCount() {
        return mData.size();
    }

    private Context getContext() {
        return mContext;
    }

}
