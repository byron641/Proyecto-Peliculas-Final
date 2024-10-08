package com.example.aplicacionpeliculas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ImageButton googleSignInButton;
    private FloatingActionButton fabCreateUser; // El botón flotante

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Configuración de Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Verifica que sea el ID correcto
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton = findViewById(R.id.btn_google);
        fabCreateUser = findViewById(R.id.btn_nuevo); // Referencia al FAB

        // Inicio de sesión con Google
        googleSignInButton.setOnClickListener(v -> signIn());

        // Acción del FAB para redirigir a la nueva actividad de creación de usuario
        fabCreateUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Create.class); // Crear el intent
            startActivity(intent); // Iniciar la nueva actividad
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
                Log.w("MainActivity", "Error en el inicio de sesión con Google: " + e.getMessage(), e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Login Exitoso", Toast.LENGTH_SHORT).show();
                        Intent main = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(main);
                    } else {
                        Toast.makeText(MainActivity.this, "Error en el login", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}