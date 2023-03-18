package com.example.nitcemagazine.MainActivityPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nitcemagazine.AddEditor;
import com.example.nitcemagazine.AddReviewer;
import com.example.nitcemagazine.LoginAndSignUp.LoginActivity;
import com.example.nitcemagazine.PostArticle.AddPostFragement;
import com.example.nitcemagazine.R;
import com.example.nitcemagazine.LoginAndSignUp.SignUpPage;
import com.example.nitcemagazine.UnpostedArticles;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity2 extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView navigationDrawerIcon;
    ActionBarDrawerToggle toggle;

    TextView emailId,role;
    CircleImageView profileProfilePicture;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    Menu menuView;
    MenuItem signIn,signUp,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationDrawerIcon = findViewById(R.id.navigationDrawerIcon);

        View view = navigationView.getHeaderView(0);
        menuView = navigationView.getMenu();

        emailId = view.findViewById(R.id.textViewEmailNavDrawer);
        role = view.findViewById(R.id.textViewRoleNavDrawer);
        profileProfilePicture = view.findViewById(R.id.profilePictureNavDraver);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content, new MainFragment());
        ft.commit();


        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        headerDetails();

        navigationDrawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

//        System.out.println(user.getUid());


        findRole();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.signInNavDrawer)
                {
                    Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
                    startActivity(intent);
                } else if (id == R.id.signUpNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, SignUpPage.class);
                    startActivity(intent);
                } else if (id == R.id.addPostNavDrawer) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.content, new AddPostFragement()).addToBackStack("add post");
                    ft.commit();
                } else if (id == R.id.addReviewerNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, AddReviewer.class);
                    startActivity(intent);

                }
                else if (id == R.id.addEditorNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, AddEditor.class);
                    startActivity(intent);

                }else if (id == R.id.logoutNavDrawer){
                    auth.signOut();
                    Intent intent = new Intent(MainActivity2.this,MainActivity2.class);
                    startActivity(intent);
                    finishAffinity();
                } else if (id == R.id.postArticleNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, UnpostedArticles.class);
                    startActivity(intent);
                } else if (id == R.id.reviewArticleNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, UnpostedArticles.class);
                    startActivity(intent);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    void headerDetails()
    {
        if(user != null) {

            reference.child("UserType").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String roleOfUser = snapshot.getValue().toString();

                    DatabaseReference ref = database.getReference();

                    ref.child(roleOfUser).child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            emailId.setText(snapshot1.child("email").getValue().toString());
                            role.setText(snapshot1.child("role").getValue().toString());
                            String profilePicture = snapshot1.child("profilePictures").getValue().toString();
                            System.out.println(profilePicture);
                            if(profilePicture == null )
                            {
                                profileProfilePicture.setImageResource(R.drawable.get_started_button);
                            }
                            else
                            {
                                Picasso.get().load(profilePicture).into(profileProfilePicture);
                            }
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
//            auth.signOut();
        }

        else {
            profileProfilePicture.setImageResource(R.drawable.ic_launcher_background);
            emailId.setText("Guest");
            role.setText("Guest");
        }
    }



    private void inflateMenu(char userRole) {
        if(userRole == 'S')
        {
            navigationView.inflateMenu(R.menu.navigation_item_user);
        }
        else if(userRole == 'A')
        {
            navigationView.inflateMenu(R.menu.navigation_item_admin);
        } else if (userRole == 'R') {
            navigationView.inflateMenu(R.menu.navigation_item_reviewer);
        } else if (userRole == 'E') {
            navigationView.inflateMenu(R.menu.navigation_item_editor);
        }

        setMenu();
    }

    void findRole()
    {

        if(user != null) {
            char[] userRole = new char[1];
            reference.child("UserType").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userRole[0] = snapshot.getValue().toString().charAt(0);
                    inflateMenu(userRole[0]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
            inflateMenu('S');
    }

    void setMenu()
    {
        signIn = menuView.findItem(R.id.signInNavDrawer);
        signUp = menuView.findItem(R.id.signUpNavDrawer);
        logout = menuView.findItem(R.id.logoutNavDrawer);
        if(user != null) {
            signIn.setVisible(false);
            signUp.setVisible(false);
            logout.setVisible(true);
        }
        else {
            signIn.setVisible(true);
            signUp.setVisible(true);
            logout.setVisible(false);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}