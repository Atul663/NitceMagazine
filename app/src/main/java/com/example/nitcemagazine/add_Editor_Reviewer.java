package com.example.nitcemagazine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

public class add_Editor_Reviewer extends AppCompatActivity {

    EditText student_id;
    String name;
    String password;
    String imageUrl="";

    Button addReviewer;
    DatabaseReference dbreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_editor_reviewer);
        addReviewer=(Button) findViewById(R.id.buttom_add_editor);
        student_id=(EditText) findViewById(R.id.input_Email_add_editor);
        String student_email= String.valueOf(student_id.getText());
        addReviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getValues of given email
                DatabaseReference articleRef = dbreference.child("Student").child(student_email);


                if(articleRef!=null){

                    dbreference.child("Reviewer").push()
                            .setValue(articleRef)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(add_Editor_Reviewer.this, "Reviewer Added Successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(add_Editor_Reviewer.this, "Failed To Add Reviewer", Toast.LENGTH_SHORT).show();
                                }
                            });

                    dbreference.child("Student").child(student_email).removeValue();
                }

            }
        });
    }
}