package com.nth.ikiam;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by DELL on 23/07/2014.
 */
public class CameraFragment extends Fragment implements Button.OnClickListener {

    private Button chooseBtn;
    private ImageView selectedImage;

    private static final int CAMERA_PIC_REQUEST = 1337;

    public CameraFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_layout, container, false);
        selectedImage = (ImageView) view.findViewById(R.id.camera_chosen_image_view);
        chooseBtn = (Button) view.findViewById(R.id.camera_btn);
        chooseBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), R.string.camera_app_not_available, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
//                tv.setText("Got picture!");
            Bitmap imageData = (Bitmap) data.getExtras().get("data");
            selectedImage.setImageBitmap(imageData);
        } else if (resultCode == Activity.RESULT_CANCELED) {
//                tv.setText("Cancelled");
        }
    }

}