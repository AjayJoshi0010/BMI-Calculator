package com.example.bmicalci;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.DecimalFormat;

public class bmi extends AppCompatActivity {
    Button mrecalculatebmi;

    TextView mbmidisplay, mbmicategory, mgender;
    Intent intent;
    ImageView mimageview;
    String mbmi;
    float bmi;
    String height;
    String weight;
    float mheight, mweight;
    RelativeLayout mbackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        // Check if ActionBar exists before using it
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">Result</font>"));
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#1E1D1D"));
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
        }

        intent = getIntent();

        // Initialize all views
        mbmidisplay = findViewById(R.id.bmidisplay);
        mbmicategory = findViewById(R.id.bmicategory);
        mgender = findViewById(R.id.genderdisplay);
        mbackground = findViewById(R.id.contentlayout);
        mimageview = findViewById(R.id.bmiimageview);
        mrecalculatebmi = findViewById(R.id.recalculatebmi);

        // Get data from intent
        height = intent.getStringExtra("height");
        weight = intent.getStringExtra("weight");
        String bmiString = intent.getStringExtra("bmi");

        // DEBUG Logs
        System.out.println("DEBUG - RECEIVED HEIGHT: "+height);
        System.out.println("DEBUG - RECEIVED WEIGHT: "+weight);
        System.out.println("DEBUG - RECEIVED BMI: "+bmiString);



        // Parse values safely
        try {
            bmi = Float.parseFloat(bmiString);
            System.out.println("DEBUG - PARSED BMI: "+bmi);

            // Format BMI to 2 decimal places
            DecimalFormat df = new DecimalFormat("#.##");
            mbmi = df.format(bmi);
            System.out.println("DEBUG - FORMATTED BMI : "+mbmi);

            // Set BMI category and styling
            setBMICategory();

        } catch (NumberFormatException e) {
            // Handle parsing error
            mbmidisplay.setText("Error");
            mbmicategory.setText("Invalid Input");
        }

        // Display results
        mgender.setText(intent.getStringExtra("gender"));
        mbmidisplay.setText(mbmi);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set click listener for recalculate button
        mrecalculatebmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(bmi.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setBMICategory() {
        if (bmi < 16) {
            mbmicategory.setText("Severely Underweight");
            mbackground.setBackgroundColor(Color.RED);
            mimageview.setImageResource(R.drawable.crosss);
        } else if (bmi >= 16 && bmi < 17) {  // Fixed condition
            mbmicategory.setText("Moderately Underweight");
            mbackground.setBackgroundColor(Color.RED);
            mimageview.setImageResource(R.drawable.warning);
        } else if (bmi >= 17 && bmi < 18.5) {  // Fixed condition and BMI range
            mbmicategory.setText("Underweight");
            mbackground.setBackgroundColor(Color.RED);
            mimageview.setImageResource(R.drawable.warning);
        } else if (bmi >= 18.5 && bmi < 25) {  // Fixed condition
            mbmicategory.setText("Normal Weight");
            mbackground.setBackgroundColor(Color.GREEN);  // Added green for normal
            mimageview.setImageResource(R.drawable.ok);
        } else if (bmi >= 25 && bmi < 30) {  // Fixed condition and range
            mbmicategory.setText("Overweight");
            mbackground.setBackgroundColor(Color.YELLOW);  // Better color choice
            mimageview.setImageResource(R.drawable.warning);
        } else {  // BMI >= 30
            mbmicategory.setText("Obese");
            mbackground.setBackgroundColor(Color.RED);
            mimageview.setImageResource(R.drawable.crosss);
        }
    }
}