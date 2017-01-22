package com.example.hansen.sqlitetest;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateOrEditActivity extends AppCompatActivity implements View.OnClickListener {

    private DBHelper dbHelper;
    EditText titleEdit;
    EditText valueEdit;
    EditText dateEdit;
    Spinner currencyEdit;
    Spinner categoryEdit;

    Button saveButton;
    Button editButton;
    Button deleteButton;
    LinearLayout buttonLayout;

    int expenseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        expenseID = getIntent().getIntExtra(MainActivity.KEY_EXTRA_ID, 0);

        setContentView(R.layout.activity_create_or_edit);

        titleEdit = (EditText) findViewById(R.id.titleEdit);
        valueEdit = (EditText) findViewById(R.id.valueEdit);
        dateEdit = (EditText) findViewById(R.id.dateEdit);
        currencyEdit = (Spinner) findViewById(R.id.currencySpinner);
        categoryEdit = (Spinner) findViewById(R.id.categorySpinner);

        dateEdit.setOnClickListener(this);
        dateEdit.setFocusableInTouchMode(false);
        dateEdit.setFocusable(false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencyEdit.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.category_list, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryEdit.setAdapter(adapter1);

        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        dbHelper = new DBHelper(this);

        if(expenseID > 0){
            saveButton.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);

            Cursor rs = dbHelper.getExpense(expenseID);
            rs.moveToFirst();
            String expenseTitle = rs.getString(rs.getColumnIndex(DBHelper.EXPENSE_COLUMN_TITLE));
            int expenseValue = rs.getInt(rs.getColumnIndex(DBHelper.EXPENSE_COLUMN_VALUE));
            String expenseDate = rs.getString(rs.getColumnIndex(DBHelper.EXPENSE_COLUMN_DATE));

            titleEdit.setText(expenseTitle);
            titleEdit.setFocusable(false);
            titleEdit.setClickable(false);

            valueEdit.setText(String.valueOf(expenseValue));
            valueEdit.setFocusable(false);
            valueEdit.setClickable(false);

            dateEdit.setText(expenseDate);
            dateEdit.setFocusable(false);
            dateEdit.setClickable(false);

            //currencyEdit.setFocusable(false);
            //currencyEdit.setClickable(false);
            currencyEdit.setSelection(adapter.getPosition(rs.getString(rs.getColumnIndex(DBHelper.EXPENSE_COLUMN_CURR))));
            currencyEdit.setEnabled(false);

            categoryEdit.setSelection(adapter1.getPosition(rs.getString(rs.getColumnIndex(DBHelper.EXPENSE_COLUMN_CAT))));
            categoryEdit.setEnabled(false);

            if(!rs.isClosed()){
                rs.close();
            }
        }else{
            SimpleDateFormat today = new SimpleDateFormat("d MMMM yyyy", Locale.US);
            dateEdit.setText(today.format(new Date()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editButton:
                saveButton.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.GONE);

                titleEdit.setEnabled(true);
                titleEdit.setFocusableInTouchMode(true);
                titleEdit.setClickable(true);

                valueEdit.setEnabled(true);
                valueEdit.setFocusableInTouchMode(true);
                valueEdit.setClickable(true);

                dateEdit.setEnabled(true);
                dateEdit.setFocusableInTouchMode(false);
                dateEdit.setFocusable(false);
                dateEdit.setClickable(true);

                currencyEdit.setEnabled(true);
                categoryEdit.setEnabled(true);
                return;

            case R.id.saveButton:
                saveExpense();
                return;

            case R.id.deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Confirm expense deletion")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteExpense(expenseID);
                                Toast.makeText(getApplicationContext(), "Delete Success",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),
                                        MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog for cancel
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Delete Expense?");
                d.show();
                return;

            case R.id.dateEdit:
                if(saveButton.isShown()) {
                    showDatePickerDialog();
                }
                return;
        }
    }

    public void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateOrEditActivity.this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.US);
                dateEdit.setText(dateFormat.format(newDate.getTime()));
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void saveExpense() {
        if(expenseID > 0){
            dbHelper.updateExpense(expenseID, titleEdit.getText().toString(),
                    Integer.parseInt(valueEdit.getText().toString()),
                    currencyEdit.getSelectedItem().toString(),
                    categoryEdit.getSelectedItem().toString(),
                    dateEdit.getText().toString());
            Toast.makeText(getApplicationContext(), "Update Success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{
            dbHelper.insertExpense(titleEdit.getText().toString(),
                    Integer.parseInt(valueEdit.getText().toString()),
                    currencyEdit.getSelectedItem().toString(),
                    categoryEdit.getSelectedItem().toString(),
                    dateEdit.getText().toString());
            Toast.makeText(getApplicationContext(), "Insert Success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
