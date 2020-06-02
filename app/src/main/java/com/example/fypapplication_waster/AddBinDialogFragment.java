package com.example.fypapplication_waster;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.DialogFragment;

import com.example.fypapplication_waster.util.CameraUtils;
import com.example.fypapplication_waster.util.Constants;
import com.example.fypapplication_waster.util.FirebaseUtils;
import com.example.fypapplication_waster.util.RetrofitUtils;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AddBinDialogFragment extends DialogFragment {
    private Double newBinLatitude, newBinLongitude;
    private Uri photoUri;
    private StorageReference binImgStorageRef;

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
        final CheckBox cbxClearGlass = rootView.findViewById(R.id.cbxClearGlass);
        final CheckBox cbxCardboard = rootView.findViewById(R.id.cbxCardboard);
        final CheckBox cbxAABattery = rootView.findViewById(R.id.cbxAABattery);
        final CheckBox cbxDrinkCan = rootView.findViewById(R.id.cbxDrinkCan);
        final CheckBox cbxPlasticBottle = rootView.findViewById(R.id.cbxPlasticBottle);
        final CheckBox cbxPlasticFoodWrappers = rootView.findViewById(R.id.cbxPlasticFoodWrapper);

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

        Button btnTakePhoto = rootView.findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoUri = CameraUtils.dispatchTakePictureIntentAndGetUri(AddBinDialogFragment.this);
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
                    String hours = "";
                    String owner = null;

                    ArrayList materials = new ArrayList<>();

                    if (cbxClearGlass.isChecked()) { materials.add(Constants.CLEAR_GLASS); }
                    if (cbxCardboard.isChecked()) { materials.add(Constants.CARDBOARD); }
                    if (cbxAABattery.isChecked()) { materials.add(Constants.AA_BATTERY); }
                    if (cbxDrinkCan.isChecked()) { materials.add(Constants.DRINK_CAN); }
                    if (cbxPlasticBottle.isChecked()) { materials.add(Constants.PLASTIC_BOTTLE); }
                    if (cbxPlasticFoodWrappers.isChecked()) { materials.add(Constants.PLASTIC_FOOD_WRAPPER); }

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
                    RetrofitUtils.addBin(AddBinDialogFragment.this, binName.getText().toString(), newBinLatitude, newBinLongitude,
                            binImgStorageRef.toString(), materials, owner,
                            Double.valueOf(binPrice.getText().toString()),
                            hours, isInside, buildingName, buildingFloor);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap imageBitmap = CameraUtils.onCameraActivityResult(this, requestCode, resultCode, data, photoUri);

        if (imageBitmap != null) { // user has not cancelled image capture
             // Upload Bitmap to Firebase and get its storage reference
            binImgStorageRef = FirebaseUtils.encodeBitmapAndUploadToFirebase(this, imageBitmap, photoUri);
        }
    }

}
