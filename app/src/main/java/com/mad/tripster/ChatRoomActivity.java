package com.mad.tripster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ChatRoomActivity extends AppCompatActivity {

    ImageView Logout, SendMessage, SendPhoto;
    FirebaseAuth firebaseAuth;
    TextView tv_name;
    EditText messageText;
    int PICK_IMAGE_REQUEST = 111;
    //FirebaseHandler handler;
    User currentUser;
    String email;
    FirebaseDatabase db;
    DatabaseReference mMessagesRef,mUserRef;
    LinearLayout chatslayout;
    String userUID;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference imageRef;
    //ValueEventListener v;
    String userDisplayName;
    String tripId;
    String uid;
    ChildEventListener mMessagesListener;
    ChildEventListener mUserListener;
    FirebaseAuth mAuth;
    User currUser;
    View view1;

    //FirebaseMessageHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mAuth = FirebaseAuth.getInstance();
        tripId = getIntent().getExtras().getString("TripID");
        Log.d("demo",tripId);
        Logout = (ImageView) findViewById(R.id.logout);
        SendMessage = (ImageView) findViewById(R.id.addMessage);
        SendPhoto = (ImageView) findViewById(R.id.addImage);
        tv_name = (TextView) findViewById(R.id.user_name);
        messageText = (EditText) findViewById(R.id.messageText);
        chatslayout = (LinearLayout) findViewById(R.id.linearChats);
        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d("ChatActivity", user.toString());

        db = FirebaseDatabase.getInstance();
        mMessagesRef = db.getReference().child("messages");
        mUserRef = db.getReference().child("users");
        //handler = new FirebaseMessageHandler(rootRef,this);
        final ArrayList<MessageObj> messages = new ArrayList<>();


        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        /*ArrayList<User> users = new ArrayList<>();

        FirebaseUser user = firebaseAuth.getCurrentUser();*/

        if(user!=null) {
            userDisplayName=user.getDisplayName();
            uid = user.getUid();
            //handler.retrieveMessages();
            //Log.d("DISPLAYNAME:",userDisplayName);
            tv_name.setText(userDisplayName);
        }else{
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }


        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rootRef.removeEventListener(v);
                String text = messageText.getText().toString().trim();
                messageText.setText("");
                Log.d("demo","Send message ");
                if(!text.equals("")){
                    String id = String.valueOf(UUID.randomUUID());
                    MessageObj messageObj = new MessageObj(id,text,userDisplayName, System.currentTimeMillis(), true);
                    Log.d("demo","UserID:");
                    mMessagesRef.child(tripId).child(id).setValue(messageObj);
                    Log.d("demo","Message"+messageObj.toString());
                }
            }
        });

        SendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rootRef.removeEventListener(v);
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, PICK_IMAGE_REQUEST);
            }
        });

        mMessagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageObj msg = dataSnapshot.getValue(MessageObj.class);
                messages.add(msg);
                Log.d("demo","message: "+msg.toString());
                onReturnMessage(messages);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MessageObj msg = dataSnapshot.getValue(MessageObj.class);
                messages.remove(msg);
                onReturnMessage(messages);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //mMessagesRef.child(tripId).addChildEventListener(mMessagesListener);

        mUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User usr = dataSnapshot.getValue(User.class);
                if(usr.getUser_id().equals(uid)){
                    currUser = usr;
                    userDisplayName = currUser.getUserfirstname()+" "+currUser.getUserlastname();
                    tv_name.setText(userDisplayName);
                    for(int i=0;i<2000;i++){

                    }
                    mMessagesRef.child(tripId).addChildEventListener(mMessagesListener);
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

    ArrayList<MessageObj> messages = new ArrayList<>();

    public void onReturnMessage(ArrayList<MessageObj> msgs) {
        messages = msgs;
        Log.d("demo","UserID:");
        Log.d("demo","dsdas"+messages.toString());
        chatslayout.removeAllViews();
        Collections.sort(messages,MessageObj.timeComp);

        for(final MessageObj m:messages){
            ArrayList<String> delMsgList = getDeleteMessage();
            if(delMsgList.contains(m.getId())){
                Log.d("demo","Msg deleted");
            }else{
                view1 = getLayoutInflater().inflate(R.layout.message, null);
                TextView textViewname = (TextView) view1.findViewById(R.id.messageName);
                TextView textViewtext = (TextView) view1.findViewById(R.id.messageText);
                TextView textViewtime = (TextView) view1.findViewById(R.id.messageTime);
                ImageView imageViewI = (ImageView) view1.findViewById(R.id.messageImage);

                textViewname.setText(m.getName());

                textViewtime.setText(new PrettyTime(new Locale("")).format(new Date(m.getTime())));

                if (m.getTextFlag()) {
                    //true=message, false = image
                    textViewtext.setText(m.getContent());

                } else {
                    //Glide.with(ProfileEditActivity.this).using(new FirebaseImageLoader()).load(imageRef.child(currUser.getImage_url())).into(imageViewProfile);
                    Glide.with(this)
                            .using(new FirebaseImageLoader())
                            .load(imageRef.child(m.getContent()))
                            .into(imageViewI);
                }
                final EditText input = new EditText(ChatRoomActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);

                view1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //chatslayout.removeView(v);
                        Log.d("demo","Deleted");
                        String uid = mAuth.getCurrentUser().getUid();
                        String deleteMsg = currUser.getDeleteMsg();
                        StringBuilder sb = new StringBuilder();
                        if (!deleteMsg.equals("")) {
                            sb.append(deleteMsg);
                            sb.append("@@");
                            sb.append(m.getId());
                        } else {
                            sb.append(m.getId());
                        }
                        currUser.setDeleteMsg(sb.toString());
                        mUserRef.child(currUser.getUser_id()).setValue(currUser);
                        chatslayout.removeAllViews();
                        onReturnMessage(messages);
                        return false;
                    }
                });
                chatslayout.addView(view1);

            }

        }



    }

    public ArrayList<String> getDeleteMessage(){
        ArrayList<String> list = new ArrayList<>();

            if(currUser.getDeleteMsg()!=null) {
                String temp = currUser.getDeleteMsg();

                String[] array = temp.split("@@");

                if (array.length != 0) {
                    for (String a : array) {
                        list.add(a);
                    }
                }
            }
        Log.d("demo","Print list:"+list.size());
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageViewI = (ImageView) view1.findViewById(R.id.messageImage);;

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){

            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageViewI = (ImageView) view1.findViewById(R.id.messageImage);
                imageViewI.setImageBitmap(selectedImage);

                final String image_id = String.valueOf(UUID.randomUUID());
                final String path = "images/"+ image_id+".jpg";
                StorageReference imageRef = storage.getReference(path);
                UploadTask uploadTask = imageRef.putFile(imageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });

                String id = String.valueOf(UUID.randomUUID());
                MessageObj messageObj = new MessageObj(id,path,userDisplayName, System.currentTimeMillis(), false);
                Log.d("demo","UserID:");
                mMessagesRef.child(tripId).child(id).setValue(messageObj);
                Log.d("demo","Message"+messageObj.toString());
                /*Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(imageRef.child(messageObj.getContent()))
                        .into(imageViewI);*/

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //imageViewProfile.setTag("imageReceived");



            //Log.d("writeprofile", imageViewI.getTag().toString());
            /*imageViewI.setDrawingCacheEnabled(true);
            imageViewI.buildDrawingCache();
            Bitmap bitmap = imageViewI.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();*/




            //MessageObj(String id, String content, String name, long time, Boolean textFlag)


        }
    }

}
