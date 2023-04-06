package com.example.nitcemagazine.PostUnpostedArticle;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.MainActivityPages.MainActivity2;
import com.example.nitcemagazine.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
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

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    String imageUrl="";
    String author;
    String category;
    int reviewCount=0;
    Float prevRating;

    String mesg;

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
                //dbreference.child("Article").child(ArticleId).removeValue();
                View rejectArticle= LayoutInflater.from(EditorPage.this).inflate(R.layout.post_rejection_dialog_box,null);

                EditText msg= rejectArticle.findViewById(R.id.editTextEmailContent);
                Button ok=rejectArticle.findViewById(R.id.okButton);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditorPage.this);
                builder.setView(rejectArticle);
                AlertDialog dialog1 =  builder.create();
                dialog1.show();

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mesg="Hi,\nWe are sorry to inform you that the post requested by you has been rejected due to following reason:\n";
                        String reason=msg.getText().toString();
                        if(!reason.isEmpty()) {
                            //
                            Long timeStamp = new Date().getTime();
                            Map<String,Object> map=new HashMap<>();
                            map.put("title",articletitle.getText());
                            map.put("description",description.getText());
                            map.put("ArticleImage",imageUrl);
                            map.put("authorUid",author);
                            map.put("category",category);
                            map.put("Rating",prevRating);
                            map.put("reviewCount",reviewCount);
                            map.put("Reason",reason);
                            map.put("DateTime",timeStamp);
                            dbreference.child("RejectedArticle").child(ArticleId)
                                    .setValue(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(EditorPage.this, "Article Rejected Successfully", Toast.LENGTH_SHORT).show();
                                            //delete from Articles Table
                                            dbreference.child("Article").child(ArticleId).removeValue();
                                            //Go somewhere
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditorPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            //Send Email
                            mesg = mesg + reason+"\nIf you don't repost the article within 15 days your article will be permanently deleted.\n\n"+articletitle.getText()+"\nThanks and Regards,\nNITC_E_MAGAZINE.";
                            dbreference.child("UserType").child(author).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String roleOfUser = snapshot.getValue().toString();

                                    dbreference.child(roleOfUser).child(author).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            String email=snapshot.child("email").getValue().toString();
                                            String[] recipients=new String[] {email};
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/html");
                                            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                                            intent.putExtra(Intent.EXTRA_SUBJECT, "Your Post has been Rejected");
                                            intent.putExtra(Intent.EXTRA_TEXT, mesg);
                                            startActivity(Intent.createChooser(intent, "Choose Email Client"));

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            Toast.makeText(EditorPage.this, "Give reason", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
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
                                //delete from review Table
                                dbreference.child("Review").child(ArticleId).removeValue();

                                //Enter Empty Liker List in Like Table
                                ArrayList<String> lk=new ArrayList<String>();
                                DatabaseReference ref=dbreference.child("Like").child(ArticleId);
                                Map<String,Object> map=new HashMap<>();
                                map.put("articleid",ArticleId);
                                map.put("likers",lk);
                                ref.setValue(map);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditorPage.this, "error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                //Send Email
                dbreference.child("UserType").child(author).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String roleOfUser = snapshot.getValue().toString();

                        dbreference.child(roleOfUser).child(author).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String email=snapshot.child("email").getValue().toString();
                                String[] recipients=new String[] {email};
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/html");
                                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Your Post has been posted");
                                String msg="Hi,\n This email is to inform you that the post requested by you has been posted on the basis on good reviews.Now you can check your post on our Article page by logging into the app.\n\n"+articletitle.getText()+" Thanks and Regards,\nNITC_E_MAGAZINE.";
                                intent.putExtra(Intent.EXTRA_TEXT, msg);
                                startActivity(Intent.createChooser(intent, "Choose Email Client"));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                //Go to Unposted Articles page
            }

        });


    }
}