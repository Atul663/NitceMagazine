package com.example.nitcemagazine.FragmentAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitcemagazine.R;
import com.example.nitcemagazine.ViewArticle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class FestAdapter extends RecyclerView.Adapter<FestAdapter.ViewHolder> {

    List<ModelClass> articleList;
    Context articleContext;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    public FestAdapter(List<ModelClass> articleList) {
        this.articleList = articleList;
//        this.articleContext = articleContext;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_with_image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String id = articleList.get(position).getId();

            reference.child("Article").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String title = snapshot.child("title").getValue().toString();
                    String desc = snapshot.child("description").getValue().toString();
                    String img = snapshot.child("Article Image").getValue().toString();

                    String uid = snapshot.child("authorUid").getValue().toString();

                    DatabaseReference ref = database.getReference();
                    DatabaseReference ref1 = database.getReference();
                    ref1.child("UserType").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String roleOfUser = snapshot.getValue().toString();
                            ref.child(roleOfUser).child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String author = snapshot.child("name").getValue().toString();
                                    holder.authorName.setText(author);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    holder.articelTitle.setText(title);
                    holder.articleDesc.setText(desc);

                    if(!img.equalsIgnoreCase("null"))
                    {
                        Picasso.get().load(img).into(holder.articleImageCard);
                    }
                    else
                    {
                        holder.articleImageCard.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        holder.articleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(articleContext, ViewArticle.class);
                articleContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int i = 0; i < articleList.size(); i++)
        {
            if(articleList.get(i).getCategory().equalsIgnoreCase("fest"))
            {
                count++;
            }
        }
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView articelTitle,articleDesc,authorName;
        ImageView articleImageCard;
        CardView articleCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            articelTitle = itemView.findViewById(R.id.textViewTitleCard);
            articleDesc = itemView.findViewById(R.id.textViewDescription);
            articleImageCard = itemView.findViewById(R.id.imageViewArticleImageCard);
            authorName = itemView.findViewById(R.id.textViewAuthorNameCard);
            articleCardView = itemView.findViewById(R.id.articleCardView);

        }
    }

}
