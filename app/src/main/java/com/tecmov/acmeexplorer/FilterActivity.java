package com.tecmov.acmeexplorer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    EditText textViewDateIn, textViewDateOut, textViewHour, textViewPriceMax, textViewPriceMin;
    int year, month, day, hour, minute;
    Button buttonFilterDelete;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sharedDateIn, sharedDateOut, sharedPriceMin, sharedPriceMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        textViewDateIn = findViewById(R.id.editTextDateIn);
        textViewDateOut = findViewById(R.id.editTextDateOut);
        textViewPriceMax = findViewById(R.id.editTextPriceMax);
        textViewPriceMin = findViewById(R.id.editTextPriceMin);
        buttonFilterDelete = findViewById(R.id.buttonFilterDelete);

        // Get shared preferences
        sharedPreferences = getSharedPreferences(Constants.filterPreferences, MODE_PRIVATE);
        sharedDateIn = sharedPreferences.getString(Constants.startedDate, "StartDate");
        sharedDateOut = sharedPreferences.getString(Constants.finishedDate, "FinishDate");
        sharedPriceMin = sharedPreferences.getString(Constants.priceMin, "PriceMin");
        sharedPriceMax = sharedPreferences.getString(Constants.priceMax, "PriceMax");

        // Set share preferences
        textViewDateIn.setText(sharedDateIn);
        textViewDateOut.setText(sharedDateOut);
        textViewPriceMax.setText(sharedPriceMax);
        textViewPriceMin.setText(sharedPriceMin);
    }

    public void setPrice(View view) {

        if (textViewPriceMax.getText().toString().equals("PriceMax")){
            textViewPriceMax.setText("0.0");
        }
        if (textViewPriceMin.getText().toString().equals("PriceMin")){
            textViewPriceMin.setText("0.0");
        }

    }

    public void setDateIn(View view) {

        if (textViewDateIn.getText().toString().equals("StartDate")) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            String split[] = textViewDateIn.getText().toString().split("/");
            day = Integer.valueOf(split[0]);
            month = Integer.valueOf(split[1]) - 1;
            year = Integer.valueOf(split[2]);
        }

        DatePickerDialog pickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                        textViewDateIn.setText(dd + "/" + (mm + 1) + "/" + yy);
                    }
                }, year, month, day);
        pickerDialog.show();

    }

    public void setDateOut(View view) {

        if (textViewDateOut.getText().toString().equals("FinishDate")) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            String split[] = textViewDateOut.getText().toString().split("/");
            day = Integer.valueOf(split[0]);
            month = Integer.valueOf(split[1]) - 1;
            year = Integer.valueOf(split[2]);
        }

        DatePickerDialog pickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                        textViewDateOut.setText(dd + "/" + (mm + 1) + "/" + yy);
                    }
                }, year, month, day);
        //pickerDialog.getDatePicker().setMinDate(textViewDateIn);
        pickerDialog.show();
    }

    public void setTime(View view) {
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog pickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hh, int m) {
                        textViewHour.setText(hour + ":" + minute);
                    }
                }, hour, minute, true);

        pickerDialog.show();
    }

    public void FilterView(View view) {
        sharedPreferences = getSharedPreferences(Constants.filterPreferences, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Intent intent = new Intent();

        // return values with intent
        intent.putExtra("filter_startedDate", textViewDateIn.getEditableText().toString());
        intent.putExtra("filter_finishedDate", textViewDateOut.getEditableText().toString());
        intent.putExtra("filter_priceMin", textViewPriceMin.getEditableText().toString());
        intent.putExtra("filter_priceMax", textViewPriceMax.getEditableText().toString());

        // save shared preferences
        editor.putString(Constants.startedDate, textViewDateIn.getEditableText().toString());
        editor.putString(Constants.finishedDate, textViewDateOut.getEditableText().toString());
        editor.putString(Constants.priceMin, textViewPriceMin.getEditableText().toString());
        editor.putString(Constants.priceMax, textViewPriceMax.getEditableText().toString());
        editor.apply();

        setResult(RESULT_OK, intent);
        finish();
    }

    public void DeletePreferences(View view) {
        sharedPreferences = getSharedPreferences(Constants.filterPreferences, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString(Constants.startedDate, "StartDate");
        editor.putString(Constants.finishedDate, "FinishDate");
        editor.putString(Constants.priceMin, "PriceMin");
        editor.putString(Constants.priceMax, "PriceMax");
        editor.apply();

        textViewDateIn.setText("StartDate");
        textViewDateOut.setText("FinishDate");
        textViewPriceMax.setText("PriceMax");
        textViewPriceMin.setText("PriceMin");
    }

}