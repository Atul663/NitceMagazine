package com.example.nitcemagazine;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.nitcemagazine.FragmentAdapters.EducationalAdapter;
import com.example.nitcemagazine.FragmentAdapters.ModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UnpostedArticles extends AppCompatActivity {

    RecyclerView unpostedArticle;
    UnpostedArticleAdapter unpostedArticleAdapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    List<ModelClass> articleList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ModelClass modelClass = new ModelClass();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unposted_articles);

        unpostedArticle = findViewById(R.id.recyclerViewUnpostedArticle);

        unpostedArticle.setLayoutManager(new LinearLayoutManager(this));

        articleList = new ArrayList<>();

        getArticle();
    }

    void getArticle()
    {
        reference.child("Article").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                modelClass = snapshot.getValue(ModelClass.class);
                articleList.add(modelClass);
                modelClass.setId(snapshot.getKey());
                unpostedArticleAdapter.notifyDataSetChanged();
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
        unpostedArticleAdapter = new UnpostedArticleAdapter(articleList, UnpostedArticles.this);
        unpostedArticle.setAdapter(unpostedArticleAdapter);
    }


}