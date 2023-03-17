package com.example.nitcemagazine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddEditor extends AppCompatActivity {
    EditText student_id;
    Button addEditor;
    DatabaseReference dbreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_editor);

        addEditor=(Button) findViewById(R.id.buttom_add_editor);
        student_id=(EditText) findViewById(R.id.input_Email_add_editor);
        String student_email= String.valueOf(student_id.getText());

        addEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reviewerRef = dbreference.child("Reviewer").child(student_email);
                Query query = reviewerRef.orderByChild("email").equalTo(student_email);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean reviewerExists = snapshot.exists();
                        if(reviewerExists){
                            reviewerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    try{
                                        String name = snapshot.child("name").getValue(String.class);
                                        String password= snapshot.child("password").getValue(String.class);
                                        String url=snapshot.child("pic").getValue(String.class);

                                        reviewerRef.removeValue();


                                        DatabaseReference editorRef = dbreference.child("Editor");
                                        Query firstValueQuery = editorRef.orderByKey().limitToFirst(1);
                                        firstValueQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    String firstValueKey = snapshot.getChildren().iterator().next().getKey();
                                                    DatabaseReference firstValueRef = editorRef.child(firstValueKey);
                                                    firstValueRef.child("email").setValue(student_email);
                                                    firstValueRef.child("name").setValue(name);
                                                    firstValueRef.child("password").setValue(password);
                                                    firstValueRef.child("pic").setValue(url);
                                                    Toast.makeText(AddEditor.this, "Editor SuccessFully Changed", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    DatabaseReference edRef=editorRef.push();
                                                    edRef.child("name").setValue(name);
                                                    edRef.child("email").setValue(student_email);
                                                    edRef.child("password").setValue(password);
                                                    edRef.child("pic").setValue(url);
                                                    Toast.makeText(AddEditor.this, "Editor SuccessFully Added", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(AddEditor.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                    catch (Exception e){
                                        Toast.makeText(AddEditor.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(AddEditor.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            Toast.makeText(AddEditor.this, "Invalid User", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddEditor.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}