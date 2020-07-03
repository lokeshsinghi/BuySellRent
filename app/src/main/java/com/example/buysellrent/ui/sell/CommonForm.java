package com.example.buysellrent.ui.sell;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buysellrent.Adapter.AdvertisementAdapter;
import com.example.buysellrent.Class.AdvertisementCarModel;
import com.example.buysellrent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class CommonForm extends AppCompatActivity {

    private ImageView addImages;
    private RecyclerView recyclerView;
    private ArrayList<Bitmap> bitmaps;
    private ArrayList<Uri> imageList;
    private ArrayList<String> imageListFinal;
    private EditText count;
    private RecyclerView.LayoutManager layoutManager;
    private Button images_next;
    private EditText price_ad;
    private EditText phone_num;
    private String AdId;
    private String address="unknown";
    private int i;
    private TextView locationText;
    private int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_form);
        bitmaps = new ArrayList<Bitmap>();
        addImages = (ImageView)findViewById(R.id.addImages);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerAdImages);
        phone_num = (EditText) findViewById(R.id.phone);
        layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        images_next=findViewById(R.id.ad_image_next);
        locationText = findViewById(R.id.selectLocation);
        locationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommonForm.this, SelectAdLocation.class);
                startActivityForResult(intent,2);
            }
        });

        price_ad=findViewById(R.id.price_ad);
        imageList=new ArrayList<Uri>();
        i=0;
        imageListFinal=new ArrayList<>();

        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(CommonForm.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(CommonForm.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            100);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        images_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = getIntent().getExtras();
                AdvertisementModel advertisementModel = null;


                long price;

                if (!price_ad.getText().toString().equals(""))
                    price = Long.parseLong(price_ad.getText().toString());
                else
                    price = 0;
                String number = "+977" + phone_num.getText().toString().trim();


                if (price < 100 || price > 10000000) {
                    price_ad.setError("Price must be in range of Hundred to Crore !");
                } else if (bitmaps.size() < 3) {

                    Toast.makeText(CommonForm.this, "Please insert three images to continue", Toast.LENGTH_LONG).show();
                } else if (address.equals("unknown")) {
                    Toast.makeText(CommonForm.this, "Please select the location", Toast.LENGTH_LONG).show();
                    locationText.setError("Please select the location !");
                } else if (phone_num.getText().toString().equals("")||phone_num.getText().toString().length() < 10) {

                    phone_num.setError("Please enter valid mobile number");

                } else if (bundle.getString("category").equals("Cars")) {

                    flag = 1;
                    String Price = price + "";

                    final String Brand = bundle.getString("brand");
                    final int Year = bundle.getInt("year");
                    final int Driven = bundle.getInt("driven");
                    final String transmission = bundle.getString("transmission");
                    final String Title = bundle.getString("title");
                    final String Desc = bundle.getString("description");
                    final String Fuel = bundle.getString("fuel");
                    final String category = bundle.getString("category");
                    AdId = UUID.randomUUID().toString();
                    advertisementModel = new AdvertisementCarModel(Brand, Year, Driven, transmission, Title, Desc, Fuel, Price, category, number, address);
                } else if (bundle.getString("category").equals("Bikes")) {

                    flag = 2;
                    String Price = price + "";

                    final String Brand = bundle.getString("brand");
                    final int Year = bundle.getInt("year");
                    final int Driven = bundle.getInt("driven");
                    final String Title = bundle.getString("title");
                    final String Desc = bundle.getString("description");
                    final String category = bundle.getString("category");
                    AdId = UUID.randomUUID().toString();
                    advertisementModel = new AdvertisementBikeModel(Brand, Year, Driven, Title, Desc, Price, category, number, address);

                }
                else {
                    flag=3;
                    String Price = price + "";

                    String Brand = bundle.getString("brand");
                    if(Brand.equals(""))
                        Brand="NOT BRANDED";
                    final String Title = bundle.getString("title");
                    final String Desc = bundle.getString("description");
                    final String category = bundle.getString("category");
                    AdId = UUID.randomUUID().toString();
                    advertisementModel = new AdvertisementExtraModel(Brand,Title, Desc, Price, category, number, address);
                }
                if (flag == 1) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String Uid = firebaseUser.getUid();
                    ((AdvertisementCarModel)advertisementModel).setSellerId(Uid);
                    ((AdvertisementCarModel)advertisementModel).setAdId(AdId);
                    ((AdvertisementCarModel)advertisementModel).setImgCount(imageList.size());
                    ((AdvertisementCarModel)advertisementModel).setNumber(number);
                }
                else if(flag==2){
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String Uid = firebaseUser.getUid();
                    ((AdvertisementBikeModel)advertisementModel).setSellerId(Uid);
                    ((AdvertisementBikeModel)advertisementModel).setAdId(AdId);
                    ((AdvertisementBikeModel)advertisementModel).setImgCount(imageList.size());
                    ((AdvertisementBikeModel)advertisementModel).setNumber(number);
                }
                else if(flag>0){
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String Uid = firebaseUser.getUid();
                    ((AdvertisementExtraModel)advertisementModel).setSellerId(Uid);
                    ((AdvertisementExtraModel)advertisementModel).setAdId(AdId);
                    ((AdvertisementExtraModel)advertisementModel).setImgCount(imageList.size());
                    ((AdvertisementExtraModel)advertisementModel).setNumber(number);
                }

                if(flag>0){
                    for (Uri a : imageList) {

                        final ProgressDialog progressDialog = new ProgressDialog(CommonForm.this);
                        progressDialog.setTitle("Uploading " + a);
                        progressDialog.show();

                        final StorageReference profileRef = FirebaseStorage.getInstance().getReference("ad_uploads").child(System.currentTimeMillis() + "." + getFileExtension(a));
                        profileRef.putFile(a)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        progressDialog.dismiss();
                                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Log.e("images", uri.toString());
                                                imageListFinal.add(uri.toString());
                                                i++;
                                                {

                                                    String img = "image" + i;
                                                    FirebaseDatabase.getInstance().getReference("AdImages").child(AdId).child(img).setValue(uri.toString());
                                                    //Log.e("images",a.toString());

                                                }
                                                //Toast.makeText(CommonForm.this, "Success", Toast.LENGTH_SHORT).show();
                                            }

                                        });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {

                                        progressDialog.dismiss();
                                        Toast.makeText(CommonForm.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        //calculating progress percentage
                                        double progress = 0;

                                        progress = (double) (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                        //displaying percentage in progress dialog
                                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");

                                    }

                                });
                    }


                    FirebaseDatabase.getInstance().getReference("Ads").child(AdId)
                            .setValue(advertisementModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CommonForm.this, "Ad sent for Verification", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(CommonForm.this, "Error", Toast.LENGTH_LONG).show();
                            }

//                                String imgCount=""+imageListFinal.size();
//                                FirebaseDatabase.getInstance().getReference("CarAds").child(randId).child("ImageCount").setValue(imgCount);
                            Intent intent = new Intent(CommonForm.this, VerificationAd.class);
                            startActivity(intent);

                        }


                    });


                }


            }



        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK) {
            address = data.getStringExtra("location");

            locationText.setText(address);
        }

        if (requestCode == 1 && resultCode == RESULT_OK) {
            final ImageView imageView = findViewById(R.id.imageView);

            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    imageList.add(imageUri);
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmaps.add(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Uri imageUri = data.getData();
                imageList.add(imageUri);
                try {
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    bitmaps.add(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }


        }
        AdvertisementAdapter advertisementAdapter=new AdvertisementAdapter(this,bitmaps,imageList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(advertisementAdapter);
        }

        private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
        }

    }


