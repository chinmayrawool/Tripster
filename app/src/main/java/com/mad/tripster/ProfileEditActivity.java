package com.mad.tripster;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class ProfileEditActivity extends AppCompatActivity {

    EditText editTextFname,editTextLname;
    Spinner genderSpinner;
    ImageView imageViewProfile;
    Button btnCancel,btnSave;
    String userID;

    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mUserRef;
    ChildEventListener mUserListener;
    User currUser;
    private static final int RESULT_LOAD_IMAGE =112 ;
    Uri selectedImage;

    //firebase storage
    FirebaseStorage storage;
    StorageReference imageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        editTextFname = (EditText) findViewById(R.id.et_fname_profile);
        editTextLname = (EditText) findViewById(R.id.et_lname_profile);
        genderSpinner = (Spinner) findViewById(R.id.sp_gender_profile);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
        btnCancel = (Button) findViewById(R.id.btn_cancelProfile);
        btnSave = (Button) findViewById(R.id.btn_editProfile);

        mAuth = FirebaseAuth.getInstance();

        //storage
        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference();

        if(getIntent().getExtras().getString("User_ID")!=null){
            userID = getIntent().getExtras().getString("User_ID");
        }
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("ChatActivity", user.toString());

        db = FirebaseDatabase.getInstance();
        mUserRef = db.getReference().child("users");

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        mUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User usr = dataSnapshot.getValue(User.class);
                if(usr.getUser_id().equals(userID)){
                    currUser = usr;
                    editTextFname.setText(currUser.getUserfirstname());
                    editTextLname.setText(currUser.getUserlastname());
                    String gender = currUser.getGender();
                    switch(gender) {
                        case "Male":
                            genderSpinner.setSelection(0);
                            break;
                        case "Female":
                            genderSpinner.setSelection(1);
                            break;
                    }

                    Glide.with(ProfileEditActivity.this).using(new FirebaseImageLoader()).load(imageRef.child(currUser.getImage_url())).into(imageViewProfile);
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextFname.getText().toString().equals("")) {
                    Toast.makeText(ProfileEditActivity.this, "First Name field is empty", Toast.LENGTH_LONG).show();
                }else if (editTextLname.getText().toString().equals("")) {
                    Toast.makeText(ProfileEditActivity.this, "Last Name field is empty", Toast.LENGTH_LONG).show();
                }/*else if (editTextGender.getText().toString().equals("")) {
                    Toast.makeText(ProfileEditActivity.this, "Choose a gender", Toast.LENGTH_LONG).show();
                }*//*else if ("profile".equals(imageViewProfile.getTag())) {
                    Toast.makeText(ProfileEditActivity.this, "Select Image", Toast.LENGTH_LONG).show();}*/
                else {
                    currUser.setUserfirstname(editTextFname.getText().toString());
                    currUser.setUserlastname(editTextLname.getText().toString());
                    currUser.setGender(genderSpinner.getSelectedItem().toString());
                    //update image url
                    final String image_id = String.valueOf(UUID.randomUUID());
                    final String path = "images/"+userID+ image_id+".jpg";
                    StorageReference imageRef = storage.getReference(path);
                    Log.d("writeprofile", imageViewProfile.getTag().toString());
                    imageViewProfile.setDrawingCacheEnabled(true);
                    imageViewProfile.buildDrawingCache();
                    Bitmap bitmap = imageViewProfile.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = imageRef.putBytes(data);
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

                    currUser.setImage_id(image_id.toString());
                    currUser.setImage_url(path);

                    mUserRef.child(userID).setValue(currUser);
                    finish();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){ // && null != data) {


            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
                imageViewProfile.setImageBitmap(selectedImage);

                final String image_id = String.valueOf(UUID.randomUUID());
                final String path = "images/"+userID+ image_id+".jpg";
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

                currUser.setImage_id(image_id.toString());
                currUser.setImage_url(path);

                mUserRef.child(userID).setValue(currUser);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //imageViewProfile.setTag("imageReceived");
        }
    }
}
