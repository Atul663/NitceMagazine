package com.example.nitcemagazine.Fragments;

import static android.os.Environment.DIRECTORY_ALARMS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.Comment.CommentAdapter;
import com.example.nitcemagazine.Comment.CommentDetailClass;
import com.example.nitcemagazine.Comment.CommentModelClass;
import com.example.nitcemagazine.R;
import com.google.android.gms.common.api.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewArticle extends AppCompatActivity {
    TextView articelTitle,articleDesc,comment;
    ImageView articleImageCard,downloadButton;
    Button addComment;
    RecyclerView commentRecyclerView;

    ArrayList <String > imgfile = new ArrayList<>();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final int STORAGE_CODE = 1000;

    List<CommentModelClass> commentList;
    CommentAdapter adapter;
    CommentModelClass commentModelClass = new CommentModelClass();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    String id;

    Bitmap bmp, scaledBmp;
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
        downloadButton = findViewById(R.id.downloadButton);

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();


        id = getIntent().getStringExtra("ArticleIdIntent");

        getComment();

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(imgfile.get(0));
//                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
//                {
//                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
//                    {
//                        String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                        requestPermissions(permissions,STORAGE_CODE);
//                    }
//                    else {
//                        try {
//                            createPdf();
//                        } catch (FileNotFoundException e) {
//                            throw new RuntimeException(e);
//                        } catch (DocumentException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//                else {
                    try {
                        createPdf();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }catch (DocumentException e) {
                        throw new RuntimeException(e);
                    }
//                }
            }
        });
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
                    imgfile.add(img);
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

    private void createPdf() throws FileNotFoundException, DocumentException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, id+".pdf");
        OutputStream outputStream = new FileOutputStream(file);

        Document doc = new Document();



        try {
            PdfWriter.getInstance(doc,outputStream);

            doc.open();

            String desc = articleDesc.getText().toString();
            String title = articelTitle.getText().toString();
            doc.addAuthor("Atul");
            Font fontSize_10 =  FontFactory.getFont(FontFactory.TIMES, 18f);
            Paragraph p = new Paragraph(title,fontSize_10);
            doc.add(p);
            doc.add(new Paragraph(desc));
//            String urlOfImage = imgfile.get(0);
//
//
//            //Set absolute position for image in PDF (or fixed)
//            image.setAbsolutePosition(100f, 500f);
//
//            //Scale image's width and height
//            image.scaleAbsolute(200f, 200f);
//
//            //Scale image's height
//            image.scaleAbsoluteWidth(200f);
//            //Scale image's width
//            image.scaleAbsoluteHeight(200f);
//
//            doc.add(Image.getInstance(imgfile.get(0))); //Add image to document

            doc.close();

        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        Toast.makeText(ViewArticle.this, "Pdf downloaded successfully", Toast.LENGTH_SHORT).show();

    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case STORAGE_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    try {
//                        createPdf();
//                    } catch (FileNotFoundException e) {
//                        throw new RuntimeException(e);
//                    } catch (DocumentException e) {
//                        throw new RuntimeException(e);
//                    }
//                } else {
//                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//                }
//
//        }
//    }
}