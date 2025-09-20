package com.example.bmicalci;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    android.widget.Button mcalculatebmi;

    // Now we only need EditText for both display and input
    EditText mheightEditText, mweightEditText, mageEditText;

    ImageView mincrementage, mincrementweight, mdecrementage, mdecrementweight;
    SeekBar mseekbarforheight;
    RelativeLayout mmale, mfemale;


    int height = 170; // Set a default height
    int weight = 50;
    int age = 19;
    String typeofuser = "0";



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.history_menu) {
            showBMIHistory();
            return true;
        } else if (id == R.id.logout_menu) {
            showLogoutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showBMIHistory() {
        int userId = getIntent().getIntExtra("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getBmiHistory(userId);

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No BMI history found", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }

        StringBuilder historyBuilder = new StringBuilder();
        int count = 1;

        while (cursor.moveToNext()) {
            double bmi = cursor.getDouble(0);
            String date = cursor.getString(1);

            // Format date - remove time part
            String shortDate = date.length() > 10 ? date.substring(0, 10) : date;

            // Get BMI category
            String category = getBMICategory(bmi);

            historyBuilder.append(count + ". BMI: " + String.format("%.1f", bmi))
                    .append(" (" + category + ")")
                    .append("\n   Date: " + shortDate)
                    .append("\n\n");
            count++;
        }
        cursor.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your BMI History");
        builder.setMessage(historyBuilder.toString());
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmClearHistory(userId);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25.0) return "Normal";
        else if (bmi < 30.0) return "Overweight";
        else return "Obese";
    }

    private void confirmClearHistory(int userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clear History");
        builder.setMessage("Delete all BMI history? This cannot be undone.");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper db = new DatabaseHelper(MainActivity.this);
                db.clearBMIHistory(userId);
                Toast.makeText(MainActivity.this, "History cleared", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mcalculatebmi = findViewById(R.id.calculatebmi);
        mheightEditText = findViewById(R.id.height_edit_text);
        mweightEditText = findViewById(R.id.weight_edit_text);
        mageEditText = findViewById(R.id.age_edit_text);
        mincrementage = findViewById(R.id.incrementage);
        mincrementweight = findViewById(R.id.incrementweight);
        mdecrementage = findViewById(R.id.decrementage);
        mdecrementweight = findViewById(R.id.decrementweight);
        mseekbarforheight = findViewById(R.id.seekbarforheight);
        mmale = findViewById(R.id.male);
        mfemale = findViewById(R.id.female);




        // Set initial values on UI
        mheightEditText.setText(String.valueOf(height));
        mweightEditText.setText(String.valueOf(weight));
        mageEditText.setText(String.valueOf(age));
        mseekbarforheight.setProgress(height);
        mseekbarforheight.setMax(250);

        // Gender selection logic
        mmale.setOnClickListener(view -> {
            mmale.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.malefemalefocus));
            mfemale.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.malefemalenotfocus));
            typeofuser = "Male";
        });

        mfemale.setOnClickListener(view -> {
            mfemale.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.malefemalefocus));
            mmale.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.malefemalenotfocus));
            typeofuser = "Female";
        });

        // Height Logic (SeekBar and EditText)
        mseekbarforheight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    height = i;
                    mheightEditText.setText(String.valueOf(height));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mheightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().isEmpty()) {
                    try {
                        int value = Integer.parseInt(s.toString());
                        if (value >= 0 && value <= mseekbarforheight.getMax()) {
                            height = value;
                            mseekbarforheight.setProgress(height);
                        }
                    } catch (NumberFormatException e) {
                        // Handle invalid number format
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Weight Logic (Buttons and EditText)
        mincrementweight.setOnClickListener(view -> {
            weight++;
            mweightEditText.setText(String.valueOf(weight));
        });

        mdecrementweight.setOnClickListener(view -> {
            if (weight > 0) {
                weight--;
                mweightEditText.setText(String.valueOf(weight));
            }
        });

        mweightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().isEmpty()) {
                    try {
                        weight = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        // Handle invalid number
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Age Logic (Buttons and EditText)
        mincrementage.setOnClickListener(view -> {
            age++;
            mageEditText.setText(String.valueOf(age));
        });

        mdecrementage.setOnClickListener(view -> {
            if (age > 0) {
                age--;
                mageEditText.setText(String.valueOf(age));
            }
        });

        mageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().isEmpty()) {
                    try {
                        age = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        // Handle invalid number
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Calculate BMI button logic
        mcalculatebmi.setOnClickListener(view -> {


            if (typeofuser.equals("0")) {
                Toast.makeText(getApplicationContext(), "Gender Not Selected", Toast.LENGTH_SHORT).show();
            } else if (height <= 0) {
                Toast.makeText(getApplicationContext(), "Height is incorrect", Toast.LENGTH_SHORT).show();
            } else if (age <= 0) {
                Toast.makeText(getApplicationContext(), "Age is incorrect", Toast.LENGTH_SHORT).show();
            } else if (weight <= 0) {
                Toast.makeText(getApplicationContext(), "Weight is incorrect", Toast.LENGTH_SHORT).show();
            } else {

                //DEBUG Logs
                System.out.println("DEBUG - HEIGHT : "+height);
                System.out.println("DEBUG - WEIGHT : "+weight);



                int userId = getIntent().getIntExtra("USER_ID", -1);
                double bmiValue = weight / Math.pow(height / 100.0, 2);


                System.out.println("DEBUG - BMI :"+bmiValue); // DEBUG log


                DatabaseHelper db = new DatabaseHelper(this);
                db.saveBmi(userId, bmiValue);

                Intent intent = new Intent(MainActivity.this, bmi.class);
                intent.putExtra("gender", typeofuser);
                intent.putExtra("height", String.valueOf(height));
                intent.putExtra("weight", String.valueOf(weight));
                intent.putExtra("age", String.valueOf(age));
                intent.putExtra("bmi",String.valueOf(bmiValue));
                startActivity(intent);
            }
        });
    }
}