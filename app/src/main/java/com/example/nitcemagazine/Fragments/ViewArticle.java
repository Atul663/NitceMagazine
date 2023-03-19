package com.example.nitcemagazine.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nitcemagazine.Comment.CommentAdapter;
import com.example.nitcemagazine.Comment.CommentDetailClass;
import com.example.nitcemagazine.Comment.CommentModelClass;
import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewArticle extends AppCompatActivity {
    TextView articelTitle,articleDesc,comment;
    ImageView articleImageCard;
    Button addComment;
    RecyclerView commentRecyclerView;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    List<CommentModelClass> commentList;
    CommentAdapter adapter;
    CommentModelClass commentModelClass = new CommentModelClass();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_article);

        articelTitle = findViewById(R.id.TitleArticleView);
        articleDesc = findViewById(R.id.articleTextArticleView);
        articleImageCard = findViewById(R.id.articleImageArticleView);
        comment = findViewById(R.id.commentArticleView);
        addComment = findViewById(R.id.buttonAddCommentArticleView);
        commentRecyclerView = findViewById(R.id.recyclerViewArticleView);

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();


        id = getIntent().getStringExtra("ArticleIdIntent");

        getComment();
        reference.child("PostedArticle").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue().toString();
                String desc = snapshot.child("description").getValue().toString();
                String img = snapshot.child("ArticleImage").getValue().toString();


                articelTitle.setText(title);
                articleDesc.setText(desc);


                if (!img.equalsIgnoreCase("null")) {
                    Picasso.get().load(img).into(articleImageCard);
                }
                else
                {
                    articleImageCard.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = comment.getText().toString();

                DatabaseReference ref = database.getReference();

                String commentKey = ref.child("ArticleComment").child(id).push().getKey();
                CommentDetailClass commentDetailClass = new CommentDetailClass(commentText, auth.getCurrentUser().getUid(),id);
                ref.child("ArticleComment").child(id).child(commentKey).setValue(commentDetailClass);

                comment.setText("");
            }
        });
    }

    private void getComment() {
        reference.child("ArticleComment").child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                commentModelClass = snapshot.getValue(CommentModelClass.class);
                commentList.add(commentModelClass);
                adapter.notifyDataSetChanged();

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
        adapter = new CommentAdapter(commentList,id);
        commentRecyclerView.setAdapter(adapter);
    }
}