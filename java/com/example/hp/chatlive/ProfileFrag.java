package com.example.hp.chatlive;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import io.fabric.sdk.android.Fabric;

import static android.app.Activity.RESULT_OK;


public class ProfileFrag extends Fragment {
    private View parentView;
    String imgDecodableString;
    TextView statusText;
    private static int RESULT_LOAD_IMG = 111;
    private static int SELECT_FILE = 1;
    de.hdodenhof.circleimageview.CircleImageView ik;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference imagesRef = storageRef.child("images");
    DatabaseReference userdatabase;
    ImageView statusbrush;
    Context context=getActivity();
    StorageReference mStorageRef;
    byte[] thumb_bytes;
    Animation a,b;
    RelativeLayout relview;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.profilelay3, container, false);
        Fabric.with(getActivity(), new Crashlytics());
        Firebase.setAndroidContext(getActivity());
        TextView txtuser = (TextView) parentView.findViewById(R.id.textView7);
        TextView txtuserno = (TextView) parentView.findViewById(R.id.textView11);
        statusText = (TextView) parentView.findViewById(R.id.textView9);
        statusbrush=(ImageView)parentView.findViewById(R.id.imageView7);
        ImageView myQr=(ImageView)parentView.findViewById(R.id.imageView6);
        ik = (de.hdodenhof.circleimageview.CircleImageView) parentView.findViewById(R.id.profile_image);

        relview=(RelativeLayout)parentView.findViewById(R.id.imageView5);
        a= AnimationUtils.loadAnimation(getActivity(),R.anim.scale_out);
        b= AnimationUtils.loadAnimation(getActivity(),R.anim.scale);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                relview.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        mStorageRef = FirebaseStorage.getInstance().getReference();
        userdatabase= FirebaseDatabase.getInstance().getReference();
        //Typeface face = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Dejavu.ttf");
       // txtuser.setTypeface(face);


        userdatabase.child("users").child(UserDetails.username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status=dataSnapshot.child("status").getValue().toString();
                String img=dataSnapshot.child("profile_img").getValue().toString();
                statusText.setText(status);
                /*RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.profileholder);
                requestOptions.error(R.drawable.profileholder);
                Glide.with(getActivity())
                        .load(img)
                        .apply(requestOptions)
                        .into(ik);*/
                if(!img.equals(""))
                {
                    Picasso.with(getActivity()).load(img).placeholder(R.drawable.profileholder).into(ik);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        txtuser.setText(UserDetails.username);
        txtuserno.setText(UserDetails.phone);
        //checkforpropic();

        //

        final Dialog d = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        d.setContentView(R.layout.customqrd);
        d.setCanceledOnTouchOutside(true);
        d.setCancelable(true);
        //
        final ImageView iqr=(ImageView)d.findViewById(R.id.imageViewqr);
        final LinearLayout lshare=(LinearLayout)d.findViewById(R.id.shareqr);

        statusbrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStatusChangeDialog();
            }
        });
        //
        ik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);*/
               cropper();

            }
        });


        myQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap myBitmap = QRCode.from(UserDetails.username).bitmap();
                /*final Dialog d = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                d.setContentView(R.layout.customqrd);
                d.setCanceledOnTouchOutside(true);
                d.setCancelable(true);
                //
                ImageView iqr=(ImageView)d.findViewById(R.id.imageViewqr);
                LinearLayout lshare=(LinearLayout)d.findViewById(R.id.shareqr); */
                iqr.setImageBitmap(myBitmap);
                d.show();
                //myImage.setImageBitmap(myBitmap);
            }
        });
        lshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap myBitmap = QRCode.from(UserDetails.username).bitmap();
                /*ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "title");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
                Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);


                OutputStream outstream;
                try {
                    outstream = getActivity().getContentResolver().openOutputStream(uri);
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                    outstream.close();
                } catch (Exception e) {
                    System.err.println(e.toString());
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("mimeType", "image/*");*/
                String pathofBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), myBitmap,"title", null);
                Uri bmpUri = Uri.parse(pathofBmp);
                final Intent emailIntent1 = new Intent(     android.content.Intent.ACTION_SEND);
                emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
                emailIntent1.setType("image/png");
                startActivity(Intent.createChooser(emailIntent1, "Share QRcode with"));
            }
        });

        return parentView;
    }

    public void showStatusChangeDialog() {
        relview.startAnimation(a);
        final Dialog dialog=new Dialog(getActivity(),R.style.PauseDialogStatus);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialog.setContentView(R.layout.jabp);
        dialog.setContentView(R.layout.statusdialog);

        String statusformTV=statusText.getText().toString();
        final EditText edt = (EditText) dialog.findViewById(R.id.editText);
        edt.setText(statusformTV);
        Button updatestatusbutton=(Button)dialog.findViewById(R.id.button4);
        updatestatusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status=edt.getText().toString();
                final Dialog ddi = new Dialog(getActivity());
                ddi.requestWindowFeature(Window.FEATURE_NO_TITLE);

                ddi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ddi.setContentView(R.layout.load);
                TextView ty=(TextView)ddi.findViewById(R.id.tyo);
                ty.setText("Updating status");
                ddi.show();
                userdatabase.child("users").child(UserDetails.username).child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getActivity(),"Status updated",Toast.LENGTH_LONG);
                            dialog.dismiss();
                            ddi.dismiss();
                        }
                        else{
                            Toast.makeText(getActivity(),"Status updated",Toast.LENGTH_LONG);
                            dialog.dismiss();
                            ddi.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                    @Override
                    public void run() {
                        relview.setVisibility(View.VISIBLE);
                        relview.startAnimation(b);
                    }
                }, 600);

                // do something
            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);

        //if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data.getData() != null) {
        //    Uri selectedImage = data.getData();
            /*String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();*/

            //saveimg(selectedImage);
           // ik.setImageBitmap(BitmapFactory.decodeFile(picturePath));

 //       }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
               // Toast.makeText(getActivity(),resultUri.toString(),Toast.LENGTH_LONG).show();
                final Dialog ddi = new Dialog(getActivity());
                ddi.requestWindowFeature(Window.FEATURE_NO_TITLE);

                ddi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ddi.setContentView(R.layout.load);
                TextView ty=(TextView)ddi.findViewById(R.id.tyo);
                ty.setText("Changing Profile Pic");
                ddi.show();


                File thumb_file= new File(resultUri.getPath());

                try
                {
                    Bitmap thumb_Bitmap = new Compressor(getActivity())
                            .setMaxHeight(200)
                            .setMaxHeight(200)
                            .compressToBitmap(thumb_file);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    thumb_bytes = baos.toByteArray();
                }catch (Exception e){
                    e.printStackTrace();
                }


                StorageReference profile_imageReference= mStorageRef.child("profile_images").child(UserDetails.idd+".jpg");
                final StorageReference thumb_imageReference= mStorageRef.child("profile_images").child("thumb_images").child(UserDetails.idd+".jpg");

                profile_imageReference.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            final String downloaduri= task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_imageReference.putBytes(thumb_bytes);

                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbtask) {
                                    if(thumbtask.isSuccessful())
                                    {
                                        String downloadThumbUri= thumbtask.getResult().getDownloadUrl().toString();
                                        Map updateHashmap=new HashMap();
                                        updateHashmap.put("profile_img",downloaduri);
                                        updateHashmap.put("thumb_nail",downloadThumbUri);

                                        userdatabase.child("users").child(UserDetails.username).updateChildren(updateHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(getActivity(),"All Set",Toast.LENGTH_LONG).show();
                                                    ddi.dismiss();
                                                }
                                                else{
                                                    Toast.makeText(getActivity(),"uploading image failed",Toast.LENGTH_LONG).show();
                                                    ddi.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }
                            });


                        }
                        else
                        {
                            Toast.makeText(getActivity(),"Error:"+task.getException(),Toast.LENGTH_LONG).show();
                            ddi.dismiss();
                        }
                    }
                });


                //saveimg(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        /*else
        {
            Toast.makeText(getActivity(),"Can't fetch",Toast.LENGTH_LONG).show();
        }*/


    }

    void saveimg(Uri selectedImage)
    {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Setting profile pic");
        pd.show();
        // File or Blob
        Uri file = selectedImage;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), file);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 2, bytes);
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Myprofpic", null);
            Uri uril = Uri.parse(path);
// Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

// Upload file and metadata to the path 'images/mountains.jpg'
            UploadTask uploadTask = storageRef.child("images/" + uril.getLastPathSegment()).putFile(uril, metadata);

// Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    pd.setMessage("Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.setMessage("Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    pd.setMessage("upload failed");
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    pd.setMessage("upload successful");
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
               /* ik.setImageURI(null);
                ik.setImageURI(downloadUrl);*/
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.profileholder);
                    requestOptions.error(R.drawable.profileholder);
                    Glide.with(getContext())
                            .load(downloadUrl)
                            .apply(requestOptions)
                            .into(ik);
                   /* Glide
                            .with(getContext())
                            .load(downloadUrl) // the uri you got from Firebase
                            .placeholder(R.drawable.penguin)
                            .dontAnimate()
                            .centerCrop()
                            .into(ik); //Your imageView variable*/
                    savetoprofilebase(downloadUrl);
                    Toast.makeText(getActivity(), "Setting new profile pic", Toast.LENGTH_LONG).show();
                }
            });
            pd.dismiss();
        }catch (Exception e)
        {
            e.printStackTrace();
            pd.dismiss();
        }
    }

    public void savetoprofilebase(final Uri downloadUrl)
    {
        String urll = "https://fir-g-a2c81.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, urll, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://fir-g-a2c81.firebaseio.com/users");
                Firebase reference2 = new Firebase("https://fir-g-a2c81.firebaseio.com/proimgdetail");
                String user=UserDetails.username;
                UserDetails.imguri=downloadUrl.toString();
                reference.child(user).child("profile_img").setValue(UserDetails.imguri);
                reference2.child(user).child("img").setValue(UserDetails.imguri);
                //Toast.makeText(getActivity(),"Done haha",Toast.LENGTH_LONG).show();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }
    public void checkforpropic()
    {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Checking details");
        pd.show();
        String url2 = "https://fir-g-a2c81.firebaseio.com/users.json";
        StringRequest request = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                    try {
                        JSONObject obj = new JSONObject(s);

                        String user=UserDetails.username;
                        UserDetails.imguri= obj.getJSONObject(user).getString("profile_img");
                        if(UserDetails.imguri.equals("")){
                            int a=0;
                        }
                        else {
                            Uri u=Uri.parse(UserDetails.imguri);
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(R.drawable.profileholder);
                            requestOptions.error(R.drawable.profileholder);
                            Glide.with(getContext())
                                    .load(u)
                                    .apply(requestOptions)
                                    .into(ik);
                            /*Glide
                                    .with(getContext())
                                    .load(u)
                                    .placeholder(R.drawable.profileholder)
                                    .dontAnimate()// the uri you got from Firebase
                                    .centerCrop()
                                    .into(ik); //Your imageView variable*/
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
        pd.dismiss();
    }
    public void cropper()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(getContext(),this);
    }
}
