package com.example.hansen.sqlitetest;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CurrencyEditActivity extends AppCompatActivity {

    EditText EditUSD;
    EditText EditIDR;
    Button saveBtn;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_edit);

        EditUSD = (EditText) findViewById(R.id.currencyEditUSD);
        EditIDR = (EditText) findViewById(R.id.currencyEditIDR);
        saveBtn = (Button) findViewById(R.id.currencySaveButton);

        dbHelper = new DBHelper(this);

        for (int id = 1; id<=2; id++) {
            Cursor cursor = dbHelper.getCurrency(id);
            cursor.moveToFirst();
            int value = cursor.getInt(cursor.getColumnIndex(DBHelper.CURRENCY_COLUMN_VALUE));

            switch (id){
                case 1:
                    EditUSD.setText(String.valueOf(value));
                    break;
                case 2:
                    EditIDR.setText(String.valueOf(value));
                    break;
            }
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.updateCurrency(Integer.parseInt(EditUSD.getText().toString()),
                        Integer.parseInt(EditIDR.getText().toString()));
                Toast.makeText(getApplicationContext(), "Update Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CurrencyEditActivity.class);
                startActivity(intent);
            }
        });
    }
}
