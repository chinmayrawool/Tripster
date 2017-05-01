package com.mad.tripster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase db;
    DatabaseReference mUserRef;
    ChildEventListener mUserListener;
    FirebaseStorage storage;
    StorageReference imageRef;

    TextView tv;
    ImageView ivProfile;
    TextView userName;
    TextView userGender;
    String userID;
    User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //TODO

        NavigationView navHeaderView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navHeaderView.getHeaderView(0);

        db = FirebaseDatabase.getInstance();
        mUserRef = db.getReference().child("users");
        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference();
        //users = new ArrayList<User>();
        ivProfile = (ImageView) headerView.findViewById(R.id.imageViewProfileHeader);
        userName = (TextView) headerView.findViewById(R.id.textViewUserName);
        userGender = (TextView) headerView.findViewById(R.id.textViewUserGender);

        userID = mAuth.getCurrentUser().getUid();

        mUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User usr = dataSnapshot.getValue(User.class);
                if(usr.getUser_id().equals(userID)){
                    currUser = usr;
                    userName.setText(currUser.getUserfirstname()+ " "+currUser.getUserlastname());
                    userGender.setText(currUser.getGender());
                    //Picasso.with(getApplicationContext()).load(currUser.getImage_url()).into(ivProfile);
                    Glide.with(ContentActivity.this).using(new FirebaseImageLoader()).load(imageRef.child(currUser.getImage_url())).into(ivProfile);
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



        /*mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference().child("users");*/

        getFragmentManager().beginTransaction()
                .replace(R.id.container,new FriendsFragment(),"frag_friends").commit();

        if(mAuth.getCurrentUser()==null){
            //Intent to login activity
            finish();
            Log.d("demo","Intent to profile activity");
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("demo", "onAuthStateChanged:signed_in:" + user.getUid());
                    //finish();
                    Log.d("demo",mAuth.getCurrentUser().getEmail()+mAuth.getCurrentUser().getUid());
                    //startActivity(new Intent(getApplicationContext(),ContentActivity.class));

                } else {
                    // User is signed out
                    Log.d("demo", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        tv = (TextView) findViewById(R.id.tv_header);
        tv.setText("USERS");
        tv.setGravity(Gravity.CENTER);


        //tv.setText(mAuth.getCurrentUser().getEmail());
        Log.d("demo",mAuth.getCurrentUser().getEmail()+" "+mAuth.getCurrentUser().getUid());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_trip);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //uid and call add trip activity

                Intent intent = new Intent(ContentActivity.this,AddTripActivity.class);
                intent.putExtra("User",mAuth.getCurrentUser().getUid());
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trip) {
            // Handle the camera action
            Log.d("demo","Trips clicked");
            tv.setText("TRIPS");
            tv.setGravity(Gravity.CENTER);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container,new TripsFragment(),"frag_trips").commit();
        } else if (id == R.id.nav_friends) {
            Log.d("demo","Friends clicked");
            tv.setText("USERS");
            tv.setGravity(Gravity.CENTER);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container,new FriendsFragment(),"frag_friends").commit();
            //Log.d("demo",users.toString());
        } else if (id == R.id.nav_requests) {
            Log.d("demo","Requests clicked");
            getFragmentManager().beginTransaction()
                    .replace(R.id.container,new RequestsFragment(),"frag_requests").commit();
            tv.setText("REQUESTS");
            tv.setGravity(Gravity.CENTER);
        } else if (id == R.id.nav_profile) {
            Log.d("demo","Profile clicked");
            Intent intent = new Intent(ContentActivity.this,ProfileEditActivity.class);
            intent.putExtra("User_ID",mAuth.getCurrentUser().getUid());
            startActivity(intent);
            //tv.setText("Profile page");
        } else if (id == R.id.nav_logout) {
            Log.d("demo","Logout clicked");
            //tv.setText("Logout page");
            mAuth.signOut();
            Intent intent = new Intent(ContentActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
        //mAuth.signOut();
    }


}
