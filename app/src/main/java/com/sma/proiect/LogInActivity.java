package com.sma.proiect;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        EditText email = findViewById(R.id.emailValue);
        EditText password = findViewById(R.id.passwordValue);
        Button bLogIn = findViewById(R.id.bLogIn);

        bLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("email", email.getText().toString());
                intent.putExtra("password", password.getText().toString());
                setResult(MainActivity.SI_CODE, intent);
                finish();
            }
        });
    }

}