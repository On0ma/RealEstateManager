package com.openclassrooms.realestatemanager.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.openclassrooms.realestatemanager.databinding.ActivityLoanBinding;

public class LoanActivity extends AppCompatActivity {

    private ActivityLoanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoanBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.loanResult.setVisibility(View.GONE);
        binding.loanResultLabel.setVisibility(View.GONE);

        binding.loanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields()) {
                    float loanAmount = Float.parseFloat(binding.loanAmount.getText().toString());
                    float loanInterest = Float.parseFloat(binding.loanInterest.getText().toString());
                    int loanLength = Integer.parseInt(binding.loanLength.getText().toString());
                    int result = calculateLoan(loanAmount, loanInterest, loanLength);
                    if (result != 0) {
                        binding.loanResult.setVisibility(View.VISIBLE);
                        binding.loanResultLabel.setVisibility(View.VISIBLE);
                        binding.loanResult.setText(String.valueOf(result) + "€");
                    }
                }
            }
        });
    }

    private boolean checkFields() {
        boolean isChecked = true;
        // MONTANT
        if (TextUtils.isEmpty(binding.loanAmount.getText())) {
            binding.loanAmount.setError("Le montant est requis");
            isChecked = false;
        }
        // TAUX
        if (TextUtils.isEmpty(binding.loanInterest.getText())) {
            binding.loanInterest.setError("Le taux d'intérêt est requis");
            isChecked = false;
        }
        // DUREE
        if (TextUtils.isEmpty(binding.loanLength.getText())) {
            binding.loanLength.setError("La durée de remboursement est requise");
            isChecked = false;
        }
        return isChecked;
    }

    public static int calculateLoan(float amount, float interest, int length) {
        double periodicRate = (interest / 100) / 12;
        double monthlyPayment = (amount * periodicRate) / (1-Math.pow(1+periodicRate, -length));
        return (int) Math.round(monthlyPayment);
    }
}
