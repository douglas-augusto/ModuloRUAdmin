package com.douglas.ufpi.moduloruadmin.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.douglas.ufpi.moduloruadmin.R;
import com.douglas.ufpi.moduloruadmin.config.ConfiguracaoFirebase;
import com.douglas.ufpi.moduloruadmin.model.Funcionario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FuncLoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button botaoLogar;
    private Funcionario funcionario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_func_login);

        verificarUsuarioLogado();

        email = findViewById(R.id.campoEmailFuncionario);
        senha = findViewById(R.id.campoSenhaFuncionario);
        botaoLogar = findViewById(R.id.botaoEntrarFuncionario);

        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty() || senha.getText().toString().isEmpty()) {
                    Toast.makeText(FuncLoginActivity.this, "Digite seu email e senha!", Toast.LENGTH_LONG).show();
                }else if(email.getText().toString().equals("admin@admin.com")){
                    funcionario = new Funcionario();
                    funcionario.setEmail(email.getText().toString());
                    funcionario.setSenha(senha.getText().toString());
                    final ProgressDialog dialog = new ProgressDialog(FuncLoginActivity.this);
                    dialog.setMessage("Carregando... aguarde");
                    dialog.setIndeterminate(false);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setCancelable(true);
                    dialog.show();
                    validarLogin();
                }else{
                    Toast.makeText(FuncLoginActivity.this, "Essa conta não possue acesso ao sistema!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                funcionario.getEmail(),
                funcionario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    abrirTelaPrincipal();
                }else{
                    Toast.makeText(FuncLoginActivity.this, "Erro ao fazer login", Toast.LENGTH_LONG).show();
                }

            }
        }) ;
    }

    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if( autenticacao.getCurrentUser() != null ){
            abrirTelaPrincipal();
        }
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(FuncLoginActivity.this, MainFuncActivity.class);
        startActivity(intent);
        finish();
    }

}
