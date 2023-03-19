package com.example.nitcemagazine.ReviewUnpostedArticle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.nitcemagazine.FragmentAdapters.ModelClass;
import com.example.nitcemagazine.PostUnpostedArticle.PostUnpostedArticleAdapter;
import com.example.nitcemagazine.PostUnpostedArticle.PostUnpostedArticles;
import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ReviewUnpostedArticles extends AppCompatActivity {

    RecyclerView unpostedArticle;
    ReviewUnpostedArticleAdapter reviewUnpostedArticleAdapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    List<ModelClass> articleList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ModelClass modelClass = new ModelClass();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_unposted_articles);

        unpostedArticle = findViewById(R.id.recyclerViewReviewUnpostedArticle);

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
                reviewUnpostedArticleAdapter.notifyDataSetChanged();
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
        reviewUnpostedArticleAdapter= new ReviewUnpostedArticleAdapter(articleList, ReviewUnpostedArticles.this);
        unpostedArticle.setAdapter(reviewUnpostedArticleAdapter);
    }


}