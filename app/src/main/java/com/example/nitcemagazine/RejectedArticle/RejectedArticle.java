package com.example.nitcemagazine.RejectedArticle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.nitcemagazine.FragmentAdapters.ModelClass;
import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RejectedArticle extends AppCompatActivity {

    List<ModelClass> articleList;
    RejectedArticleAdapter adapter;
    RecyclerView recyclerView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ModelClass modelClass = new ModelClass();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_article);

        recyclerView = findViewById(R.id.recyclerViewRejectedArticle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        articleList = new ArrayList<>();

        getArticle();
    }

    void getArticle()
    {
        reference.child("RejectedArticle").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                modelClass = snapshot.getValue(ModelClass.class);
                if(snapshot.child("authorUid").getValue().toString().equalsIgnoreCase(user.getUid())) {
                    articleList.add(modelClass);
                    modelClass.setId(snapshot.getKey());
                    System.out.println("hii " +modelClass.getId());

                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new RejectedArticleAdapter(articleList, RejectedArticle.this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {

        FirebaseAuth auth1 = FirebaseAuth.getInstance();
        FirebaseUser user1 = auth1.getCurrentUser();
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference reference1 = database1.getReference();
        ArrayList<String> ar = new ArrayList<>();
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        String time = sdf.format(timeStamp);
        if(user1 != null)
        {
            reference1.child("RejectedArticle").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("authorUid").getValue().toString().equals(user1.getUid())) {
                            Long time = (Long) ds.child("DateTime").getValue();
                            try {
                                String date1 = sdf.format(time);
                                System.out.println(date1);
                                Long timeStamp = new Date().getTime();
                                String dt2 = sdf.format(timeStamp);


                                Date date = dtf.parse(date1);
                                Date date2 = dtf.parse(dt2);
                                long diff = date2.getTime() - date.getTime();
                                int day = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                                System.out.println("min: " + TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS));

                                if (day >= 7) {
                                    reference.child("RejectedArticle").child(ds.getKey()).removeValue();
                                }
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        super.onStart();
    }
}