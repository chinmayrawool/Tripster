package com.mad.tripster;

import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neha5 on 22-04-2017.
 */

public class RequestAdapter extends ArrayAdapter<User> {
    Context mContext;
    int mResource;
    List<User> mData;
    User user;
    StorageReference imageRef;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    String uid;
    DatabaseReference mUserRef;
    FirebaseDatabase db;
    ChildEventListener mUserListener;
    User currUser;
    List<String> friendsLoggedInUser;
    List<String> friendsUserInList;
    List<String> requestList;

    public RequestAdapter(Context context, int resource, List<User> objects) {
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
        friendsLoggedInUser = new ArrayList<String>();
        friendsUserInList = new ArrayList<String>();
        requestList = new ArrayList<String>();

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

        Log.d("demo","inside constructor");
        for(User u: mData){
            requestList.add(u.getUser_id());
        }
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
        Button acceptBtn = (Button) convertView.findViewById(R.id.btn_accept);
        Button declineBtn = (Button) convertView.findViewById(R.id.btn_decline);

        //Picasso.with(mContext).load(user.getImage_url()).into(imageView);
        Glide.with(mContext).using(new FirebaseImageLoader()).load(imageRef.child(user.getImage_url())).into(imageView);
        TextView tv_title = (TextView) convertView.findViewById(R.id.textViewContent);
        tv_title.setText("Name: "+user.getUserfirstname()+" "+user.getUserlastname()+"\n"+"Gender: "+user.getGender());



        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove from reqlist, add into both friends list
                user = mData.get(position);
                friendsLoggedInUser = currUser.getFriends();
                friendsUserInList = user.getFriends();

                Log.d("demo", "Current user friends: " +currUser.getFriends().toString());
                Log.d("demo", "List user's friends: "+user.getFriends());

                String uid2 = user.getUser_id();
                requestList.remove(requestList.indexOf(uid2));
                List<String> sentReqList = user.getSentReq();

                if(sentReqList!=null){
                    sentReqList.remove(sentReqList.indexOf(currUser.getUser_id()));
                }

                if(friendsLoggedInUser!=null) {
                    if (!friendsLoggedInUser.contains(uid2)) {
                        friendsLoggedInUser.add(uid2);
                    }
                }else{
                    friendsLoggedInUser = new ArrayList<String>();
                    friendsLoggedInUser.add(uid2);
                }

                if(friendsUserInList!=null) {
                    if (!friendsUserInList.contains(currUser.getUser_id())) {
                        friendsUserInList.add(currUser.getUser_id());
                    }
                }else{
                    friendsUserInList = new ArrayList<String>();
                    friendsUserInList.add(currUser.getUser_id());
                }
                currUser.setFriends(friendsLoggedInUser);
                currUser.setReceivedReq(requestList);
                user.setFriends(friendsUserInList);
                user.setSentReq(sentReqList);
                mUserRef.child(uid2).setValue(user);
                mUserRef.child(uid).setValue(currUser);
                v.setVisibility(View.INVISIBLE);
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = mData.get(position);

                String uid3 = user.getUser_id();
                List<String> sentReqList = user.getSentReq();
                sentReqList.remove(sentReqList.indexOf(currUser.getUser_id()));
                user.setSentReq(sentReqList);
                requestList.remove(requestList.indexOf(uid3));
                currUser.setReceivedReq(requestList);
                mUserRef.child(uid).setValue(currUser);
                mUserRef.child(uid3).setValue(user);
                v.setVisibility(View.INVISIBLE);
            }
        });

        convertView.setClickable(true);
        convertView.setLongClickable(true);

        if(position%2==0){
            convertView.setBackgroundColor(Color.rgb(240,240,240));
            Log.d("demo","position: Even");
        }else{
            convertView.setBackgroundColor(Color.WHITE);
            Log.d("demo","position: Odd");
        }

        return convertView;
    }
    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }
}
