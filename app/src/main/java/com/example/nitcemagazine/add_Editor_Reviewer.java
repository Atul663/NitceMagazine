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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class add_Editor_Reviewer extends AppCompatActivity {

    EditText student_id;
    Button addReviewer;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference dbreference = database.getReference();

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
                DatabaseReference studentRef = database.getReference().child("Student");
                Query query = studentRef.orderByChild("email").equalTo(student_email);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean studentExists = snapshot.exists();
                        if (studentExists) {
                            // student exists in the database
                            studentRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    try {
                                        //Get Values from Student Table
                                        String uid=snapshot.getKey();
                                        String email=student_email;
                                        String name = snapshot.child("name").getValue(String.class);
                                        String password= snapshot.child("password").getValue(String.class);
                                        String url=snapshot.child("pic").getValue(String.class);
                                        //remove from Student Table
                                        database.getReference().child("Student").child(uid).removeValue();

                                        //Store in Reviewer Table
                                        DatabaseReference destRef = database.getReference().child("Reviewer").push();
                                        destRef.child("email").setValue(email);
                                        destRef.child("name").setValue(name);
                                        destRef.child("password").setValue(password);
                                        destRef.child("pic").setValue(url);
                                        Toast.makeText(add_Editor_Reviewer.this, "Reviewer Added Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    catch (Exception e){
                                        Toast.makeText(add_Editor_Reviewer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(add_Editor_Reviewer.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // student does not exist in the database
                            Toast.makeText(add_Editor_Reviewer.this, "Invalid User", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(add_Editor_Reviewer.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}