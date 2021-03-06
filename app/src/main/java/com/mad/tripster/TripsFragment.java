package com.mad.tripster;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link TripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseDatabase mDatabase;
    private DatabaseReference mTripsRef;
    private DatabaseReference mUserRef;

    private ChildEventListener mTripListener;
    private ChildEventListener mUserListener;
    List<Trip> trips;
    private RecyclerView mTripsListView;
    private TripAdapter mTripAdapter;

    private FirebaseAuth mAuth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String userID;
    private User currUser;
    private List<String> usersFriends;

    //private OnFragmentInteractionListener mListener;

    public TripsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TripsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripsFragment newInstance(String param1, String param2) {
        TripsFragment fragment = new TripsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trips, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
   /* public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        trips = new ArrayList<Trip>();
        mDatabase = FirebaseDatabase.getInstance();
        mTripsRef = mDatabase.getReference().child("trips");
        mUserRef = mDatabase.getReference().child("users");
        //List<Us> friendlyMessages = new ArrayList<>();
        mTripsListView = (RecyclerView) getActivity().findViewById(R.id.listView_trips);

        mUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User usr = dataSnapshot.getValue(User.class);
                Log.d("demo","user onchildadded");
                if(usr.getUser_id().equals(userID)){
                    Log.d("demo","User found");
                    currUser = usr;
                    usersFriends = currUser.getFriends();
                    mTripsRef.addChildEventListener(mTripListener);
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


        mTripListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                Log.d("demo","trip onchildadded");

                //show only the trips created by you or your friends
                //trip.getcreatedby == curruser.getId OR curruser.getfriends == trip.createdby
                if(trip.getCreated_by().equals(currUser.getUser_id())){
                    Log.d("demo","trip created by you "+trip.getTrip_id());
                    trips.add(trip);
                }else{
                    if(usersFriends!=null) {
                        for (String id : usersFriends) {
                            if (trip.getCreated_by().equals(id)) {
                                Log.d("demo", "trip created by your friends " + trip.getTrip_id());
                                trips.add(trip);
                            }
                        }
                    }
                }

                mTripsListView.setLayoutManager(new GridLayoutManager(getContext(),2, LinearLayoutManager.VERTICAL,false));
                mTripAdapter = new TripAdapter(getContext(), trips, userID);
                mTripsListView.setAdapter(mTripAdapter);
                mTripAdapter.notifyDataSetChanged();
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



    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
