package com.example.nitcemagazine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nitcemagazine.AddReviewerAndEditor.AddReviewer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


public class userFragment extends Fragment {
    TextView name, role;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    public userFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user,null);

        name = view.findViewById(R.id.textView3);
        role = view.findViewById(R.id.textView4);

        AddReviewer activity = (AddReviewer) getActivity();
        String roleOfUser = activity.role();
        String uid = activity.uid();
        System.out.println(uid+" =======================");
        try {
            reference.child(roleOfUser).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name.setText(snapshot.child("name").getValue().toString());
                    role.setText(snapshot.child("role").getValue().toString());

                    System.out.println(name.getText().toString()+"---------------------------------------------");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e)
        {
            System.out.println(e);
        }


        return inflater.inflate(R.layout.fragment_user, container, false);

    }
}