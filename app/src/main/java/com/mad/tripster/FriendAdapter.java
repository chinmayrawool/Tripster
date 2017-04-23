package com.mad.tripster;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
    StorageReference imageRef;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    String uid;
    DatabaseReference mUserRef;
    FirebaseDatabase db;
    ChildEventListener mUserListener;
    User currUser;
    List<String> sentRequests;
    List<String> receivedReq;
    List<String> friends;

    public FriendAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource =resource;
        mData = objects;
        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();
        mUserRef = db.getReference().child("users");
        sentRequests = new ArrayList<String>();
        receivedReq = new ArrayList<String>();
        friends = new ArrayList<String>();
        mUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user1 = dataSnapshot.getValue(User.class);

                if(user1.getUser_id().equals(uid)){
                    currUser = user1;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mUserRef.addChildEventListener(mUserListener);
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
        Button requestBtn = (Button) convertView.findViewById(R.id.btn_send_request);

        if(currUser.getSentReq()!=null){
            sentRequests = currUser.getSentReq();
            for(String id: sentRequests) {
                Log.d("demo","sent request to id: "+id);
                Log.d("demo","list user's id:"+user.getUser_id());
                if (user.getUser_id().equals(id)){
                    requestBtn.setVisibility(View.INVISIBLE);
                }
            }
        }

        if(currUser.getFriends()!=null){
            friends = currUser.getFriends();
            for(String id: friends) {
                Log.d("demo","sent request to id: "+id);
                Log.d("demo","list user's id:"+user.getUser_id());
                if (user.getUser_id().equals(id)){
                    requestBtn.setVisibility(View.INVISIBLE);
                }
            }
        }

        //Picasso.with(mContext).load(user.getImage_url()).into(imageView);
        Glide.with(mContext).using(new FirebaseImageLoader()).load(imageRef.child(user.getImage_url())).into(imageView);
        TextView tv_title = (TextView) convertView.findViewById(R.id.textViewContent);
        tv_title.setText("Name: "+user.getUserfirstname()+" "+user.getUserlastname()+"\n"+"Gender: "+user.getGender());

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = mData.get(position);
                if(user.getReceivedReq()!=null) {
                    receivedReq = user.getReceivedReq();
                    Log.d("demo","not null");
                }
                if(!receivedReq.contains(uid)) {
                    receivedReq.add(uid);


                }
                Log.d("demo","new user id: "+user.getUser_id());
                sentRequests.add(user.getUser_id());
                currUser.setSentReq(sentRequests);
                user.setReceivedReq(receivedReq);
                mUserRef.child(user.getUser_id()).setValue(user);
                mUserRef.child(uid).setValue(currUser);
                v.setVisibility(View.INVISIBLE);
            }
        });


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
