package com.romega_swiftfix;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.romega_swiftfix.model.Job;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ReportProblemActivity extends AppCompatActivity implements OnMapReadyCallback {
    private boolean isManualLocationSelection = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_COMPRESSION_QUALITY = 50;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private ImageView imageViewSelected;
    private GoogleMap map;
    private LatLng selectedLocation;
    private String currentPhotoPath;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        // Set App Bar
        AppBar.setAppBar(getSupportFragmentManager(), R.id.fragmentContainerView3);

        // Set Bottom Navigation
        BottomNavigation.setNavigationBar(getSupportFragmentManager(), R.id.fragmentContainerView2);

        Button selectLocationButton = findViewById(R.id.buttonSelectLocation);
        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationPermission();
                showMap();
            }
        });

        Button attachImageButton = findViewById(R.id.buttonAttachImage);
        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasCameraPermission()) {
                    showImageOptions();
                } else {
                    requestCameraPermission();
                }
            }
        });

        imageViewSelected = findViewById(R.id.imageViewSelected);

        /////////Submit Button Function/////////
        Button submitButton = findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate that the description field is not empty
                if (isDescriptionValid() && isImageSelected()) {
                    // Call a method to handle job submission
                    submitJob();
                } else {
                    showToast("Please enter a description and select an image for your problem");
                }
            }
        });

    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private String getJobDescription() {

    EditText editTextDescription = findViewById(R.id.editTextProblemDescription);
    return editTextDescription.getText().toString();

}

    private boolean isDescriptionValid() {
        EditText editTextDescription = findViewById(R.id.editTextProblemDescription);
        // Check if the description is not empty
        return !editTextDescription.getText().toString().trim().isEmpty();
    }


    private boolean isImageSelected() {
        // Check if an image is selected
        return imageViewSelected.getDrawable() != null;
    }
    private void submitJob() {
        if (selectedLocation != null) {
            // Generate a unique job number
            String jobNumber = generateUniqueJobNumber();

            String jobDescription = getJobDescription();
            // Get the user's email
            String userEmail = firebaseAuth.getCurrentUser().getEmail();

            // Upload the image to Firebase Storage
            uploadImageToStorage(jobNumber);

            // Create a Job object with relevant details
            Job job = new Job(
                    jobNumber,
                    jobDescription,
                    selectedLocation.latitude,
                    selectedLocation.longitude,
                    userEmail
            );

            // Add the job to Firestore
            addJobToFirestore(job);
        } else {
            showToast("Please select a location on the map");
        }

        isManualLocationSelection = false; // Disable manual selection after confirming
    }

    private String generateUniqueJobNumber() {

        return String.valueOf(System.currentTimeMillis()) + new Random().nextInt(1000);
    }

    private void uploadImageToStorage(String jobNumber) {
        // Get the ImageView as a Bitmap
        imageViewSelected.setDrawingCacheEnabled(true);
        imageViewSelected.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageViewSelected.getDrawable()).getBitmap();

        // Compress the bitmap to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, baos);
        byte[] data = baos.toByteArray();

        // Define the storage path
        String storagePath = "images/" + jobNumber + ".jpg";

        // Upload the image to Firebase Storage
        StorageReference imageRef = storageReference.child(storagePath);
        UploadTask uploadTask = imageRef.putBytes(data);

        // Register observers to listen for when the upload is successful or fails
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image upload success
                showToast("Image uploaded successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Image upload failure
                showToast("Error uploading image: " + e.getMessage());
            }
        });
    }

    private void addJobToFirestore(Job job) {
        // Add the job details to Firestore
        firestore.collection("jobs")
                .document(job.getJobNumber())
                .set(job)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Job submitted successfully!");

                        Intent intent = new Intent( ReportProblemActivity.this,ViewStatusActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Error submitting job: " + e.getMessage());
                    }
                });
    }






    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            // Permission is already granted, proceed with location selection
            showLocationSelection();
        }
    }
    private void showLocationSelection() {
        // Check if the map is ready
        if (map != null) {
            // Use the map to get the user's current location or allow them to mark a location
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // Handle the selected location
                    handleSelectedLocation(latLng);
                }
            });


            showToast("Click on the map to select your location");
        }
    }

    private void handleSelectedLocation(LatLng latLng) {
        // Handle the selected location, e.g., update UI, store the location, etc.
        selectedLocation = latLng;

        // Remove the map click listener to prevent further selection
        map.setOnMapClickListener(null);

        showToast("Location selected: ");
    }


    private void showMap() {
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.mapContainer, mapFragment);
            ft.addToBackStack(null);
            ft.commit();
            fm.executePendingTransactions();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng latLng = new LatLng(7.4373449337030095, 80.43210958509296);
//        map.addMarker(new MarkerOptions().position(latLng).title("Location"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

        // Hide the button and show the map
        Button selectLocationButton = findViewById(R.id.buttonSelectLocation);
        selectLocationButton.setVisibility(View.GONE);

        View mapContainer = findViewById(R.id.mapContainer);
        mapContainer.setVisibility(View.VISIBLE);

        // Set an onClickListener to handle manual location selection
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isManualLocationSelection) {
                    // Clear existing markers
                    map.clear();
                    // Add a new marker at the selected location
                    map.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                    selectedLocation = latLng;
                    showToast("Location Selected"+ selectedLocation);
                }
            }
        });

        // Set an onMapLongClickListener to clear the manual selection
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // Clear existing markers
                map.clear();
                // Add a marker at the default location
                map.addMarker(new MarkerOptions().position(latLng).title("Location"));
                selectedLocation = latLng;
                showToast("Location Selected");
            }
        });

        // Set an onMarkerClickListener to handle marker clicks
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // If the marker is the selected location marker, toggle manual selection mode
                if (marker.getPosition().equals(selectedLocation)) {
                    isManualLocationSelection = !isManualLocationSelection;
                    showToast(isManualLocationSelection ? "Select a location on the map" : "Map click disabled");
                    return true;
                }
                return false;
            }
        });
    }

    // handle the location set button click
//    public void onLocationSetButtonClick(View view) {
//        if (selectedLocation != null) {
//            // Do something with the selected location
//            showToast("Location set: " + selectedLocation.latitude + ", " + selectedLocation.longitude);
//        } else {
//            showToast("Please select a location on the map");
//        }
//        isManualLocationSelection = false; // Disable manual selection after confirming
//    }
    private void showImageOptions() {
        // Display a dialog or options to choose between camera and gallery
        // For simplicity, I'll use a simple AlertDialog in this example
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source")
                .setItems(R.array.image_sources, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            dispatchTakePictureIntent();
                        } else {
                            pickImageFromGallery();
                        }
                    }
                });
        builder.create().show();
    }

    private void dispatchTakePictureIntent() {
        // Create a file to save the image
        File photoFile = createImageFile();

        if (photoFile != null) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            Uri photoUri = FileProvider.getUriForFile(
                    this,
                    "com.romega_swiftfix",
                    photoFile
            );

            // Pass the photo URI to the camera app
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            showToast("Error creating image file");
        }
    }


    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Display the captured image
                setPic();
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // Handle the selected image from the gallery
                Uri selectedImageUri = data.getData();
                // Display the selected image
                imageViewSelected.setImageURI(selectedImageUri);
                imageViewSelected.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageViewSelected.getWidth();
        int targetH = imageViewSelected.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        // Display the bitmap in the ImageView
        imageViewSelected.setImageBitmap(bitmap);
        imageViewSelected.setVisibility(View.VISIBLE);
    }
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File imageFile = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir       // directory
            );
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = imageFile.getAbsolutePath();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageOptions();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location selection
                showLocationSelection();
            } else {
                // Permission denied, show a message or handle accordingly
                showToast("Location permission denied");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
