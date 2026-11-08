package com.example.excessbaggagefeeestimator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLog";

    private TextInputEditText etWeight, etRate;
    private Spinner spTicketClass;
    private TextView tvCheckedWeight, tvTicketClassSummary, tvExcessWeight, tvExcessCharge, tvTaxAmount, tvTotalPayable;
    private Button btnCalculate, btnClear;

    private static final String GITHUB_URL = "https://github.com/ainSafiyya-a11y/ExcessBaggageFeeEstimator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make status bar transparent and ensure layout draws behind it cleanly
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        setContentView(R.layout.activity_main);

        // Configure ActionBar Title and enable Back Arrow Button (←)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Excess Baggage Fee Estimator");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inputs & Controls
        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);
        spTicketClass = findViewById(R.id.spTicketClass);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnClear = findViewById(R.id.btnClear);

        // Summary Outputs
        tvCheckedWeight = findViewById(R.id.tvCheckedWeight);
        tvTicketClassSummary = findViewById(R.id.tvTicketClassSummary);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvExcessCharge = findViewById(R.id.tvExcessCharge);
        tvTaxAmount = findViewById(R.id.tvTaxAmount);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);

        btnCalculate.setOnClickListener(v -> calculateFee());
        btnClear.setOnClickListener(v -> clearFields());
    }

    private void calculateFee() {
        String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
        String rateStr = etRate.getText() != null ? etRate.getText().toString().trim() : "";

        if (weightStr.isEmpty()) {
            etWeight.setError("Please enter baggage weight");
            etWeight.requestFocus();
            return;
        }

        if (rateStr.isEmpty()) {
            etRate.setError("Please enter excess rate per kg");
            etRate.requestFocus();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double rate = Double.parseDouble(rateStr);

            if (weight < 0) {
                etWeight.setError("Weight cannot be negative");
                etWeight.requestFocus();
                return;
            }

            if (rate < 0) {
                etRate.setError("Rate cannot be negative");
                etRate.requestFocus();
                return;
            }

            int selectedClassPosition = spTicketClass.getSelectedItemPosition();
            double allowance = (selectedClassPosition == 0) ? 20.0 : 40.0;
            String className = (selectedClassPosition == 0) ? "Economy Class" : "Business Class";

            double excessWeight = Math.max(0.0, weight - allowance);
            double excessCharge = excessWeight * rate;
            double taxAmount = 0.08 * excessCharge; // Corrected multiplication syntax

            double totalPayable = excessCharge + taxAmount;

            tvCheckedWeight.setText(String.format(Locale.getDefault(), "%.2f kg", weight));
            tvTicketClassSummary.setText(className);
            tvExcessWeight.setText(String.format(Locale.getDefault(), "%.2f kg", excessWeight));
            tvExcessCharge.setText(String.format(Locale.getDefault(), "RM %.2f", excessCharge));
            tvTaxAmount.setText(String.format(Locale.getDefault(), "RM %.2f", taxAmount));
            tvTotalPayable.setText(String.format(Locale.getDefault(), "RM %.2f", totalPayable));

            Toast.makeText(this, "Calculation Complete!", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid numeric input: " + e.getMessage(), e);
            Toast.makeText(this, "Error: Please enter valid numbers.", Toast.LENGTH_LONG).show();
        }
    }

    private void clearFields() {
        etWeight.setText("");
        etRate.setText("");
        spTicketClass.setSelection(0);

        tvCheckedWeight.setText("0.00 kg");
        tvTicketClassSummary.setText("Economy Class");
        tvExcessWeight.setText("0.00 kg");
        tvExcessCharge.setText("RM 0.00");
        tvTaxAmount.setText("RM 0.00");
        tvTotalPayable.setText("RM 0.00");

        etWeight.setError(null);
        etRate.setError(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(welcomeIntent);
            finish();
            return true;
        } else if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Excess Baggage Fee Estimator App");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out the app repo: " + GITHUB_URL);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            return true;
        } else if (id == R.id.action_about) {
            Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}