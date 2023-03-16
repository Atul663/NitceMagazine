package com.example.nitcemagazine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditorPage extends AppCompatActivity {

    ImageView articleImage;
    TextView articletitle;
    TextView description;
    RatingBar ratingBar;
    TextView ratingSummary;
    Button reject;
    Button post;

    String ArticleId;
    DatabaseReference dbreference;

    String imageUrl="";
    int reviewCount=0;
    float Rating=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_page);

        articleImage=(ImageView) findViewById(R.id.articleImage);
        articletitle=(TextView) findViewById(R.id.Title);
        description=(TextView) findViewById(R.id.articleText);
        ratingBar=(RatingBar) findViewById(R.id.ratingBar);
        ratingSummary=(TextView) findViewById(R.id.rating);
        reject=(Button) findViewById(R.id.rejectPost);
        post=(Button) findViewById(R.id.postArticle);

        dbreference= FirebaseDatabase.getInstance().getReference();

        ArticleId = getIntent().getStringExtra("article_id");


        dbreference.child("Articles").child(ArticleId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get Values from database
                imageUrl = snapshot.child("imageUrl").getValue(String.class);
                String title = snapshot.child("title").getValue(String.class);
                String content = snapshot.child("content").getValue(String.class);

                //set Values into Views
                Picasso.get().load(imageUrl).into(articleImage);
                articletitle.setText(title);
                description.setText(content);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dbreference.child("reviews").child(ArticleId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get Values from database
                reviewCount = snapshot.child("count").getValue(Integer.class);
                Rating = snapshot.child("rating").getValue(Float.class);

                //set Values into Views
                ratingBar.setRating(Rating);
                String summ=Rating+"/5 reviewed by "+reviewCount+" reviewers";
                ratingSummary.setText(summ);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbreference.child("Articles").child(ArticleId).removeValue();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> map=new HashMap<>();
                map.put("ArticleId",ArticleId);
                map.put("title",articletitle.getText());
                map.put("content",description.getText());
                map.put("imageURL",imageUrl);
                //map.put("Author",);  getAuthor Things done

                //insert article in PostedArticleTable
                dbreference.child("PostedArticles").push()
                        .setValue(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditorPage.this, "Article Posted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditorPage.this, "error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                //delete from Articles Table
                dbreference.child("Articles").child(ArticleId).removeValue();
            }
        });
    }
}