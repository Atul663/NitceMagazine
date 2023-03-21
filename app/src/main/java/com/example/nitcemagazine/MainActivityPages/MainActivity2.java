package com.example.nitcemagazine.MainActivityPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.AddReviewerAndEditor.AddEditor;
import com.example.nitcemagazine.AddReviewerAndEditor.AddReviewer;
import com.example.nitcemagazine.AddReviewerAndEditor.DeleteReviewer;
import com.example.nitcemagazine.DeleteArticle;
import com.example.nitcemagazine.LoginAndSignUp.LoginActivity;
import com.example.nitcemagazine.PostArticle.AddPostFragement;
import com.example.nitcemagazine.R;
import com.example.nitcemagazine.LoginAndSignUp.SignUpPage;
import com.example.nitcemagazine.PostUnpostedArticle.PostUnpostedArticles;
import com.example.nitcemagazine.ReviewUnpostedArticle.ReviewUnpostedArticles;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
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
    AlertDialog dialog;
    NavigationView navigationView;
    ImageView navigationDrawerIcon;
    ActionBarDrawerToggle toggle;

    EditText username,password;
    Button signin;

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
                    if(user!= null)
                    {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.content, new AddPostFragement()).addToBackStack("add post");
                        ft.commit();
                    }
                    else {
                        AlertDialog.Builder dialogLogin = new AlertDialog.Builder(MainActivity2.this);
                        View loginView = getLayoutInflater().inflate(R.layout.dialog_login,null);

                        username = loginView.findViewById(R.id.editTextDialogUsername);
                        password = loginView.findViewById(R.id.editTextDialogPassword);
                        signin = loginView.findViewById(R.id.buttonDialogLogin);
                        dialogLogin.setView(loginView);
                        dialog = dialogLogin.create();
                        dialog.show();
                        signin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String userEmailId = username.getText().toString();
                                String userPassword = password.getText().toString();

                                if (userEmailId.isEmpty() && userPassword.isEmpty()) {
                                    Toast.makeText(MainActivity2.this, "Please enter the Email id and Password", Toast.LENGTH_SHORT).show();
                                } else if (userEmailId.isEmpty()) {
                                    Toast.makeText(MainActivity2.this, "Please enter a Email id", Toast.LENGTH_SHORT).show();

                                } else if (userPassword.isEmpty()) {
                                    Toast.makeText(MainActivity2.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                                } else if (Patterns.EMAIL_ADDRESS.matcher(userEmailId).matches()) {
                                    signInWithFirebase(userEmailId, userPassword);
                                } else {
                                    Toast.makeText(MainActivity2.this, "Please enter a valid email id", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
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
                    Intent intent = new Intent(MainActivity2.this, PostUnpostedArticles.class);
                    startActivity(intent);
                } else if (id == R.id.reviewArticleNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, ReviewUnpostedArticles.class);
                    startActivity(intent);
                } else if (id == R.id.deleteArticleNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, DeleteArticle.class);
                    startActivity(intent);
                } else if (id == R.id.removeReviewerNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, DeleteReviewer.class);
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
            emailId.setText("");
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

    private void signInWithFirebase(String userEmailId, String userPassword) {
        auth.signInWithEmailAndPassword(userEmailId,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    reference.child("UserType").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String role = snapshot.getValue().toString();
                            if(role.equalsIgnoreCase("Admin"))
                            {
                                Toast.makeText(MainActivity2.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                                FragmentManager fm = getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.content, new AddPostFragement()).addToBackStack("add post");
                                ft.commit();
                            }
                            else if(auth.getCurrentUser().isEmailVerified())
                            {
                                Toast.makeText(MainActivity2.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                                FragmentManager fm = getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.content, new AddPostFragement()).addToBackStack("add post");
                                ft.commit();
                            }
                            else {
                                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(MainActivity2.this, "Please verify your email.", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                else
                {
                    if(task.getException() instanceof FirebaseAuthInvalidUserException)
                    {
                        Toast.makeText(MainActivity2.this, "Invalid User", Toast.LENGTH_SHORT).show();
                    }
                    else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(MainActivity2.this, "Password is wrong", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}