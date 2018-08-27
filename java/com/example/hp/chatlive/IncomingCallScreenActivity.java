package com.example.hp.chatlive;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class IncomingCallScreenActivity extends BaseActivity {

    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    private AudioPlayer mAudioPlayer;
    Vibrator vibe;
    static int answer_flag;
    RelativeLayout rl;
    de.hdodenhof.circleimageview.CircleImageView imgv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vincoming2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        Fabric.with(this, new Crashlytics());
        Animation shake;
        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        final ImageView answer = (ImageView) findViewById(R.id.answerButton);
        //answer.setOnClickListener(mClickListener);
        final ImageView decline = (ImageView) findViewById(R.id.declineButton);
        rl=(RelativeLayout)findViewById(R.id.rl);
        //decline.setOnClickListener(mClickListener);
        answer.startAnimation(shake);
        decline.startAnimation(shake);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        imgv=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.circleImageView);
        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        answer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    answer.clearAnimation();
                    answer_flag=1;
                    vibe.vibrate(100);
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        });
        decline.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    decline.clearAnimation();
                    answer_flag=0;
                    vibe.vibrate(100);
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        });
        rl.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        // v.setBackgroundDrawable(enterShape);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        // v.setBackgroundDrawable(normalShape);
                        break;
                    case DragEvent.ACTION_DROP:
                        // Dropped, reassign View to ViewGroup
                        View view = (View) event.getLocalState();
                        ViewGroup owner = (ViewGroup) view.getParent();
                        owner.removeView(view);
                        RelativeLayout container = (RelativeLayout) v;
                        container.addView(view);
                        view.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        // v.setBackgroundDrawable(normalShape);
                        if(answer_flag==1) {
                            new Thread(new Runnable() {
                                public void run() {
                                    try
                                    {
                                        Thread.sleep(1500);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //vocalize
                                                answerClicked();
                                            }
                                        });
                                    }catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();
                        }
                        else
                        {
                            declineClicked();
                        }
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
            ImageView iUser = (ImageView) findViewById(R.id.uimg);
            remoteUser.setText(call.getRemoteUserId());
            getuimguri(call.getRemoteUserId());
            checkforpropic(call.getRemoteUserId());

        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();
            Intent intent = new Intent(this, CallScreenActivity.class);
            intent.putExtra(SinchService.CALL_ID, mCallId);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            // Display some kind of icon showing it's a video call
        }
        @Override
        public void onVideoTrackPaused(Call call) {
            // Display some kind of icon showing it's a video call
        }
        @Override
        public void onVideoTrackResumed(Call call) {
            // Display some kind of icon showing it's a video call
        }
    }

    /*private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    vibe.vibrate(100);
                    new Thread(new Runnable() {
                        public void run() {
                            try
                            {
                                Thread.sleep(500);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //vocalize
                                        answerClicked();
                                    }
                                });
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                        }
                    }).start();

                    break;
                case R.id.declineButton:
                    vibe.vibrate(100);
                    declineClicked();
                    break;
            }
        }
    };*/

    void getuimguri(String ud)
    {
       // reference1 = new Firebase("https://fir-g-a2c81.firebaseio.com/proimgdetail/" +ud+"/img");
    }
    public void checkforpropic(final String a)
    {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Checking details");
        pd.show();
        String url2 = "https://fir-g-a2c81.firebaseio.com/users.json";
        StringRequest request = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj = new JSONObject(s);

                    String user=a;
                    UserDetails.imguri= obj.getJSONObject(user).getString("profile_img");
                    if(UserDetails.imguri.equals("")){
                        int a=0;
                    }
                    else {
                        Uri u=Uri.parse(UserDetails.imguri);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.profileholder);
                        requestOptions.error(R.drawable.profileholder);
                        Glide.with(getApplicationContext())
                                .load(u)
                                .apply(requestOptions)
                                .into(imgv);
                       /* Glide
                                .with(getApplicationContext())
                                .load(u)
                                .placeholder(R.drawable.profileholder)
                                .dontAnimate()// the uri you got from Firebase
                                .centerCrop()
                                .into(imgv); //Your imageView variable*/
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

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
        pd.dismiss();
    }
}
