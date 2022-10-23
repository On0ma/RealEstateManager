package com.openclassrooms.realestatemanager.ui;

import static android.Manifest.permission.CAMERA;
import static com.openclassrooms.realestatemanager.Utils.getDateFromLong;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.databinding.ActivityAddPropertyBinding;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.model.PropertyPhotos;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddPropertyActivity extends AppCompatActivity implements DatePickerFragment.OnDataPass, AddPropertyPhotosAdapter.PropertyPhotoCallback {

    Uri picUri;

    private RealEstateManagerDatabase database;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 107;

    private ActivityAddPropertyBinding binding;

    private final String DATE_PICKER_ADDED = "added";
    private final String DATE_PICKER_SOLD = "sold";

    private AddPropertyPhotosAdapter adapter;
    private List<PropertyPhotos> propertyPhotos = new ArrayList<>();
    private RecyclerView recyclerView;
    private Property.PropertyType propertyType;
    private Property.SaleStatus saleStatus;
    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPropertyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = RealEstateManagerDatabase.getInstance(this);

        Property property = (Property) getIntent().getSerializableExtra("property");

        recyclerView = binding.fragmentPropertyRecyclerView;
        adapter = new AddPropertyPhotosAdapter(propertyPhotos, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        ArrayAdapter<Property.SaleStatus> statusAdapter = new ArrayAdapter<Property.SaleStatus>(this, android.R.layout.simple_spinner_item, Property.SaleStatus.values());
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.propertyAddStatus.setAdapter(statusAdapter);

        ArrayAdapter<Property.PropertyType> typeAdapter = new ArrayAdapter<Property.PropertyType>(this, android.R.layout.simple_spinner_item, Property.PropertyType.values());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.propertyAddType.setAdapter(typeAdapter);

        binding.propertyAddType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = String.valueOf(adapterView.getItemAtPosition(i));
                propertyType = Property.PropertyType.fromString(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.propertyAddStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = String.valueOf(adapterView.getItemAtPosition(i));
                saleStatus = Property.SaleStatus.fromString(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.propertyAddDateAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), DATE_PICKER_ADDED);
            }
        });

        binding.propertyAddDateSold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), DATE_PICKER_SOLD);
            }
        });


        binding.propertyAddPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });

        binding.propertyAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkFields()) {
                    Toast.makeText(getApplicationContext(),"Certains champs ne sont pas correctement remplis", Toast.LENGTH_LONG).show();
                } else {
                    addProperty(property);
                }
            }
        });

        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        if (property != null) {
            isUpdate = true;
            setPropertyData(property);
        }
    }

    private void setPropertyData(Property property) {
        binding.toolbar.setTitle("Modifier un bien immobilier");
        binding.propertyAddButton.setText("Modifier un bien");
        binding.propertyAddPrice.setText(String.valueOf(property.getPrice()));
        binding.propertyAddSize.setText(String.valueOf(property.getSize()));
        binding.propertyAddRooms.setText(String.valueOf(property.getRoomNb()));
        binding.propertyAddDescription.setText(property.getDescription());
        binding.propertyAddAddress.setText(property.getAddress());
        binding.propertyAddSeller.setText(property.getPropertySeller());
        binding.propertyAddSchool.setChecked(property.isHasSchool());
        binding.propertyAddMarket.setChecked(property.isHasSuperMarket());
        adapter.updatePhotos(property.getPhotos());
        propertyPhotos.addAll(property.getPhotos());
        String dateAdded = getDateFromLong(property.getDateAdded());
        binding.propertyAddDateAdded.setText(dateAdded);
        if (property.getDateSold() != 0) {
            String dateSold = getDateFromLong(property.getDateSold());
            binding.propertyAddDateSold.setText(dateSold);
        }

        int typePos = property.getType().ordinal();
        int statusPos = property.getStatus().ordinal();
        binding.propertyAddType.setSelection(typePos);
        binding.propertyAddStatus.setSelection(statusPos);
    }

    private boolean checkFields() {
        boolean isChecked = true;
        // PRIX
        if (TextUtils.isEmpty(binding.propertyAddPrice.getText())) {
            binding.propertyAddPrice.setError("Le prix est requis");
            isChecked = false;
        }
        // SUPERFICIE
        if (TextUtils.isEmpty(binding.propertyAddSize.getText())) {
            binding.propertyAddSize.setError("La superficie est requise");
            isChecked = false;
        }
        // NOMBRE DE PIECES
        if (TextUtils.isEmpty(binding.propertyAddRooms.getText())) {
            binding.propertyAddRooms.setError("Le nombre de pièces est requis");
            isChecked = false;
        }
        if (TextUtils.isEmpty(binding.propertyAddDescription.getText())) {
            binding.propertyAddDescription.setError("La description est requise");
            isChecked = false;
        }
        if (TextUtils.isEmpty(binding.propertyAddAddress.getText())) {
            binding.propertyAddAddress.setError("L'adresse est requise");
            isChecked = false;
        }
        if (TextUtils.isEmpty(binding.propertyAddSeller.getText())) {
            binding.propertyAddSeller.setError("Le vendeur est requis");
            isChecked = false;
        }
        if (TextUtils.isEmpty(binding.propertyAddDateAdded.getText())) {
            binding.propertyAddPrice.setError("La date d'ajout est requise");
            isChecked = false;
        }
        return isChecked;
    }

    private void addProperty(Property property) {
        try {
            String dateString = binding.propertyAddDateAdded.getText().toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = simpleDateFormat.parse(dateString);
            Long dateLong = date.getTime();

            Property newProperty = new Property(
                    propertyType,
                    Float.parseFloat(binding.propertyAddPrice.getText().toString()),
                    Float.parseFloat(binding.propertyAddSize.getText().toString()),
                    Integer.parseInt(binding.propertyAddRooms.getText().toString()),
                    binding.propertyAddDescription.getText().toString(),
                    propertyPhotos,
                    binding.propertyAddAddress.getText().toString(),
                    saleStatus,
                    dateLong,
                    binding.propertyAddSeller.getText().toString(),
                    binding.propertyAddSchool.isChecked(),
                    binding.propertyAddMarket.isChecked()
            );

            if (isUpdate) {
                newProperty.setId(property.getId());
                database.propertyDao().updateProperty(newProperty);
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Votre bien immobilier a bien été modifié", Toast.LENGTH_LONG).show();
            } else {
                database.propertyDao().createProperty(newProperty);
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Votre bien immobilier a bien été ajouté", Toast.LENGTH_LONG).show();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDataPass(String data, String tag) {
        if (Objects.equals(tag, DATE_PICKER_ADDED)) {
            binding.propertyAddDateAdded.setText(data);
        } else if (Objects.equals(tag, DATE_PICKER_SOLD)) {
            binding.propertyAddDateSold.setText(data);
        }
    }

    @Override
    public void onPropertyDelete(PropertyPhotos propertyPhoto) {
        propertyPhotos.remove(propertyPhoto);
        adapter.updatePhotos(propertyPhotos);
    }

    /**
     * Create a chooser intent to select the source to get image from.<br />
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br />
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);

                PropertyPhotos newPhoto = new PropertyPhotos(picUri.toString(), "");
                propertyPhotos.add(newPhoto);
                adapter.updatePhotos(propertyPhotos);
            }
        }
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br />
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {

                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());

                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }
}
