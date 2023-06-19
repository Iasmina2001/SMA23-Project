package com.sma.proiect;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView firstName = findViewById(R.id.firstNameValue);
        TextView lastName = findViewById(R.id.lastNameValue);
        EditText email = findViewById(R.id.emailValue);
        Spinner sUserType = findViewById(R.id.sUserValue);
        EditText password = findViewById(R.id.passwordValue);
        EditText reenteredPassword = findViewById(R.id.reenterPasswordValue);
        Button bCreateAccount = findViewById(R.id.bCreateAccount);

        String[] userTypes = new String[]{"reader", "librarian"};
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, userTypes);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sUserType.setAdapter(sAdapter);

        bCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("first name", firstName.getText().toString());
                intent.putExtra("last name", lastName.getText().toString());
                intent.putExtra("email", email.getText().toString());
                intent.putExtra("user type", sUserType.getSelectedItem().toString());
                intent.putExtra("password", password.getText().toString());
                intent.putExtra("password2", reenteredPassword.getText().toString());
                setResult(MainActivity.SU_CODE, intent);
                finish();
            }
        });
    }
}