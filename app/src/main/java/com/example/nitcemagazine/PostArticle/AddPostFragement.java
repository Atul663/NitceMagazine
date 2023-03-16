package com.example.nitcemagazine.PostArticle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nitcemagazine.MainActivityPages.MainActivity2;
import com.example.nitcemagazine.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class AddPostFragement extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView signout;
    Button articleImage, submit;
    EditText title,description;
    String item[] = {"Select Category","Home","Educational","Technical","Sport","Fest"};
    ArrayAdapter<String > arrayAdapter;
    Spinner autoCompleteTextView;

    boolean imgControl = false;
    Uri imageUri;
    String key;
    String imageSelectedName;

    String categorySelected = null;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    FirebaseStorage storage;
    StorageReference storageReference;


    public AddPostFragement() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post_fragement, container, false);

//        signout = (TextView) view.findViewById(R.id.button2);
        autoCompleteTextView = (Spinner) view.findViewById(R.id.category);
        articleImage = (Button) view.findViewById(R.id.selectImg);
        title = (EditText) view.findViewById(R.id.editTextTitle);
        description = (EditText) view.findViewById(R.id.editTextTextMultiLineDescription);
        submit = (Button) view.findViewById(R.id.SubmitForReview);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



//        signout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                auth.signOut();
//                Intent intent = new Intent(getActivity(),MainActivity.class);
//                startActivity(intent);
//                getActivity().finish();
//            }
//        });

        arrayAdapter = new ArrayAdapter<String >(getActivity(),R.layout.category_drop_down_menu,item);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        articleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
                System.out.println(imgControl);
                if (imgControl) {
//                    Cursor cursor = getContentResolver().query(imageUri,null,null,null,null);
//                    int imageSelected = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                    imageSelectedName = cursor.getString(imageSelected);
//                    System.out.println(imageSelectedName);
                    articleImage.setText("imageSelectedName");
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleDetails articleDetails = new ArticleDetails(title.getText().toString(),description.getText().toString(),categorySelected,auth.getCurrentUser().getUid());
                key = reference.child("Article").push().getKey();

                reference.child("Article").child(key).setValue(articleDetails);
                setArticleImage();
                Intent intent = new Intent(getActivity(), MainActivity2.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }

    private void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            imgControl = true;
        }
        else
        {
            imgControl = false;
        }
    }

    private void setArticleImage() {
        if(imgControl)
        {
            UUID randomId = UUID.randomUUID();
            String imgName = "images/" + randomId + ".jpg";
            System.out.println(imgName);

            storageReference.child(imgName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference myStorageRef = storage.getReference(imgName);
                    myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String filePath = uri.toString();
                            reference.child("Article").child(key).child("Article Image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
//                                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "this fail", Toast.LENGTH_SHORT).show();
                }
            });

        }

        else
        {
            reference.child("Article").child(key).child("Article Image").setValue("null");
        }
    }

}