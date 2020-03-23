package com.example.fypapplication_waster;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.fypapplication_waster.retrofit.model.BinToBeSent;
import com.example.fypapplication_waster.util.CameraUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddBinDialogFragment extends DialogFragment {
    private Double newBinLatitude, newBinLongitude;
    private Uri photoUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newBinLatitude = getArguments().getDouble("latitude");
        newBinLongitude = getArguments().getDouble("longitude");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        View rootView = inflater.inflate(R.layout.add_bin_modal, container, false);
        final TextView binName = rootView.findViewById(R.id.txtBinName);
        final CheckBox cbxGlass = rootView.findViewById(R.id.cbxGlass);
        final CheckBox cbxPlastic = rootView.findViewById(R.id.cbxPlastic);
        final CheckBox cbxMetal = rootView.findViewById(R.id.cbxMetal);
        
        Spinner spinner = rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array andAsy a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.floor_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        LinearLayout binAccessLayout = rootView.findViewById(R.id.linearLayoutBinAccess);

        EditText txtBuildingName = rootView.findViewById(R.id.txtBuildingName);

        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.accessibilityRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == R.id.accessRadioBtnInside) {
                    binAccessLayout.setVisibility(View.VISIBLE);
                }
                else {
                    if (binAccessLayout.getVisibility() != View.GONE) {
                        binAccessLayout.setVisibility(View.GONE);
                    }
                }
            }
        });


        final EditText binPrice = rootView.findViewById(R.id.txtPrice);

//        mBuilder.setView(rootView);
//        dialog = mBuilder.create();

        Button btnTakePhoto = rootView.findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntentAndGetUri();
            }
        });

        Button btnCancel = rootView.findViewById(R.id.btnCancelAddBin);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnSubmit = rootView.findViewById(R.id.btnSubmitBin);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate fields
                if (!TextUtils.isEmpty(binName.getText()) && !TextUtils.isEmpty(binPrice.getText())) {
                    ArrayList materials = new ArrayList<>();

                    if (cbxGlass.isChecked()) { materials.add("glass"); }
                    if (cbxMetal.isChecked()) { materials.add("metal"); }
                    if (cbxPlastic.isChecked()) { materials.add("plastic"); }

                    boolean isInside;
                    String buildingFloor;
                    String buildingName;

                    //check radio buttons
                    if (radioGroup.getCheckedRadioButtonId() == R.id.accessRadioBtnInside) {
                        isInside = true;
                        buildingFloor = spinner.getSelectedItem().toString();
                        buildingName = txtBuildingName.getText().toString();
                    }
                    else {
                        isInside = false;
                        buildingFloor = null;
                        buildingName = null;
                    }

                    // POST bin to server
//                    Call<ResponseBody> call = service.addBin(new BinToBeSent(binName.getText().toString(), newBinLatitude, newBinLongitude,
//                            newBinStorageRef.toString(), materials,
//                            null, null, Double.valueOf(binPrice.getText().toString()),
//                            null, isInside, buildingName, buildingFloor));
//
//                    call.enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            Log.d("MapsActivity -> Add", response.message());
//                            dismiss();
//                            if (response.isSuccessful()) {
//                                Toast.makeText(getContext(), "Bin added successfully!", Toast.LENGTH_SHORT).show();
//                                Log.d("AddBinActivity", "Bin Added Successfully");
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
//                            Log.e("AddBinActivity", "Connection Failed\n" + t.getMessage());
//                        }
//                    });
                }

                else {
                    Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        CameraUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private Uri dispatchTakePictureIntentAndGetUri() {
        photoUri = CameraUtils.dispatchTakePictureIntentAndGetUri(this);
        return photoUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap imageBitmap = CameraUtils.onCameraActivityResult(this, requestCode, resultCode, data, photoUri);

        if (imageBitmap != null) { // user has not cancelled image capture
             // do Firebase upload stuff with picture
            // TODO refactor Firebase stuff into utils
//            byte[] encodedBitmap = getEncodedBitmap(imageBitmap);
//            newBinStorageRef = uploadEncodedImageToFirebase(encodedBitmap);
        }
    }

}
