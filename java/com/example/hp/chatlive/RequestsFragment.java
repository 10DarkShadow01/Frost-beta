package com.example.hp.chatlive;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

public class RequestsFragment extends Fragment {
    View parentView;
    DatabaseReference requestdatabaseReference,usersdatabase;
    RecyclerView mRequestRecyclerlist;
    DatabaseReference mFriendRequestDatabase,mrootDatabase;
    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriendRequestDatabase= FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mrootDatabase= FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView=inflater.inflate(R.layout.fragment_requests, container, false);
        Fabric.with(getActivity(), new Crashlytics());



        mRequestRecyclerlist=(RecyclerView)parentView.findViewById(R.id.Requests_list);
        requestdatabaseReference= FirebaseDatabase.getInstance().getReference().child("Friend_Req").child(UserDetails.username);
        usersdatabase= FirebaseDatabase.getInstance().getReference().child("users");

        mRequestRecyclerlist.setHasFixedSize(true);
        mRequestRecyclerlist.setLayoutManager(new LinearLayoutManager(getContext()));
        return parentView;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Requests,RequestsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(
                Requests.class,
                R.layout.request_list_single,
                RequestsViewHolder.class,
                requestdatabaseReference

        ) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder viewHolder, Requests model, int position) {

                String request_type=model.getRequest_type();
                if(request_type.equals("received"))
                {
                    //viewHolder.setDate(model.getDate());
                    final String list_userId=getRef(position).getKey();
                    usersdatabase.child(list_userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String name=list_userId;
                            String thumb=dataSnapshot.child("thumb_nail").getValue().toString();
                            String status=dataSnapshot.child("status").getValue().toString();
                            final String profile_img=dataSnapshot.child("profile_img").getValue().toString();

                            viewHolder.setName(name);
                            viewHolder.setStatus(status);
                            viewHolder.setThumb(thumb,getActivity());


                            viewHolder.mView.findViewById(R.id.acceptbtn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String currentdate= DateFormat.getDateTimeInstance().format(new Date());

                                    Map friendsMap=new HashMap<>();
                                    friendsMap.put("users/"+UserDetails.username+"/"+"Friends/"+name+"/"+"/date",currentdate);
                                    friendsMap.put("users/"+name+"/"+"Friends/"+UserDetails.username+"/"+"/date",currentdate);
                                    friendsMap.put("Friend_Req/"+UserDetails.username+"/"+name,null);
                                    friendsMap.put("Friend_Req/"+name+"/"+UserDetails.username,null);

                                    mrootDatabase.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if(databaseError==null)
                                            {
                                                Toast.makeText(getActivity(),name+" is your friend now.",Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                                }
                            });

                            viewHolder.mView.findViewById(R.id.rejectbtn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mFriendRequestDatabase.child(UserDetails.username).child(name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendRequestDatabase.child(name).child(UserDetails.username).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });
                                        }
                                    });
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
                else
                {
                    viewHolder.setViewGone();
                }

            }
        };
        mRequestRecyclerlist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public RequestsViewHolder(View itemView) {
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

        public void setViewGone()
        {
            RelativeLayout relv=(RelativeLayout)mView.findViewById(R.id.request_view);
            relv.setVisibility(View.GONE);
        }
    }

}
