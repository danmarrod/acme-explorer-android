package com.tecmov.acmeexplorer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tecmov.acmeexplorer.entity.Trip;
import com.tecmov.acmeexplorer.entity.User;

import java.io.File;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 0x512;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 0x513;
    private static final int TAKE_PHOTO_CODE = 0x514;

    private Button takePictureButton, profileSaveButton;
    private ImageView takePictureImage;
    private User userProfile;

    private String file;

    private FirebaseAuth mAuth;
    private FirestoreService firestoreService;

    private TextInputLayout profile_name;
    private AutoCompleteTextView profile_name_et;

    private TextInputLayout profile_surname;
    private AutoCompleteTextView profile_surname_et;

    private TextInputLayout profile_address;
    private AutoCompleteTextView profile_address_et;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userProfile = new User();
        mAuth = FirebaseAuth.getInstance();
        firestoreService = firestoreService.getServiceInstance();

        takePictureButton = findViewById(R.id.take_picture_button);
        takePictureImage = findViewById(R.id.take_picture_image);
        profileSaveButton = findViewById(R.id.profile_save_button);
        profile_name = findViewById(R.id.profile_name);
        profile_name_et = findViewById(R.id.profile_name_et);
        profile_surname = findViewById(R.id.profile_surname);
        profile_surname_et = findViewById(R.id.profile_surname_et);
        profile_address = findViewById(R.id.profile_address);
        profile_address_et = findViewById(R.id.profile_address_et);
        progressBar = findViewById(R.id.profile_progress);

        progressBar.setVisibility(View.GONE);
        getUserProfile();

        takePictureButton.setOnClickListener(l -> {
            takePicture();
        });

        profileSaveButton.setOnClickListener(l -> saveProfile());

    }

    private void getUserProfile() {

        firestoreService.getUser(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    Log.i("Acme-Explorer", "Firestore get user: " + user.toString());

                    if (user.getName() != null) {
                        userProfile.setName(user.getName());
                        profile_name_et.setText(userProfile.getName());
                    }
                    if (user.getSurname() != null) {
                        userProfile.setSurname(user.getSurname());
                        profile_surname_et.setText(userProfile.getSurname());
                    }
                    if (user.getAddress() != null) {
                        userProfile.setAddress(user.getAddress());
                        profile_address_et.setText(userProfile.getAddress());
                    }
                    if (user.getPicture() != null) {
                        userProfile.setPicture(user.getPicture());
                        Glide.with(getApplicationContext())
                                .load(user.getPicture())
                                .placeholder(R.drawable.ic_launcher_background)
                                .centerCrop()
                                .into(takePictureImage);
                    }
                }
            }
        });


    }

    private void saveProfile() {

        userProfile.setName(profile_name_et.getText().toString());
        userProfile.setSurname(profile_surname_et.getText().toString());
        userProfile.setAddress(profile_address_et.getText().toString());
        userProfile.setAddress(profile_address_et.getText().toString());

        firestoreService.saveUser(userProfile, task -> {
            if (task.isSuccessful()) {
                DocumentReference documentReference = task.getResult();
                documentReference.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task1.getResult();
                        User user = documentSnapshot.toObject(User.class);
                        Log.i("Acme-Explorer", "User save feedback: " + user.toString());
                    }
                });
                Log.i("Acme-Explorer", "User saved successfully " + task.getResult().getId());
                Toast.makeText(ProfileActivity.this, "User saved successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            } else {
                Log.i("Acme-Explorer", "Error saving user");
                Toast.makeText(ProfileActivity.this, "Error saving user", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Rationalize permissions, request user allowed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Snackbar.make(takePictureButton, R.string.take_picture_camera_rationale, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.take_picture_camera_rationale_ok, click -> {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }

        } else {
            // Allowed permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Rationalize permissions, request user allowed
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Snackbar.make(takePictureButton, R.string.take_write_external_storage_rationale, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.take_picture_camera_rationale_ok, click -> {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST);
                    });
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                }

            } else {
                // Here, we get all permissions allowed

                // Next line to delete privacy policy when storing info outside app directory
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                String dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/AcmeExplorer/";
                File newFile = new File(dir);
                newFile.mkdirs();

                file = dir + Calendar.getInstance().getTimeInMillis() + ".jpg";
                File newFilePicture = new File(file);

                try {
                    newFilePicture.createNewFile();
                } catch (Exception e) {
                }

                Uri outputFileDir = Uri.fromFile(newFilePicture);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileDir);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressBar.setVisibility(View.VISIBLE);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            File filePicture = new File(file);

            FirebaseStorage storage = FirebaseStorage.getInstance();

            // user must be authenticated
            StorageReference storageReference = storage.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child(filePicture.getName());
            // To delete a picture
            // storageReference.delete();
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(filePicture));
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.i("Acme-Explorer", "Firebase storage: completed " + task.getResult().getTotalByteCount());

                        // Android 14 or higher, bug or security problem, image not set property in ImageView. Image url cannot set directly, we need download it previously
                        // We'll use to download
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    userProfile.setPicture(task.getResult().toString());
                                    Glide.with(getApplicationContext())
                                            .load(task.getResult())
                                            .placeholder(R.drawable.ic_launcher_background)
                                            .centerCrop()
                                            .into(takePictureImage);
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Acme-Explorer", "Firebase storage: error");
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(this, R.string.camera_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;
            case WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(this, R.string.write_external_storage_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}