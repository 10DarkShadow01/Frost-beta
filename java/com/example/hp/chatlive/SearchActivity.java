package com.example.hp.chatlive;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {
DatabaseReference searchDatabase;
RecyclerView searchrecyclerview;
EditText editText;
ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchDatabase= FirebaseDatabase.getInstance().getReference().child("users");
        searchrecyclerview=(RecyclerView)findViewById(R.id.searchrecyclerview);
        searchrecyclerview.setHasFixedSize(true);
        searchrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        editText=(EditText)findViewById(R.id.editText2);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String searchtxt=s.toString();
                search(searchtxt);
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });

        img=(ImageView)findViewById(R.id.imageView10);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchtxt=editText.getText().toString();
                search(searchtxt);
            }
        });

    }


    public void search(String searchtxt) {
        Query query= searchDatabase.orderByKey().startAt(searchtxt).endAt(searchtxt+"\uf8ff");
        FirebaseRecyclerAdapter<Search,searchViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Search, searchViewHolder>(
                Search.class,
                R.layout.searchlist_single,
                searchViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final searchViewHolder viewHolder, Search model, int position) {


                final String list_userId=getRef(position).getKey();
                viewHolder.setName(list_userId);
                viewHolder.setStatus(model.getStatus());
                viewHolder.setThumb(model.getThumb_nail(),SearchActivity.this);

                /*viewHolder.mView.findViewById(R.id.imageViewh).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog=new Dialog(SearchActivity.this,R.style.PauseDialog);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        //dialog.setContentView(R.layout.jabp);
                        dialog.setContentView(R.layout.zoom_profilepicdialog);
                        dialog.show();
                        CircleImageView imageView=(CircleImageView)dialog.findViewById(R.id.proimgview);
                        if(!profile_img.equals(""))
                        {
                            Picasso.with(SearchActivity.this).load(profile_img).placeholder(R.drawable.profileholder).into(imageView);
                        }
                    }
                });*/
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       if(list_userId.equals(UserDetails.username))
                       {
                           Intent inn=new Intent(SearchActivity.this,ResideV.class);
                           inn.putExtra("own","yes");
                           startActivity(inn);
                       }
                       else
                       {
                           Intent inn2=new Intent(SearchActivity.this,ProfileActivity.class);
                           inn2.putExtra("user_id",list_userId);
                           startActivity(inn2);
                       }
                    }
                });

            }
        };
        searchrecyclerview.setAdapter(firebaseRecyclerAdapter);
    }

    public static class searchViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public searchViewHolder(View itemView) {
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
}
