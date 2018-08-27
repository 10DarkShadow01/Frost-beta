package com.example.hp.chatlive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ImageView mProfileImage;
    TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    Button mProfileSendFriendRequest,mProfileDeclineFriendRequest;

    DatabaseReference mUserDatabase;
    DatabaseReference mFriendRequestDatabase;
    DatabaseReference mFriendsDatabase;
    DatabaseReference mNotificationsDatabase;
    DatabaseReference mrootDatabase;

    FirebaseUser mCurrentUser;

    String mcurrentstate="not_friends";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id=getIntent().getStringExtra("user_id");

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();

        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        mUserDatabase.keepSynced(true);
        mFriendRequestDatabase= FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendsDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(UserDetails.username).child("Friends");
        mNotificationsDatabase= FirebaseDatabase.getInstance().getReference().child("notifications");
        mrootDatabase= FirebaseDatabase.getInstance().getReference();

        mProfileName=(TextView)findViewById(R.id.profile_displayname);
        mProfileImage=(ImageView)findViewById(R.id.profile_image);
        mProfileStatus=(TextView)findViewById(R.id.profile_status);
        mProfileFriendsCount=(TextView)findViewById(R.id.profile_friendscount);
        mProfileSendFriendRequest=(Button)findViewById(R.id.profile_sendfriendrequest);
        mProfileDeclineFriendRequest=(Button)findViewById(R.id.profile_declinefriendrequest);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayname=user_id;
                String status=dataSnapshot.child("status").getValue().toString();
                final String image=dataSnapshot.child("profile_img").getValue().toString();

                mProfileName.setText(displayname);
                mProfileStatus.setText(status);
                if(!image.equals("default"))
                {
                    //Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.flap).into(mProfileImage);
                    Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profileholder).into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.profileholder).into(mProfileImage);
                        }
                    });

                }
                else
                {
                    Picasso.with(ProfileActivity.this).load(R.drawable.profileholder).into(mProfileImage);
                }

                mFriendRequestDatabase.child(UserDetails.username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id))
                        {
                            String resq_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(resq_type.equals("received"))
                            {
                                mcurrentstate="received_req";
                                mProfileSendFriendRequest.setText("  Accept Request  ");
                                mProfileDeclineFriendRequest.setVisibility(View.VISIBLE);
                                mProfileDeclineFriendRequest.setEnabled(true);
                            }
                            else if(resq_type.equals("sent"))
                            {
                                mcurrentstate="cancel_req";
                                mProfileSendFriendRequest.setText("  Cancel Request  ");
                                mProfileDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineFriendRequest.setEnabled(false);
                            }
                        }
                        else
                        {
                            mFriendsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id))
                                    {
                                        mcurrentstate="friends";
                                        mProfileSendFriendRequest.setText("  UnFriend  ");
                                        mProfileDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                        mProfileDeclineFriendRequest.setEnabled(false);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileSendFriendRequest.setEnabled(false);
                // -------------- NOT FREINDS STATE----------------
                if(mcurrentstate.equals("not_friends"))
                {
                    mFriendRequestDatabase.child(UserDetails.username).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mFriendRequestDatabase.child(user_id).child(UserDetails.username).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String,String> notification=new HashMap<>();
                                        notification.put("from",UserDetails.username);
                                        notification.put("type","request");
                                        mNotificationsDatabase.child(user_id).push().setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mcurrentstate="cancel_req";
                                                mProfileSendFriendRequest.setEnabled(true);
                                                mProfileSendFriendRequest.setText("  Cancel Request  ");
                                                Toast.makeText(ProfileActivity.this,"Request Sent Successfully",Toast.LENGTH_LONG).show();
                                                mProfileDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                mProfileDeclineFriendRequest.setEnabled(false);
                                            }
                                        });

                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this,"Failed Sending Request",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                // -------------- CANCEL STATE ---------------
                if(mcurrentstate == "cancel_req")
                {
                    mFriendRequestDatabase.child(UserDetails.username).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendRequestDatabase.child(user_id).child(UserDetails.username).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mcurrentstate="not_friends";
                                    mProfileSendFriendRequest.setEnabled(true);
                                    mProfileSendFriendRequest.setText("Send Friend Request");
                                    mProfileDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                    mProfileDeclineFriendRequest.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                //--------------------REQUEST RECEIVED-------------------------
                if(mcurrentstate == "received_req")
                {
                    final String currentdate= DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap=new HashMap<>();
                    friendsMap.put("users/"+UserDetails.username+"/"+"Friends/"+user_id+"/"+"/date",currentdate);
                    friendsMap.put("users/"+user_id+"/"+"Friends/"+UserDetails.username+"/"+"/date",currentdate);
                    friendsMap.put("Friend_Req/"+UserDetails.username+"/"+user_id,null);
                    friendsMap.put("Friend_Req/"+user_id+"/"+UserDetails.username,null);

                    /*mFriendsDatabase.child(mCurrentUser.getUid()).child(user_id).setValue(currentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(user_id).child(mCurrentUser.getUid()).setValue(currentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mcurrentstate="friends";
                                                    mProfileSendFriendRequest.setEnabled(true);
                                                    mProfileSendFriendRequest.setText("  UnFriend  ");
                                                    mProfileDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                    mProfileDeclineFriendRequest.setEnabled(false);
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    });*/
                    mrootDatabase.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError==null)
                            {
                                mcurrentstate="friends";
                                mProfileSendFriendRequest.setEnabled(true);
                                mProfileSendFriendRequest.setText("  UnFriend  ");
                                mProfileDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineFriendRequest.setEnabled(false);
                            }
                            else {
                                Toast.makeText(ProfileActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }

                //--------------------Already Friends-------------------------
                if(mcurrentstate == "friends")
                {
                    final String currentdate= DateFormat.getDateTimeInstance().format(new Date());
                    mFriendsDatabase.child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mrootDatabase.child("users").child(user_id).child("Friends").child(UserDetails.username).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mcurrentstate="not_friends";
                                    mProfileSendFriendRequest.setEnabled(true);
                                    mProfileSendFriendRequest.setText("Send Friend Request");
                                    mProfileDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                    mProfileDeclineFriendRequest.setEnabled(false);

                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
