package com.example.sigapk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class optionMenu extends AppCompatActivity implements View.OnClickListener {

    Button btnPerson, btnInstitution;
    Intent ir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_menu);

        btnPerson = findViewById(R.id.btnPerson);
        btnPerson.setOnClickListener(this);
        btnInstitution = findViewById(R.id.btnInstitution);
        btnInstitution.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPerson:
                ir = new Intent(optionMenu.this, Person.class);
                startActivity(ir);
                break;
            case R.id.btnInstitution:
                ir = new Intent(optionMenu.this, Institution.class);
                startActivity(ir);
                break;
        }
    }
}