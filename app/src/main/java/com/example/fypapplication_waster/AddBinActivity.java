package com.example.fypapplication_waster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fypapplication_waster.model.BinToBeSent;
import com.example.fypapplication_waster.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bin);
    }

    public void onAddBinClicked(View v) {
        Log.d("AddBinActivity", "button has been clicked");
        String binName = ((EditText) findViewById(R.id.add_bin_name)).getText().toString();
        String binLatitudeStr = ((EditText) findViewById(R.id.add_bin_latitude)).getText().toString();
        Double binLatitude = Double.valueOf(binLatitudeStr);
        String binLongitudeStr = ((EditText) findViewById(R.id.add_bin_longitude)).getText().toString();
        Double binLongitude = Double.valueOf(binLongitudeStr);
        String binPhoto = ((EditText) findViewById(R.id.add_bin_photo)).getText().toString();

        if (binName.equals("")) {
            binName = null;
        }
        if (binLatitude.equals("")) {
            binLatitude = null;
        }
        if (binLongitude.equals("")) {
            binLongitude = null;
        }
        if (binPhoto.equals("")) {
            binPhoto = null;
        }

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<BinToBeSent> call = service.addBin(new BinToBeSent(binName, binLatitude, binLongitude, binPhoto, null, null, null, null, null));
        call.enqueue(new Callback<BinToBeSent>() {
            @Override
            public void onResponse(Call<BinToBeSent> call, Response<BinToBeSent> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddBinActivity.this, "Bin added successfully!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BinToBeSent> call, Throwable t) {
                Log.d("AddBinActivity", "FUCK", t);
                Toast.makeText(AddBinActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
