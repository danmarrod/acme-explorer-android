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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

public class FirebaseStorageActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 0x512;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 0x513;
    private static final int TAKE_PHOTO_CODE = 0x514;

    private Button takePictureButton;
    private ImageView takePictureImage;

    private String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_storage);

        takePictureButton = findViewById(R.id.take_picture_button);
        takePictureImage = findViewById(R.id.take_picture_image);

        takePictureButton.setOnClickListener(l -> {
            takePicture();
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
                                if (task.isSuccessful())
                                    Glide.with(FirebaseStorageActivity.this)
                                            .load(task.getResult())
                                            .placeholder(R.drawable.ic_launcher_background)
                                            .centerCrop()
                                            .into(takePictureImage);
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
}