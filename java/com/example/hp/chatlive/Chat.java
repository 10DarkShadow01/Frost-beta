package com.example.hp.chatlive;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.utilities.Base64;
import com.giphy.sdk.core.network.api.GPHApi;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.onesignal.OneSignal;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.fabric.sdk.android.Fabric;
import xyz.klinker.giphy.Giphy;

import static android.widget.Toast.LENGTH_LONG;
import static java.security.AccessController.getContext;


public class Chat extends BaseActivity  {
    private static int RESULT_LOAD_IMG = 111;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference imagesRef = storageRef.child("images");
    DatabaseReference mrefdatabase;
    LinearLayout layout,linearlayoutv;
    RelativeLayout layout_2,vdi,ro;
    ImageView sendButton,cB,econ;
   // EditText messageArea;
    String TAG="keyy";
    EmojiconEditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    View rootView;
    static int vri=0;
    EmojIconActions emojIcon;
    String url5 = "https://fir-g-a2c81.firebaseio.com/proimgdetail.json";
    MediaPlayer insond,outsond;
    de.hdodenhof.circleimageview.CircleImageView uhdd;

    private static final String APP_KEY = "c100e144-5c0f-4faf-b1f3-a5d1850b56e5";
    private static final String APP_SECRET = "cnXyvTlvQU2dnGYicZ5uXw==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        GPHApi client = new GPHApiClient("zbL01LibkeeFrntaIvCHHgCxqjN7MDpv");
        rootView = findViewById(R.id.root_view);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final android.content.Context context = this.getApplicationContext();
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        vdi = (RelativeLayout)findViewById(R.id.vdi);
        ro=(RelativeLayout)findViewById(R.id.ro);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        cB = (ImageView)findViewById(R.id.cButton);
        econ = (ImageView)findViewById(R.id.icemo);
        messageArea = (EmojiconEditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        Fabric.with(this, new Crashlytics());
        Firebase.setAndroidContext(this);
        OneSignal.startInit(this).init();

        mrefdatabase= FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbary);
        TextView tvv=(TextView)toolbar.findViewById(R.id.toolbar_title);
        final TextView lastseentext=(TextView)toolbar.findViewById(R.id.lastseen_text);
        uhdd = (de.hdodenhof.circleimageview.CircleImageView) toolbar.findViewById(R.id.imageViewhhh);
        ImageView iw=(ImageView)toolbar.findViewById(R.id.backimg);
        ImageView icall=(ImageView)toolbar.findViewById(R.id.imageView12);
        ImageView ivideoc=(ImageView)toolbar.findViewById(R.id.imageView11);
        tvv.setText(UserDetails.chatWith);
//

        mrefdatabase.child("users").child(UserDetails.chatWith).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                String online=dataSnapshot.child("online").getValue().toString();
                String proimg=dataSnapshot.child("thumb_nail").getValue().toString();
                if(online.equals("true"))
                {
                    lastseentext.setText("Online");
                }
                else {
                    GetTimeAgo gettimeago=new GetTimeAgo();
                    long lastTime=Long.parseLong(online);
                    String lastSeen=gettimeago.getTimeAgo(lastTime,getApplication());
                    lastseentext.setText(lastSeen);
                }
                if(!proimg.equals(""))
                {
                    Picasso.with(Chat.this).load(proimg).placeholder(R.drawable.profileholder).into(uhdd);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        emojIcon = new EmojIconActions(this, rootView, messageArea, econ,"#469fbc","#ffffff","#FFEEEEEE");
        emojIcon.setUseSystemEmoji(false);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard closed");
            }
        });

        //Below code is Important if toolbar profile pic is not set enable this code below

       /* StringRequest request3 = new StringRequest(Request.Method.GET, url5, new Response.Listener<String>(){
            @Override
            public void onResponse(String r) {
                doOnSuccessimgg(r);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyErrorr) {
                System.out.println("" + volleyErrorr);
            }
        });
        RequestQueue rQueue3 = Volley.newRequestQueue(this);
        rQueue3.add(request3);*/

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        reference1 = new Firebase("https://fir-g-a2c81.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://fir-g-a2c81.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);
        
        insond=MediaPlayer.create(this,R.raw.outsound);
        outsond=MediaPlayer.create(this,R.raw.arpeggio);

        iw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ity=new Intent(Chat.this,ResideV.class);
                startActivity(ity);
            }
        });


        icall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call call = getSinchServiceInterface().callUser(UserDetails.chatWith);
                String callId = call.getCallId();
                Intent callScreen = new Intent(Chat.this, AudioCallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            }
        });
        ivideoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call call = getSinchServiceInterface().callUserVideo(UserDetails.chatWith);
                String callId = call.getCallId();

                Intent callScreen = new Intent(Chat.this, CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            }
        });



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm aa");
                String datetime = dateformat.format(c.getTime());
                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("date",datetime);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                    sendnoti(messageText);
                }
            }
        });

        //

        cB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vri=0;
                String messageText = messageArea.getText().toString();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        /*vdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vri=1;
                String messageText = messageArea.getText().toString();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });*/
        vdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vri=1;
                String messageText = messageArea.getText().toString();
                new Giphy.Builder(Chat.this, "zbL01LibkeeFrntaIvCHHgCxqjN7MDpv")    // Giphy's BETA key
                        .maxFileSize(2 * 1024 * 1024)               // 5 mb
                        .start();
            }
        });
        //


        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String time=map.get("date").toString();
                String userName = map.get("user").toString();

                if(userName.equals(UserDetails.username)){
                    //addMessageBox("You:-\n" + message, 1);
                    addMessageBox(message, 1,time);
                    //addMessageBox(message, 1);
                    //sendnoti(message);
                }
                else{
                    //addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
                    addMessageBox(message, 2,time);
                   // addMessageBox(message, 2);

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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        ro.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                ro.getWindowVisibleDisplayFrame(r);
                int screenHeight = ro.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d("Test", "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                   // Toast.makeText(context,"keypad opened",Toast.LENGTH_LONG).show();
                    vdi.setVisibility(View.VISIBLE);
                }
                else {
                    // keyboard is closed
                    vdi.setVisibility(View.GONE);
                }
            }
        });

    }

    public void addMessageBox(String message, int type,String time){
      //chage this to get orriginal // TextView textView = new TextView(Chat.this);
        EmojiconTextView textView= new EmojiconTextView(Chat.this);
        textView.setUseSystemDefault(false);
        textView.setEmojiconSize(34);
        EmojiconTextView textViewtime= new EmojiconTextView(Chat.this);
       /* textView.setUseSystemDefault(false);
        textView.setEmojiconSize(34);*/
       textViewtime.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        //TextView textView2 = new TextView(Chat.this);
        //textView.setText(message);
        ImageView iy=new ImageView(Chat.this);
        VideoView vvv=new VideoView(Chat.this);
        MediaController mediaController = new MediaController(Chat.this);

       // textView2.setText(message);
        if(message.startsWith("https://firebasestorage.googleapis.com/v0/b/fir-g-a2c81.appspot.com/o/images"))
        {
            Uri u=Uri.parse(message);
            linearlayoutv = new LinearLayout(this);
            linearlayoutv.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 1.0f;

            if(type == 1) {
                textViewtime.setText(time);

                lp2.gravity = Gravity.RIGHT;
                //textViewtime.setLayoutParams(lp2);
               // iy.setBackgroundResource(R.drawable.bubblle_inf);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.placy);
                requestOptions.error(R.drawable.placy);
                Glide.with(getApplicationContext())
                        .load(u)
                        .apply(requestOptions)
                        .into(iy);
                linearlayoutv.addView(iy);
                linearlayoutv.addView(textViewtime);
                linearlayoutv.setBackgroundResource(R.drawable.bubblle_inf);
                /*Glide
                        .with(getApplicationContext())
                        .load(u)
                        .placeholder(R.drawable.penguin)
                        //.centerCrop()// the uri you got from Firebase           //.centerCrop()
                        .into(iy);*/
                insond.start();

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
            else{
                textViewtime.setText(time);
                lp2.gravity = Gravity.LEFT;
               // textViewtime.setLayoutParams(lp2);
              //  iy.setBackgroundResource(R.drawable.bubblle_outf);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.placy );
                requestOptions.error(R.drawable.placy);
                Glide.with(getApplicationContext())
                        .load(u)
                        .apply(requestOptions)
                        .into(iy);
                linearlayoutv.addView(iy);
                linearlayoutv.addView(textViewtime);
                linearlayoutv.setBackgroundResource(R.drawable.bubblle_outf);
                /*Glide
                        .with(getApplicationContext())
                        .load(u)
                        .placeholder(R.drawable.penguin)
                        //.centerCrop()// the uri you got from Firebase           //.centerCrop()
                        .into(iy);*/
                outsond.start();

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
            //iy.setLayoutParams(lp2);
            linearlayoutv.setLayoutParams(lp2);
            //linearlayoutv.setBackgroundResource(R.drawable.bubblle_inf);
            //layout.addView(iy);
            layout.addView(linearlayoutv);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
        else if(message.startsWith("https://firebasestorage.googleapis.com/v0/b/fir-g-a2c81.appspot.com/o/video"))
        {
            Uri u=Uri.parse(message);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 1.0f;

            if(type == 1) {
                lp2.gravity = Gravity.RIGHT;
                vvv.setBackgroundResource(R.drawable.bubblle_inf);
                vvv.setVideoURI(u);
                mediaController.setAnchorView(vvv);
                vvv.setMediaController(mediaController);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
            else{
                lp2.gravity = Gravity.LEFT;
                vvv.setBackgroundResource(R.drawable.bubblle_outf);
                vvv.setVideoURI(u);
                mediaController.setAnchorView(vvv);
                vvv.setMediaController(mediaController);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
            vvv.setLayoutParams(lp2);
            layout.addView(vvv);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
            vvv.start();
        }
        else {
            linearlayoutv = new LinearLayout(this);
            linearlayoutv.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 1.0f;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight = 1.0f;

            if(type == 1) {
                //textView.setText("You:-\n" + message);
               // textView.setText("\n"+message+"\n");
                lp.gravity = Gravity.RIGHT;
                textView.setText(message);
                textViewtime.setText(time);
                lp2.gravity = Gravity.RIGHT;
                linearlayoutv.addView(textView);
                linearlayoutv.addView(textViewtime);
               // change it back//textView.setBackgroundResource(R.drawable.bubblle_inf);
                linearlayoutv.setBackgroundResource(R.drawable.bubblle_inf);
                insond.start();

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
            else{
                //textView.setText(UserDetails.chatWith + ":-\n" +message);
                //textView.setText("\n"+message+"\n");
                lp.gravity = Gravity.RIGHT;
                textView.setText(message);
                textViewtime.setText(time);
                lp2.gravity = Gravity.LEFT;
                linearlayoutv.addView(textView);
                linearlayoutv.addView(textViewtime);
                // change it back textView.setBackgroundResource(R.drawable.bubblle_outf);
                linearlayoutv.setBackgroundResource(R.drawable.bubblle_outf);
                outsond.start();

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
            //textView.setLayoutParams(lp2);
            linearlayoutv.setLayoutParams(lp2);
            //layout.addView(textView);
            layout.addView(linearlayoutv);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });

        }
       /* LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubblle_in);
            insond.start();

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
        else{
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubblle_out);
            outsond.start();

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });*/
    }



    ////////////////


    public void doOnSuccessimgg(String r){
        try {
            JSONObject obj2 = new JSONObject(r);
            Iterator i = obj2.keys();
            //Toast.makeText(this,"got access",LENGTH_LONG).show();
            String userimg=obj2.getJSONObject(UserDetails.chatWith).getString("img");
            if(!userimg.equals(""))
            {
                /*Glide
                        .with(getApplicationContext())
                        .load(userimg) // the uri you got from Firebase
                        .centerCrop()
                        .into(uhdd);//Your imageView variable*/ //Replaced
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.profileholder);
                requestOptions.error(R.drawable.profileholder);
                Glide.with(getApplicationContext())
                        .load(userimg)
                        .apply(requestOptions)
                        .into(uhdd);
                /*Glide
                        .with(getApplicationContext())
                        .load(userimg)
                        .placeholder(R.drawable.profileholder)
                        .dontAnimate()// the uri you got from Firebase
                        .centerCrop()
                        .into(uhdd); //Your imageView variable*/
            }

        } catch (JSONException e) {
            String w=e.toString();
           // Toast.makeText(this,w,LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }
    /////////////////


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data.getData() != null) {
            Uri selectedImage = data.getData();
            saveimg(selectedImage);
        }
        //new code added here
        if (requestCode == Giphy.REQUEST_GIPHY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri gif = data.getData();
                //Toast.makeText(Chat.this,gif.toString()+" This is the problem dumdum!!",LENGTH_LONG).show();
                saveimg(gif);
                // do something with the uri.
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }

    }
    void saveimg(Uri selectedImage)
    {
        final ProgressDialog pd = new ProgressDialog(Chat.this);
        pd.setMessage("Uploading");
        pd.show();
        if(vri==0) {
            // File or Blob
            Uri file = selectedImage;
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();


            UploadTask uploadTask = storageRef.child("images/" + file.getLastPathSegment()).putFile(file, metadata);

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

                    postimg(downloadUrl);
                }
            });
            pd.dismiss();
        }
        else if(vri==1)
        {

            // File or Blob
            Uri file = selectedImage;
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/gif")
                    .build();


            UploadTask uploadTask = storageRef.child("images/" + file.getLastPathSegment()).putFile(file, metadata);

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
                   // Toast.makeText(Chat.this,downloadUrl.toString()+" That was sent though!!",LENGTH_LONG).show();
                    postimg(downloadUrl);
                }
            });
            pd.dismiss();
        }
        else
        {
            // File or Blob
            Uri file = selectedImage;
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("video/mp4")
                    .build();


            UploadTask uploadTask = storageRef.child("video/" + file.getLastPathSegment()).putFile(file, metadata);

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
                    postimg(downloadUrl);
                }
            });
            pd.dismiss();
        }
    }

    public void postimg(Uri downloadUrl)
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm aa");
        String datetime = dateformat.format(c.getTime());
        String downloadu=downloadUrl.toString();
        if(!downloadu.equals("")){
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", downloadu);
            map.put("date",datetime);
            map.put("user", UserDetails.username);
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            //reference4.push().setValue(map);
            messageArea.setText("");
            sendnoti(downloadu);
        }
    }

////////////////

    public void sendnoti(final String message)
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int sdk_int= Build.VERSION.SDK_INT;
                if(sdk_int>8)
                {
                    StrictMode.ThreadPolicy threadPolicy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(threadPolicy);
                    String send_email=UserDetails.chatWith;
                    try
                    {
                        String jsonresponse,strJsonBody,name=UserDetails.username+": ";
                        URL url=new URL("https://onesignal.com/api/v1/notifications/");
                        HttpURLConnection con=(HttpURLConnection)url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);
                        con.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization","Basic ODk3YWM3MWMtNDBlNi00YmRiLWFiYjctOGRhMWEwNTM1MjY0");
                        con.setRequestMethod("POST");
                        if(message.startsWith("https://firebasestorage.googleapis.com/v0/b/fir-g-a2c81.appspot.com/")) {
                                strJsonBody = "{"
                                    + "\"app_id\": \"66e8f56a-0782-44d2-8b08-fad1f23acd61\","
                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_id\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"
                                    + "\"data\": {\"foo\": \"bar\"},"
                                    + "\"contents\": {\"en\": \""+name+"\"Image\"}"
                                    + "}";
                        }
                        else
                        {
                                strJsonBody = "{"
                                    + "\"app_id\": \"66e8f56a-0782-44d2-8b08-fad1f23acd61\","
                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_id\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"
                                    + "\"data\": {\"foo\": \"bar\"},"
                                    + "\"contents\": {\"en\": \""+ name + message + "\"}"
                                    + "}";
                        }
                        byte[] sendBytes=strJsonBody.getBytes();
                        con.setFixedLengthStreamingMode(sendBytes.length);
                        OutputStream outputStream=con.getOutputStream();
                        outputStream.write(sendBytes);
                        int httpResponse =con.getResponseCode();
                        if(httpResponse >=HttpURLConnection.HTTP_OK && httpResponse <HttpURLConnection.HTTP_BAD_REQUEST)
                        {
                            Scanner sc= new Scanner(con.getInputStream(), "UTF-8");
                            jsonresponse =sc.useDelimiter("\\A").hasNext()?sc.next():"";
                            sc.close();
                        }
                        else
                        {
                            Scanner sc= new Scanner(con.getErrorStream(), "UTF-8");
                            jsonresponse =sc.useDelimiter("\\A").hasNext()?sc.next():"";
                            sc.close();
                        }
                    }
                    catch (Throwable t)
                    {
                        t.printStackTrace();
                    }
                }
            }
        });
    }
    //////////////////

}