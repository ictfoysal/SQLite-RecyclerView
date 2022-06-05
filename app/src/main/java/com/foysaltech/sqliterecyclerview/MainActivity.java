package com.foysaltech.sqliterecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    TextInputLayout name, contact, email;
    FloatingActionButton fb;
    Button sbmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (TextInputLayout) findViewById(R.id.nametext);
        contact = (TextInputLayout) findViewById(R.id.contacttext);
        email = (TextInputLayout) findViewById(R.id.emailtext);
        fb = (FloatingActionButton) findViewById(R.id.fbtn);
        sbmt = (Button) findViewById(R.id.sbmt_add);


        sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processinsert(name.getEditText().getText().toString(),
                        contact.getEditText().getText().toString(),
                        email.getEditText().getText().toString());
            }
        });


        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FetchData.class));
            }
        });


    }

    private void processinsert(String n, String c, String e) {
        String res = new DBManager(this).addrecord(n, c, e);
        name.getEditText().setText("");
        contact.getEditText().setText("");
        email.getEditText().setText("");
        Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
    }
}