package com.mad.tripster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TripShowActivity extends AppCompatActivity {
    String tripId;
    FirebaseAuth mAuth;
    TextView textViewTitle,textViewLocation;
    ImageView imageViewCover;
    Button btnJoin, btnChatroom;
    ChildEventListener mTripEventListener,mUserListener;
    DatabaseReference mTripRef,mUserRef;
    FirebaseDatabase db;

    User currUser;
    Trip currTrip;
    ArrayList<String> list;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_show);

        mAuth = FirebaseAuth.getInstance();
        tripId = getIntent().getExtras().getString("TripID");
        Log.d("demo",tripId);

        textViewTitle = (TextView) findViewById(R.id.trip_title);
        textViewLocation = (TextView) findViewById(R.id.trip_location);
        imageViewCover = (ImageView) findViewById(R.id.trip_cover);
        btnJoin = (Button) findViewById(R.id.trip_join);
        btnChatroom = (Button) findViewById(R.id.trip_chatroom);

        db = FirebaseDatabase.getInstance();
        mTripRef = db.getReference().child("trips");
        mUserRef = db.getReference().child("users");

        uid = mAuth.getCurrentUser().getUid();

        mUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User usr = dataSnapshot.getValue(User.class);
                if(usr.getUser_id().equals(uid)){
                    currUser = usr;
                    list =  getJoinTrip();
                    mTripRef.addChildEventListener(mTripEventListener);
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

        mTripEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                if(trip.getTrip_id().equals(tripId)){
                    currTrip = trip;
                    display();

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





        /*Intent intent = new Intent(mContext,ChatRoomActivity.class);
                intent.putExtra("TripID",trip.getTrip_id());
                mContext.startActivity(intent);*/
    }

    void display(){
        textViewTitle.setText(currTrip.getTitle().toString());
        textViewLocation.setText(currTrip.getLocation().toString());
        // Display image



        if(list.contains(tripId)){
            btnJoin.setText("Leave");
        }
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.contains(tripId)){
                    list.remove(tripId);
                    StringBuilder sb = new StringBuilder();
                    if(list.size()!=0) {
                        for (String s : list) {
                            sb.append(s);
                            sb.append("@@@@");
                        }
                    }
                    currUser.setJoinedTrip(sb.toString());
                    btnJoin.setText("Join");
                }else{
                    list.add(tripId);
                    StringBuilder sb = new StringBuilder();
                    if(list.size()!=0) {
                        for (String s : list) {
                            sb.append(s);
                            sb.append("@@@@");
                        }
                    }
                    currUser.setJoinedTrip(sb.toString());
                    btnJoin.setText("Leave");
                }
                mUserRef.child(uid).setValue(currUser);
            }
        });

        btnChatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripShowActivity.this,ChatRoomActivity.class);
                intent.putExtra("TripID",currTrip.getTrip_id());
                startActivity(intent);
            }
        });
    }

    public ArrayList<String> getJoinTrip(){
        ArrayList<String> list1 = new ArrayList<>();

        if(currUser.getJoinedTrip()!=null) {
            String temp = currUser.getJoinedTrip();
            String[] array = temp.split("@@@@");
            if (array.length != 0) {
                for (String a : array) {
                    list1.add(a);
                }
            }
        }
        Log.d("demo","Print list:"+list1.size()+list1.toString());
        return list1;
    }
}
