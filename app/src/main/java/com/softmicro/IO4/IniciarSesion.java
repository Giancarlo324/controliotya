package com.softmicro.IO4;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;


public class IniciarSesion extends AppCompatActivity implements
        View.OnClickListener {

    //Toolbar
    private Toolbar toolbar;
    //Variables
    EditText mClave, mCorreo;
    TextInputLayout mIngPass, mIngCorreo;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "";
    //Detectar usuario.
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    public AlertDialog dialog; // let this be public

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        //Toolbar back
        toolbar = (Toolbar) findViewById(R.id.toolbarSignin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIngCorreo = (TextInputLayout) findViewById(R.id.ingEmail);
        mCorreo = (EditText)findViewById(R.id.txt_correo);
        mClave = (EditText)findViewById(R.id.txt_password);
        mIngPass = (TextInputLayout)findViewById(R.id.ingPass);
        mIngPass.setErrorEnabled(true);
        mIngCorreo.setErrorEnabled(true);
        //Alert
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando...")
                .build();

        // Escuchador del botón de Google y demás
        findViewById(R.id.sign_in_button).setOnClickListener(this);//Google.
        findViewById(R.id.btn_signin).setOnClickListener(this);//Inicio Sesión por correo.
        findViewById(R.id.btnRegister).setOnClickListener(this);//Register
        findViewById(R.id.btnRecoveryPass).setOnClickListener(this);//Recuperar Contraseña lblSignUp

        // Configuración de Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
    }

    //Autenticación con Google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    //Autenticación Google Firebase.
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        //Ingresó con Google.
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    //Ingreso por Google.
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    //Igreso por correo.
    private void signInEmailPass() {
        dialog.show();
        //Declaro variables donde guardaré la información escrita por el usuario.
        String email, password;
        email = mCorreo.getText().toString();
        password = mClave.getText().toString();
        //Verificar que los campos no estén vacíos.
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(IniciarSesion.this, "Los campos no pueden estar vacíos.",
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        //Patrón para validar el email.
        Pattern patternEmail = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matherEmail = patternEmail.matcher(email);

        //Verificacion del correo.
        if(!matherEmail.find())
        {
            Toast.makeText(IniciarSesion.this, "Ingrese un correo válido.",
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
        //Iniciar sesión si los datos ingresados son correctos.
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(IniciarSesion.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Si pasa hasta aquí, el usuario inició sesión correctamente.
                            Log.d(TAG, "Inicio de sesión exitoso");
                            Toast.makeText(IniciarSesion.this, "Bienvenido.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            dialog.dismiss();
                        }
                        else
                        {
                            dialog.dismiss();
                            findViewById(R.id.btnRecoveryPass).setVisibility(View.VISIBLE);
                            mIngCorreo.setError(" ");
                            mIngPass.setError("¡Error de cuenta o contraseña!");
                            //Si pasa hasta aquí, hubo error al iniciar sesión.
                            Log.w(TAG, "Fallo al iniciar sesión", task.getException());
                            Toast.makeText(IniciarSesion.this, "Error al iniciar sesión, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        dialog.dismiss();
    }
    //Función que retrocede.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(IniciarSesion.this, MainActivity.class));
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button://Google
                signIn();
                break;
            case R.id.btn_signin://Email
                signInEmailPass();
                break;
            case R.id.btnRecoveryPass://Recu pass
                startActivity(new Intent(IniciarSesion.this, RecuperarClave.class));
                finish();
                break;
            case R.id.btnRegister://Registrarse
                startActivity(new Intent(IniciarSesion.this, RegistrarUsuario.class));
                finish();
                break;
            // case R.id.lblSignUp://Botón volver a registrarse
            //     startActivity(new Intent(IniciarSesion.this, RegistrarUsuario.class));
            //     finish();
            //     break;//lblSignUp
        }
    }
    private void updateUI(FirebaseUser user) {
        if(user!=null)
        {
            startActivity(new Intent(IniciarSesion.this, MainActivity.class));
            finish();
        }
    }
}