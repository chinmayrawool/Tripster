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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail , editTextFname, editTextLname,editTextGender;
    private EditText editTextPassword, editTextConfirmPassword;
    ImageView profilePicIV;
    private Button btnSignUp, btnCancel;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase db;
    DatabaseReference rootRef;
    private FirebaseStorage storage;
    String userUID;
    private static final int RESULT_LOAD_IMAGE = 112;
    String name;
    Uri selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail =(EditText)findViewById(R.id.et_emailSignUp);
        editTextPassword =(EditText)findViewById(R.id.et_passwordSignUp);
        editTextFname =(EditText)findViewById(R.id.et_fname_signup);
        editTextLname=(EditText)findViewById(R.id.et_lname_signup);
        editTextGender=(EditText)findViewById(R.id.et_gender_signup);
        editTextConfirmPassword =(EditText)findViewById(R.id.et_confirmpasswordSignUp);
        profilePicIV = (ImageView) findViewById(R.id.imageViewProfile);
        btnSignUp =(Button) findViewById(R.id.btn_signUp);
        btnCancel =(Button) findViewById(R.id.btn_cancel);
        storage = FirebaseStorage.getInstance();

        profilePicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        //textViewLogin = (TextView)findViewById(R.id.textViewLogin);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        rootRef = db.getReference();
        /*if(mAuth.getCurrentUser()!=null){
            //Intent to profile activity
            finish();
            Log.d("demo","Intent to profile activity");
            startActivity(new Intent(getApplicationContext(),ChatActivity.class));
        }
*/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    user.updateProfile(profileUpdates);

                    writeUser(user.getUid(),editTextEmail.getText().toString(),  editTextFname.getText().toString(), editTextLname.getText().toString(), editTextGender.getText().toString(), profilePicIV);
                    finish();
                } else {
                    // User is signed out
                    Log.d("demo", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("sign1", editTextEmail.getText().toString());
                Log.d("sign2", editTextPassword.getText().toString());

                if (editTextEmail.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "email field is empty", Toast.LENGTH_LONG).show();
                }
                else if(!isValidEmailAddress(editTextEmail.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "email is not in valid format", Toast.LENGTH_LONG).show();
                }else if (editTextPassword.getText().toString().length()<6) {
                    Toast.makeText(SignUpActivity.this, "Password is too short", Toast.LENGTH_LONG).show();
                }
                else if (editTextPassword.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "Password field is empty", Toast.LENGTH_LONG).show();
                } else if (editTextFname.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "First Name field is empty", Toast.LENGTH_LONG).show();
                }else if (editTextLname.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "Last Name field is empty", Toast.LENGTH_LONG).show();
                }else if (editTextConfirmPassword.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "Confirm Password field is empty", Toast.LENGTH_LONG).show();}
                else if (editTextGender.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "Choose a gender", Toast.LENGTH_LONG).show();
                }else if ("profile".equals(profilePicIV.getTag())) {
                    Toast.makeText(SignUpActivity.this, "Select Image", Toast.LENGTH_LONG).show();}
                else {

                    createAccount(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextFname.getText().toString(), editTextLname.getText().toString(),editTextGender.getText().toString(),profilePicIV);
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void createAccount(String email, String password, String fname, String lname, String gender , ImageView profilepicIV) {
        Log.d("demo", "createAccount:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                        Log.d("demo", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Account already exists", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Account successfully created", Toast.LENGTH_LONG).show();


                        }


                    }


                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //  ImageButton imageReceipt = (ImageButton) findViewById(R.id.imageButtonRecipt);
            profilePicIV = (ImageView) findViewById(R.id.imageViewProfile);
            profilePicIV.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            profilePicIV.setTag("imageReceived");
        }

    }

    private void writeUser(String userid, String email,  String userfirstname, String userlastname, String gender, ImageView profilepicIV) {
        Log.d("write", email);
        Log.d("write", userfirstname);
        Log.d("write", userlastname);
        final String image_id = String.valueOf(UUID.randomUUID());
        final String path = "images/"+userid+ image_id+".jpg";
        StorageReference imageRef = storage.getReference(path);
        Log.d("writeprofile", profilepicIV.getTag().toString());
        profilePicIV.setDrawingCacheEnabled(true);
        profilePicIV.buildDrawingCache();
        Bitmap bitmap = profilePicIV.getDrawingCache();
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
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        User user = new User(userfirstname, userlastname, email,gender,image_id,path,userid);

        mDatabase.child("users").child(userid).setValue(user);



    }
}
