package com.example.lenovo.xyz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.editable;
import static android.R.attr.fingerprintAuthDrawable;
import static android.R.attr.name;
import static com.example.lenovo.xyz.R.id.myimage;


public class MainActivity extends AppCompatActivity {
    EditText mytext;
    Spinner spinner;
    Button btn;
    String image="anonymus";
    String s;
    ImageButton imagepicker;
    ValueEventListener a;
    public static final int RC_SIGN_IN = 1;
    public static  final int RC_Photo_Picker=2;
    public static  final int Default_Msg_Limit=1000;
    DatabaseReference d;

    FirebaseDatabase f;
    FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String frnd;
    ImageView myimage;
    FirebaseAuth.AuthStateListener authStateListener;
    ListView mylist;
    List<Artist> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
         myimage=(ImageView)findViewById(R.id.myimage);
        mytext = (EditText) findViewById(R.id.mytext);
imagepicker=(ImageButton)findViewById(R.id.imagepicker);
        artistList = new ArrayList<>();
        mylist = (ListView) findViewById(R.id.mylist);
        f = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        d = f.getReference("atrist");
firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference("chat photos").child("chat photos");

        imagepicker.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_Photo_Picker);


        }});
        // Enable Send button when there's text to send
        mytext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    btn.setEnabled(true);
                } else {
                    btn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mytext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Default_Msg_Limit)});

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mytext.getText().toString().trim();
                FirebaseUser as = firebaseAuth.getCurrentUser();
                String frnd=as.getDisplayName();

                String id = d.push().getKey();
                Artist artist = new Artist(id, name, frnd,image);
                d.child(id).setValue(artist);
                mytext.setText("");
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, "Welcome You are logged in", Toast.LENGTH_SHORT).show();
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder().setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,

                                            AuthUI.GOOGLE_PROVIDER

                                    )
                                    .build()RC_SIGN_IN);
                }
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            if(resultCode==RESULT_OK){
                Toast.makeText(MainActivity.this,"Signed in",Toast.LENGTH_SHORT).show();
            }
else if(resultCode==RESULT_CANCELED){
                 Toast.makeText(MainActivity.this,"Not Signed in",Toast.LENGTH_SHORT).show();
                finish();
            }
            else if(requestCode==RESULT_OK&&requestCode==RC_Photo_Picker){

                Uri imageuri=data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageuri);
                    myimage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                StorageReference photoref=storageReference.child(imageuri.getLastPathSegment());
                photoref.putFile(imageuri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
Uri downloadurl= taskSnapshot.getDownloadUrl();
                        FirebaseUser as = firebaseAuth.getCurrentUser();
                        String frnd=as.getDisplayName();
                        Artist artist=new Artist(null,mytext.getText().toString(),frnd,downloadurl.toString());
                        d.push().setValue(artist);
                    }
                });

                }
            }
        }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener!=null){
        firebaseAuth.removeAuthStateListener(authStateListener);}
        detach();
        artistList.clear();
    }

    private void onSignedInInitialize(String username) {
        frnd = username;
    onStart();
    }

    protected void onStart() {
        super.onStart();
        if(a==null){
      ValueEventListener a= d.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                artistList.clear();
                for (DataSnapshot mysnapshot : dataSnapshot.getChildren()) {
                    Artist artist = mysnapshot.getValue(Artist.class);
                    artistList.add(artist);
                }

                artistlist adapter = new artistlist(MainActivity.this, artistList);
                mylist.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}
    }
private  void  onSignedOutCleanup(){
frnd="anonymus";
    artistList.clear();
    detach();

}
private  void detach(){
    if(a!=null){
    d.removeEventListener(a);

}
a=null;
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

