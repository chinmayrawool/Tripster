package com.mad.tripster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button btnSignIn,btnSignUp;
    SignInButton signin;
    GoogleApiClient googleApiClient;
    private TextView textViewSignUp;
    private GoogleSignInAccount account;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private String image_id = String.valueOf(UUID.randomUUID());
    private  String login = "app";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String EXTRA_USER_ID = "USER_ID";
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btnSignIn =(Button) findViewById(R.id.btn_signIn);
        btnSignUp =(Button) findViewById(R.id.btn_signUp);
        //textViewSignUp = (TextView)findViewById(R.id.textViewSignUp);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null){
            //Intent to profile activity
            finish();
            Log.d("demo","Intent to profile activity");
            startActivity(new Intent(getApplicationContext(),ContentActivity.class));
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("demo", "onAuthStateChanged:signed_in:" + user.getUid());
                    if(login.equals("google"))
                    {
                        DatabaseReference Userref = FirebaseDatabase.getInstance().getReference().getRoot().child("users");
                        Userref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("kkk", "in here ");
                                int k = 0;
                                long n = dataSnapshot.getChildrenCount();
                                Log.d("n is ", "value : "+n);
                                User user1 = dataSnapshot.getValue(User.class);
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.getValue(User.class).getUser_id().equals(user.getUid())) {


                                        break;
                                    }
                                    Log.d("new","k is :"+k);
                                    k++;
                                }
                                Log.d("new","k is :"+k);
                                if(k == n)
                                {
                                    new LoadProfileImage().execute(account.getPhotoUrl().toString());

                                    final String path = "images/" + user.getUid() + image_id;


                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                                    User usertobe = new User(account.getDisplayName(), account.getFamilyName(), account.getEmail(), "", image_id, path, user.getUid());

                                    mDatabase.child("users").child(user.getUid()).setValue(usertobe);



                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });

                    }
                    Intent i = new Intent(LoginActivity.this, ContentActivity.class);
                    //i.putExtra(EXTRA_USER_ID, user1.getUser_id());
                    i.putExtra(EXTRA_USER_ID, user.getUid());
                    startActivity(i);

                } else {
                    // User is signed out
                    Log.d("demo", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("585685577686-h2b0c980clgeppmkb792nrbe9p602daf.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        signin = (SignInButton) findViewById(R.id.btnGoogleSignIn);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextEmail =(EditText)findViewById(R.id.et_emailLogin);
                editTextPassword =(EditText)findViewById(R.id.et_passwordLogin);

                if (editTextEmail.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "email field is empty", Toast.LENGTH_LONG).show();
                } else if (!isValidEmailAddress(editTextEmail.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "email is not in valid format", Toast.LENGTH_LONG).show();
                } else if (editTextPassword.getText().toString().length() < 6) {
                    Toast.makeText(LoginActivity.this, "Password is too short", Toast.LENGTH_LONG).show();
                } else if (editTextPassword.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Password field is empty", Toast.LENGTH_LONG).show();
                } else {
                    signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());

                }

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mAuth.signOut();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //handleSignInResult(result);
            if (result.isSuccess()) {
                account = result.getSignInAccount();
                login = "google";
                Log.d("demo", "firebaseAuthWithGoogle:" + account.getId());
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("demo", "signInWithCredential:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w("demo", "signInWithCredential", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                // ...
                            }
                        });
            } else {

                Log.d("demo","Un Successful google signin");


            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("demo","Connection Failed: "+connectionResult);
    }
    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void signIn(String email, String password) {
        Log.d("main", "signIn:" + email);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("main", "signInWithEmail:onComplete:" + task.isSuccessful());
                        login = "app";
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("main", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_f,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]

                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            //camera=mIcon11;
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            //    bmImage.setImageBitmap(result);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            StorageReference imageRef = storage.getReference("images/" + user.getUid() + image_id);
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
        }
    }
}
