package com.remu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.remu.POJO.User;

import java.util.ArrayList;
import java.util.List;

public class QRActivity extends AppCompatActivity {

    CameraView cameraView;
    boolean isDetected = false;
    Button btnShowQR, btnInputUID;
    ImageView userQR;
    CardView qrcard;
    private Context context;


    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r);

        initialzeUI();
        context = this;

        btnShowQR.setOnClickListener(view -> {
            ShowUserUIDAsBarcode();
            qrcard.setVisibility(View.VISIBLE);
        });

        qrcard.setOnClickListener(view -> {
            qrcard.setVisibility(View.GONE);
        });

        cameraView.setLifecycleOwner(this);
        cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                processImage(getVisionImageFromFrame(frame));
            }
        });

        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();

        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

    }

    private void processImage(FirebaseVisionImage visionImageFromFrame) {
        if (!isDetected) {
            detector.detectInImage(visionImageFromFrame).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                @Override
                public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                    processResult(firebaseVisionBarcodes);
                }
            }).addOnFailureListener(e -> {
                //TODO: giving on failure solution on processing barcode
            });
        }
    }

    private void processResult(List<FirebaseVisionBarcode> barcodes) {
        if (barcodes.size() > 0) {
            isDetected = true;
            for (FirebaseVisionBarcode item : barcodes) {
                int value_type = item.getValueType();
                if (value_type == FirebaseVisionBarcode.TYPE_TEXT) {
                    System.out.println("HELLO FROM THE OTHER SIDE!");
                    String scannedUID = item.getRawValue();
                    DatabaseReference yourDatabase = FirebaseDatabase.getInstance().getReference().child("Profile").child(scannedUID);
                    yourDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                List<User> userList = new ArrayList<>();
                                userList.add(new User(scannedUID, dataSnapshot.child("image").getValue().toString(), dataSnapshot.child("birthdate").getValue().toString(), dataSnapshot.child("gender").getValue().toString(), dataSnapshot.child("name").getValue().toString()));
                                String json = new Gson().toJson(userList);
                                Intent intent = new Intent(QRActivity.this, FindFriendResultActivity.class);
                                intent.putExtra("friendList", json);
                                startActivity(intent);
                            } catch (NullPointerException np) {
                                //insert something here
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
    }

    //get user UID as barcode to scan
    private void ShowUserUIDAsBarcode() {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        String UID = FirebaseAuth.getInstance().getUid();
        System.out.println("#01: CURRENT USER UID IS: " + UID);
        Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(UID, BarcodeFormat.QR_CODE, 300, 300);
            for (int i = 0; i < 300; i++) {
                for (int j = 0; j < 300; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }

        userQR.setImageBitmap(bitmap);

    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                .build();
        return FirebaseVisionImage.fromByteArray(data, metadata);
    }

    private void initialzeUI() {
        btnShowQR = findViewById(R.id.btn_show_qr);
        cameraView = findViewById(R.id.camera_view);
        userQR = findViewById(R.id.user_QR);
        qrcard = findViewById(R.id.QR_card);
    }

}
