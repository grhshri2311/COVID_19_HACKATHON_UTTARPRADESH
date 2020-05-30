package com.gprs.uttarpradesh;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomQuoraAdapter1 extends ArrayAdapter {

        ArrayList<QuoraHelper> arrayList;
        HashMap<Integer,byte[]> hashMapimage;
    CustomQuoraAdapter2 adapter;
    ProgressBar progressBar;

        private Activity context;
    public CustomQuoraAdapter1(Activity context, ArrayList<QuoraHelper>arrayList) {
        super(context,R.layout.item_post1,arrayList);
        this.context=context;
        this.arrayList=arrayList;
        hashMapimage=new HashMap<>();
    }



    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=null;

            rowView = inflater.inflate(R.layout.item_post1, null, true);
            //this code gets references to objects in the listview_row.xml file
            TextView qtime = rowView.findViewById(R.id.date);
            TextView qname = rowView.findViewById(R.id.people_name);
            TextView qrole = rowView.findViewById(R.id.role);
            TextView qtitle = rowView.findViewById(R.id.post);
            TextView qdesc = rowView.findViewById(R.id.description);
            TextView qmessage = rowView.findViewById(R.id.quoramessage);

            qtime.setText(arrayList.get(position).getTime());
            qname.setText(arrayList.get(position).name);
            qrole.setText(arrayList.get(position).getRole());
            qtitle.setText(arrayList.get(position).getTitle());
            qdesc.setText(arrayList.get(position).getDesc());
            qmessage.setText(arrayList.get(position).getMessage());
        progressBar=rowView.findViewById(R.id.quoraprogress);
        if (!arrayList.get(position).isImage() && !arrayList.get(position).isVedio()) {
            progressBar.setVisibility(View.INVISIBLE);
        }


                if (arrayList.get(position).isImage()) {
                    final ImageView imageView = rowView.findViewById(R.id.quoraimage);
                    if(hashMapimage.get(position)==null) {


                        progressBar.setVisibility(View.VISIBLE);
                    if (FirebaseStorage.getInstance().getReference().child("Quora").child(arrayList.get(position).getUri()) != null) {
                        FirebaseStorage.getInstance().getReference().child("Quora").child(arrayList.get(position).getUri()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                hashMapimage.put(position,bytes);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                imageView.setImageBitmap(bitmap);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }

                }
                    else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(hashMapimage.get(position), 0, hashMapimage.get(position).length);
                        imageView.setImageBitmap(bitmap);
                    }
            }


        if(arrayList.get(position).isVedio()) {
            final VideoView videoView = rowView.findViewById(R.id.quoravideo);
            final String extStorageDirectory = Environment.getExternalStorageDirectory()
                    .toString();
            progressBar = rowView.findViewById(R.id.quoraprogress);
            File f=new File(extStorageDirectory + "/COVI19RELIEF/vedios/" + arrayList.get(position).getUri()  + ".mp4");
            if(!f.exists()) {

                progressBar.setVisibility(View.VISIBLE);


                MediaController mediaController = new MediaController(rowView.getContext());
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);




                if (FirebaseStorage.getInstance().getReference().child("Quora").child(arrayList.get(position).getUri()) != null) {
                    final View finalRowView = rowView;




                    FirebaseStorage.getInstance().getReference().child("Quora").child(arrayList.get(position).getUri()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            try {

                                File folder1 = new File(extStorageDirectory, "COVI19RELIEF");// Name of the folder you want to keep your file in the local storage.
                                folder1.mkdir(); //creating the folder
                                File folder2 = new File(folder1, "vedios");// Name of the folder you want to keep your file in the local storage.
                                folder2.mkdir(); //creating the folder
                                FileOutputStream fileOutputStream=new FileOutputStream(extStorageDirectory + "/COVI19RELIEF/vedios/" + arrayList.get(position).getUri()  + ".mp4");
                                fileOutputStream.write(bytes);
                                start(position,finalRowView,2);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                }

            }

            else{
                if(f.exists()){
                    start(position,rowView,1);
                }

            }
            RelativeLayout relativeLayout = rowView.findViewById(R.id.quoraitem);


            final View finalRowView3 = rowView;
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        videoView.setBackground(finalRowView3.getResources().getDrawable(R.drawable.play));
                    } else {

                        videoView.start();
                        videoView.setBackground(null);
                    }
                }
            });

        }
        else {
            RelativeLayout linearLayout=rowView.findViewById(R.id.quoraitem);
            VideoView videoView = rowView.findViewById(R.id.quoravideo);
            linearLayout.removeView(videoView);
        }

        ArrayList<String> name,answer;

        name=new ArrayList<>();
        answer=new ArrayList<>();

        if(arrayList.get(position).getAnswer()!=null && arrayList.get(position).getUser()!=null){
           name=arrayList.get(position).getUser();
            answer=arrayList.get(position).getAnswer();
        }

        ListView listView = rowView.findViewById(R.id.quoraanswers);
            if(arrayList.get(position).getAnswer()!=null) {

                 adapter = new CustomQuoraAdapter2(context,name,answer);
                ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
                layoutParams.height = 500;
                listView.setLayoutParams(layoutParams);
                listView.setAdapter(adapter);
            }

        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


        return rowView;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void start(final int position, final View rowView, int i) {
        final VideoView videoView = rowView.findViewById(R.id.quoravideo);
        final String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final ProgressBar progressBar = rowView.findViewById(R.id.quoraprogress);
                videoView.setVideoPath(extStorageDirectory + "/COVI19RELIEF/vedios/" + arrayList.get(position).getUri() + ".mp4");
                videoView.start();
                progressBar.setVisibility(View.INVISIBLE);
                videoView.pause();
                videoView.setBackground(rowView.getResources().getDrawable(R.drawable.play));
            }
        }, i*1000);

    }

}
