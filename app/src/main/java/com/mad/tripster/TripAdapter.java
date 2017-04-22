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

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Chinmay Rawool on 4/20/2017.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private List<Trip> mData;
    // Store the context for easy access
    private Context mContext;

    public TripAdapter(Context mContext,List<Trip> mData) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView titleTextView;
        public TextView locationTextView;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Trip trip = mData.get(position);
        TextView tv1 = holder.titleTextView;
        TextView tv2 = holder.locationTextView;
        ImageView iv = holder.imageView;
        try {
            tv1.setText(trip.getTitle());
            tv2.setText(trip.getLocation());
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        Picasso.with(mContext).load(trip.getImage_url()).into(iv);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ChatRoomActivity.class);
                intent.putExtra("TripID",trip.getTrip_id());
                mContext.startActivity(intent);
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
