package com.mad.tripster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
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

public class TripShowActivity extends AppCompatActivity {
    String tripId;
    FirebaseAuth mAuth;
    TextView textViewTitle,textViewLocation;
    ImageView imageViewCover;
    Button btnJoin, btnChatroom, btnPlaces, btnInAppMap, btnGoogleMaps;
    ChildEventListener mTripEventListener,mUserListener;
    DatabaseReference mTripRef,mUserRef;
    FirebaseDatabase db;

    User currUser;
    Trip currTrip;
    ArrayList<String> list;
    String uid;
    ArrayList<PlaceObject> placeObjectsList;
    ListView listView;
    PlaceAdapter placeAdapter;

    FirebaseStorage storage;
    StorageReference imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_show);

        mAuth = FirebaseAuth.getInstance();
        tripId = getIntent().getExtras().getString("TripID");
        Log.d("demo",tripId);

        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference();

        textViewTitle = (TextView) findViewById(R.id.trip_title);
        textViewLocation = (TextView) findViewById(R.id.trip_location);
        imageViewCover = (ImageView) findViewById(R.id.trip_cover);
        btnJoin = (Button) findViewById(R.id.trip_join);
        btnChatroom = (Button) findViewById(R.id.trip_chatroom);
        btnPlaces = (Button) findViewById(R.id.trip_places);
        btnInAppMap = (Button) findViewById(R.id.btn_inapp);
        btnGoogleMaps = (Button) findViewById(R.id.btn_extapp);
        placeObjectsList = new ArrayList<>();

        btnPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.d("demo","Add places clicked");
                    Intent intent =
                            new PlaceAutocomplete
                                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(TripShowActivity.this);
                    startActivityForResult(intent, 1111);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnInAppMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripShowActivity.this, MapsActivity.class);
                intent.putExtra("Place_ArrayList",placeObjectsList);
                startActivity(intent);
            }
        });
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
                    Log.d("demo","listener added");
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
                    placeObjectsList = (ArrayList<PlaceObject>) currTrip.getPlaceObjects();
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
        displayPlace();
        // Display image
        //imageViewCover
        Glide.with(this).using(new FirebaseImageLoader()).load(imageRef.child(currTrip.getImage_url())).into(imageViewCover);


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
                    btnChatroom.setEnabled(false);
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
                    btnChatroom.setEnabled(true);
                }
                mUserRef.child(uid).setValue(currUser);

            }
        });

        if(btnJoin.getText().equals("Join")){
            btnChatroom.setEnabled(false);
        }else if(btnJoin.getText().equals("Leave")){
            btnChatroom.setEnabled(true);
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("demo","In Activity Result");
        if (requestCode == 1111) {
            if (resultCode == RESULT_OK) {
                Log.d("demo","In Activity Result Result OK");
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber()+ place.getLatLng() + place.toString());

                LatLng temp = place.getLatLng();
                PlaceObject placeObject = new PlaceObject(String.valueOf(System.currentTimeMillis()),String.valueOf(place.getName()),temp.latitude,temp.longitude,String.valueOf(place.getAddress()));
                if(placeObjectsList==null){
                    placeObjectsList = new ArrayList<>();
                }
                placeObjectsList.add(placeObject);

                currTrip.setPlaceObjects(placeObjectsList);
                mTripRef.child(currTrip.getTrip_id()).setValue(currTrip);


                displayPlace();
            }
        }
    }

    void displayPlace(){
        if(placeObjectsList!=null) {
            if (placeObjectsList.size() != 0) {

                listView = (ListView) findViewById(R.id.ListView_places);
                placeAdapter = new PlaceAdapter(TripShowActivity.this, R.layout.row_layout_place, placeObjectsList);
                placeAdapter.setNotifyOnChange(true);

                listView.setClickable(true);
                listView.setLongClickable(true);
                listView.setAdapter(placeAdapter);
                Log.d("demo", placeObjectsList.size() + "");
            } else {
                Toast.makeText(this, "No Place Locations added!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
