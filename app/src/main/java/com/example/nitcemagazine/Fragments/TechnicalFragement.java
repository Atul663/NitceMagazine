package com.example.nitcemagazine.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitcemagazine.FragmentAdapters.ModelClass;
import com.example.nitcemagazine.FragmentAdapters.TechnicalAdapter;
import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TechnicalFragement extends Fragment {
    List<ModelClass> articleList;
    TechnicalAdapter adapter;
    RecyclerView recyclerView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ModelClass modelClass = new ModelClass();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.technical_fragement,null);

        recyclerView = view.findViewById(R.id.recyclerViewTechnical);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        articleList = new ArrayList<>();

        getArticle();

        return view;
    }

    void getArticle()
    {
        reference.child("PostedArticle").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                modelClass = snapshot.getValue(ModelClass.class);
                if(modelClass.getCategory().equalsIgnoreCase("technical")) {

                    articleList.add(modelClass);
                    modelClass.setId(snapshot.getKey());
                    adapter.notifyDataSetChanged();
                }


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
        adapter = new TechnicalAdapter(articleList, getContext());
        recyclerView.setAdapter(adapter);
    }
}
