package com.example.hp.chatlive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

import static android.widget.Toast.LENGTH_LONG;

public class KnownUsers4 extends Fragment {

    DatabaseReference friendsdatabaseReference,usersdatabase;
    RecyclerView mFriendslist;
    public View mMainViewr;
    String key = "";
    String url5 = "https://fir-g-a2c81.firebaseio.com/proimgdetail.json";
    String url4 = "https://fir-g-a2c81.firebaseio.com/users/"+UserDetails.username+"/Friends.json";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainViewr = inflater.inflate(R.layout.act_knownusers4, container, false);

        Fabric.with(getActivity(), new Crashlytics());



        mFriendslist=(RecyclerView)mMainViewr.findViewById(R.id.Friends_list);
        friendsdatabaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(UserDetails.username).child("Friends");
        usersdatabase= FirebaseDatabase.getInstance().getReference().child("users");

        mFriendslist.setHasFixedSize(true);
        mFriendslist.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainViewr;
    }
/////////////////////


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,friendsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Friends, friendsViewHolder>(
                Friends.class,
                R.layout.program_list,
                friendsViewHolder.class,
                friendsdatabaseReference

        ) {
            @Override
            protected void populateViewHolder(final friendsViewHolder viewHolder, Friends model, int position) {

                //viewHolder.setDate(model.getDate());
                final String list_userId=getRef(position).getKey();
                usersdatabase.child(list_userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name=list_userId;
                        String thumb=dataSnapshot.child("thumb_nail").getValue().toString();
                        String status=dataSnapshot.child("status").getValue().toString();
                        final String profile_img=dataSnapshot.child("profile_img").getValue().toString();
                        if(dataSnapshot.hasChild("online"))
                        {
                            String online=dataSnapshot.child("online").getValue().toString();
                            viewHolder.setOnline(online);
                        }


                        viewHolder.setName(name);
                        viewHolder.setStatus(status);
                        viewHolder.setThumb(thumb,getActivity());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UserDetails.chatWith=name;
                                startActivity(new Intent(getActivity()/*Users.this*/, Chat.class));
                            }
                        });
                        viewHolder.mView.findViewById(R.id.imageViewh).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Dialog dialog=new Dialog(getActivity(),R.style.PauseDialog);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                //dialog.setContentView(R.layout.jabp);
                                dialog.setContentView(R.layout.zoom_profilepicdialog);
                                dialog.show();
                                CircleImageView imageView=(CircleImageView)dialog.findViewById(R.id.proimgview);
                                if(!profile_img.equals(""))
                                {
                                    Picasso.with(getActivity()).load(profile_img).placeholder(R.drawable.profileholder).into(imageView);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mFriendslist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class friendsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public friendsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setDate(String date){
           // TextView userNameView=(TextView)mView.findViewById(R.id.user_single_status);
          //  userNameView.setText(date);
        }

        public void setName(String name){
            TextView userNameView=(TextView)mView.findViewById(R.id.textViewh);
            userNameView.setText(name);
        }

        public void setStatus(String status){
            TextView userStatusView=(TextView)mView.findViewById(R.id.textView100);
            userStatusView.setText(status);
        }

        public void setThumb(String thumb, Context cnt){
            CircleImageView img=(CircleImageView)mView.findViewById(R.id.imageViewh);
            if(!thumb.equals(""))
            {
                Picasso.with(cnt).load(thumb).placeholder(R.drawable.profileholder).into(img);
            }
            else
            {
                Picasso.with(cnt).load(R.drawable.profileholder).into(img);
            }
        }

        public void setOnline(String online){
            ImageView ig=(ImageView)mView.findViewById(R.id.online_indicator);
            if(online.equals("true"))
            {
                ig.setVisibility(View.VISIBLE);
            }
            else {
                ig.setVisibility(View.INVISIBLE);
            }

        }
    }


/////////////////////
}
