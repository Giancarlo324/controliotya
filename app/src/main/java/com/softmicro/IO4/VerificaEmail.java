package com.softmicro.IO4;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class VerificaEmail extends AppCompatActivity implements
        View.OnClickListener {

    TextView lblEmail, sendAgain;
    Button btnrefresh;
    //Firebase
    private FirebaseAuth mAuth;
    //Alert
    android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifica_email);

        mAuth = FirebaseAuth.getInstance();

        lblEmail = (TextView)findViewById(R.id.lbl_text_send);
        btnrefresh = (Button)findViewById(R.id.btn_register);


        lblEmail.setText("El correo de verificación se ha enviado a su correo: " +mAuth.getCurrentUser().getEmail() + ". Pulsa aquí para reenviar el correo");
        mAuth.getCurrentUser().sendEmailVerification();
        String asfs = "<font color =#0000f>Enviar de nuevo </font>";

        //Dialog
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando...")
                .build();

        //
        SpannableString mensaje = new SpannableString("Volver a enviar.");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#0000ff"));
        mensaje.setSpan(colorSpan, 0, 16, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        lblEmail.append(mensaje);
        //
        lblEmail.append("\n\nRevisa tu carpeta de SPAM, si no encuentras el correo en la ventana principal.");
        //Escuchador
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_cancelar).setOnClickListener(this);
        findViewById(R.id.lbl_text_send).setOnClickListener(this);
    }

    private void verifyEmail() {
        dialog.show();
        mAuth.getCurrentUser()
                .reload()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful() && mAuth.getCurrentUser().isEmailVerified())
                {
                    dialog.dismiss();
                    Toast.makeText(VerificaEmail.this, "Email verificado con éxito.",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerificaEmail.this, CrearPass.class));
                    finish();
                }
                else if(!mAuth.getCurrentUser().isEmailVerified())
                {
                    dialog.dismiss();
                    Toast.makeText(VerificaEmail.this, "Verifica tu correo para continuar.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.dismiss();
                    Toast.makeText(VerificaEmail.this, "Ocurrió un error.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.dismiss();
    }

    private void resendEmail() {
        mAuth.getCurrentUser()
                .sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(VerificaEmail.this, "Email reenviado.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cancelarReg() {
        mAuth.signOut();
        startActivity(new Intent(VerificaEmail.this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                verifyEmail();
                break;
            case R.id.lbl_text_send:
                resendEmail();
                break;
            case R.id.btn_cancelar:
                cancelarReg();
                break;
            //
        }
    }

    //Para detectar cuando pulsa hacia atrás.
    @Override
    public void onBackPressed() {
        AlertDialog.Builder mensaje = new AlertDialog.Builder(this);
        mensaje.setTitle("¿Seguro que quieres salir?");
        mensaje.setCancelable(true);
        mensaje.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        mensaje.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mensaje.show();
    }
}
