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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ReviewerPage extends AppCompatActivity {


    ImageView articleImage;
    TextView articletitle;
    TextView description;
    RatingBar ratingBar;
    Button submitButton;

    String ArticleId;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbreference = database.getReference();
    int reviewCount=0;
    float prevRating=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewer_page);
        articleImage=(ImageView) findViewById(R.id.articleImage);
        articletitle=(TextView) findViewById(R.id.Title);
        description=(TextView) findViewById(R.id.articleText);
        ratingBar=(RatingBar) findViewById(R.id.ratingBar);
        submitButton=(Button)findViewById(R.id.submitRating);

//        dbreference= FirebaseDatabase.getInstance().getReference();

        ArticleId = getIntent().getStringExtra("ArticleIdIntent");

        dbreference.child("Article").child(ArticleId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get Values from database
                System.out.println(ArticleId);
                String imageUrl = snapshot.child("ArticleImage").getValue(String.class);
                String rating = snapshot.child("Rating").getValue().toString();
                prevRating=Float.parseFloat(rating);
                String content = snapshot.child("description").getValue(String.class);
                String title = snapshot.child("title").getValue(String.class);
                System.out.println(title);
                String reviewCnt = snapshot.child("reviewCount").getValue().toString();
                reviewCount =Integer.parseInt(reviewCnt);

                //set Values into Views
                Picasso.get().load(imageUrl).into(articleImage);
                articletitle.setText(title);
                description.setText(content);
                ratingBar.setRating(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReviewerPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the current rating from the rating bar
                float currentRating = ratingBar.getRating();

                // calculate the new average rating
                float newRating = ((currentRating + (reviewCount * prevRating)) / (reviewCount + 1));

                // update the review count
                dbreference.child("Article").child(ArticleId).child("reviewCount").setValue(reviewCount + 1);

                // update the review rating
                dbreference.child("Article").child(ArticleId).child("Rating").setValue(newRating);

                // show a toast message to indicate that the review has been submitted
                Toast.makeText(ReviewerPage.this, "Review submitted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}