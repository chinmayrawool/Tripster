package com.mad.tripster;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class AddTripActivity extends AppCompatActivity {
    String uid;
    EditText et_title,et_location;
    Button createTripBtn, cancelBtn;
    ImageView coverPicIV;
    private static final int RESULT_LOAD_COVER = 110;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mTripsDbReference;
    Uri selectedImage;
    FirebaseStorage storage;
    static int count = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_trip_layout);
        et_title = (EditText) findViewById(R.id.et_title_trip);
        et_location = (EditText) findViewById(R.id.et_location_trip);
        createTripBtn = (Button) findViewById(R.id.btn_addTrip);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);

        coverPicIV = (ImageView) findViewById(R.id.imageViewCover1);
        mDatabase = FirebaseDatabase.getInstance();
        mTripsDbReference = mDatabase.getReference().child("trips");
        storage = FirebaseStorage.getInstance();

        if(getIntent().getExtras()!=null){
            uid = getIntent().getExtras().getString("User");
        }

        coverPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //i.setType("images/*");

                startActivityForResult(i, RESULT_LOAD_COVER);
            }
        });

        createTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_title.getText().toString().equals("")) {
                    Toast.makeText(AddTripActivity.this, "Title field is empty", Toast.LENGTH_LONG).show();
                } else if (et_location.getText().toString().equals("")) {
                    Toast.makeText(AddTripActivity.this, "Location field is empty", Toast.LENGTH_LONG).show();
                }else if ("profile".equals(coverPicIV.getTag())) {
                    Toast.makeText(AddTripActivity.this, "Select Image", Toast.LENGTH_LONG).show();}
                else {
                    Log.d("demo","Adding New trip");
                    final String image_id = String.valueOf(UUID.randomUUID());
                    final String path = "images/"+uid+ image_id+".jpg";
                    StorageReference imageRef = storage.getReference(path);
                    Log.d("writeprofile", coverPicIV.getTag().toString());
                    coverPicIV.setDrawingCacheEnabled(true);
                    coverPicIV.buildDrawingCache();
                    Bitmap bitmap = coverPicIV.getDrawingCache();
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
                    //Trip(String title, String location, String image_id, String image_url, String created_by, String trip_id)
                    String newTripId = uid+"@@@"+String.valueOf(UUID.randomUUID());
                    Trip trip = new Trip(et_title.getText().toString(),et_location.getText().toString(),image_id,path,uid,newTripId);
                    mTripsDbReference.child(newTripId).setValue(trip);
                    count++;
                    finish();
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_COVER && resultCode == RESULT_OK){// && null != data) {
           try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                coverPicIV = (ImageView) findViewById(R.id.imageViewCover1);
                coverPicIV.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            coverPicIV.setTag("imageReceived");
        }

    }
}
