package com.gprs.uttarpradesh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class postquestion extends Fragment {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String url;
    Uri vedioUri,imageUri,filepath;
    View root;

    EditText title,desc,message;
    boolean nil=false,img=false,vid=false;
    boolean done=true;



    private Button btnCapturePicture, btnRecordVideo,post;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_postquestion, container, false);
        btnCapturePicture = root.findViewById(R.id.btnCapturePicture);
        title=root.findViewById(R.id.title);
        desc=root.findViewById(R.id.desc);
        message=root.findViewById(R.id.message);
        post=root.findViewById(R.id.post);
        btnRecordVideo = root.findViewById(R.id.btnRecordVideo);

        pref = root.getContext().getSharedPreferences("user", 0); // 0 - for private mode
        editor = pref.edit();
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().isEmpty() ||desc.getText().toString().isEmpty() || message.getText().toString().isEmpty() ){
                    Toast.makeText(root.getContext(),"Please fill all the fields",Toast.LENGTH_LONG).show();
                }
                else{
                    post.setEnabled(false);
                    post.setBackgroundColor(Color.parseColor("#D3D0D0"));
                    if(imageUri==null && vedioUri==null)
                    {
                        nil=true;
                        FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("user","")).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserRegistrationHelper status=dataSnapshot.getValue(UserRegistrationHelper.class);
                                if(status!=null){
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String currentDateTime = dateFormat.format(new Date()); // Find todays date
                                    QuoraHelper quoraHelper=new QuoraHelper(title.getText().toString(),desc.getText().toString(),message.getText().toString(),status.getRole(),status.getPhone(),status.getFname(),currentDateTime,nil,img,vid,null,null,null);
                                    FirebaseDatabase.getInstance().getReference().child("Quora").child(currentDateTime).setValue(quoraHelper);
                                    post.setEnabled(true);
                                    post.setBackgroundColor(Color.parseColor("#000000"));
                                    Toast.makeText(root.getContext(), "Posted Successfully ! ", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                post.setEnabled(true);
                                post.setBackgroundColor(Color.parseColor("#000000"));
                            }
                        });

                    }
                    if(imageUri!=null){
                        nil=true;
                        img=true;
                        filepath=imageUri;
                        uploadImage();
                    }
                    else if(vedioUri!=null){
                        nil=true;
                        vid=true;
                        filepath=vedioUri;
                        uploadImage();
                    }

                }
            }
        });

        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

        /**
         * Record video button click event
         */
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // record video
                recordVideo();
            }
        });


        return root;
    }
    private boolean isDeviceSupportCamera() {
        // this device has a camera
        // no camera on this device
        return root.getContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }
    private void captureImage() {
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void recordVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent , CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                imageUri=data.getData();
                Toast.makeText(root.getContext(),"Image Selected",Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(root.getContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(root.getContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                vedioUri=data.getData();
                Toast.makeText(root.getContext(),"Vedio Selected",Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(root.getContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(root.getContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void uploadImage() {
        if (filepath != null) {
            Long lon=new Random().nextLong();
            if(lon<0)
                lon=lon*-1;
            url=pref.getString("user","")+lon;
            FirebaseStorage.getInstance().getReference().child("Quora").child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    done=false;                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    done=false;
                    final ProgressDialog progressDialog = new ProgressDialog(root.getContext());
                    progressDialog.setTitle("Uploading");
                    progressDialog.show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String currentDateTime = dateFormat.format(new Date()); // Find todays date
                    final StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("Quora").child(url);
                    riversRef.putFile(filepath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //if the upload is successfull
                                    //hiding the progress dialog
                                    progressDialog.dismiss();
                                    //and displaying a success toast
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("user","")).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserRegistrationHelper status=dataSnapshot.getValue(UserRegistrationHelper.class);
                                            if(status!=null){

                                                QuoraHelper quoraHelper=new QuoraHelper(title.getText().toString(),desc.getText().toString(),message.getText().toString(),status.getRole(),status.getPhone(),status.getFname(),currentDateTime,nil,img,vid,null,url,null);
                                                FirebaseDatabase.getInstance().getReference().child("Quora").child(currentDateTime).setValue(quoraHelper);
                                                Toast.makeText(root.getContext(), "Posted Successfully ! ", Toast.LENGTH_LONG).show();
                                                post.setEnabled(true);
                                                post.setBackgroundColor(Color.parseColor("#000000"));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            post.setEnabled(true);
                                            post.setBackgroundColor(Color.parseColor("#000000"));
                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    //if the upload is not successfull
                                    //hiding the progress dialog
                                    progressDialog.dismiss();
                                    post.setEnabled(true);
                                    post.setBackgroundColor(Color.parseColor("#000000"));
                                    //and displaying error message
                                    Toast.makeText(root.getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    //calculating progress percentage
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                    //displaying percentage in progress dialog
                                    progressDialog.setMessage("Uploading " + ((int) progress) + "%...");
                                }
                            });
                }
            });
            //displaying a progress dialog while upload is going on

        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }




}



