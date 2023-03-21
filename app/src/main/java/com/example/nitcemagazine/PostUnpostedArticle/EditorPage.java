package com.example.nitcemagazine.PostUnpostedArticle;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.R;
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
    String author;
    String category;
    int reviewCount=0;
    Float prevRating;

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

        ArticleId = getIntent().getStringExtra("ArticleIdIntent");

        dbreference.child("Article").child(ArticleId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get Values from database
                try{
                    imageUrl = snapshot.child("ArticleImage").getValue().toString();
                    prevRating = Float.parseFloat(snapshot.child("Rating").getValue().toString());
                    String content = snapshot.child("description").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    author = snapshot.child("authorUid").getValue(String.class);
                    reviewCount = Integer.parseInt(snapshot.child("reviewCount").getValue().toString());
                    category = snapshot.child("category").getValue().toString();

                    //set Values into Views
                    //if(imageUrl !=null)
                    //Toast.makeText(EditorPage.this, imageUrl.length(), Toast.LENGTH_SHORT).show();
                    if(imageUrl.equals("null")){
                        articleImage.setMinimumHeight(0);
                        articleImage.setLayoutParams(new ViewGroup.LayoutParams(0,0));
                    }
                    else {
                        Picasso.get().load(imageUrl).into(articleImage);
                    }


                    articletitle.setText(title);
                    description.setText(content);
                    ratingBar.setRating(prevRating);
                    ratingBar.setEnabled(false);
                    double r=Math.round(prevRating*10.0)/10.0;
                    String sum = r + "/5 reviewed by " + reviewCount + " reviewers";
                    ratingSummary.setText(sum);
                }
                catch (Exception E)
                {
                    System.out.println();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbreference.child("Article").child(ArticleId).removeValue();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> map=new HashMap<>();
                map.put("title",articletitle.getText());
                map.put("description",description.getText());
                map.put("ArticleImage",imageUrl);
                map.put("authorUid",author);
                map.put("category",category);
                map.put("Rating",prevRating);
                map.put("reviewCount",reviewCount);

                //insert article in PostedArticleTable
                dbreference.child("PostedArticle").child(ArticleId)
                        .setValue(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditorPage.this, "Article Posted Successfully", Toast.LENGTH_SHORT).show();
                                //delete from Articles Table
                                dbreference.child("Article").child(ArticleId).removeValue();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditorPage.this, "error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}