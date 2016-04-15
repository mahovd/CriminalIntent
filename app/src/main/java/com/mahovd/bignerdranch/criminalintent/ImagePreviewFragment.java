package com.mahovd.bignerdranch.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by mahovd on 12/04/16.
 * Controller
 */
public class ImagePreviewFragment extends android.support.v4.app.DialogFragment {

    private static final String ARG_FILE = "photoFile";
    private ImageView mImageView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        File photoFile = (File) getArguments().getSerializable(ARG_FILE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image,null);

        mImageView = (ImageView) v.findViewById(R.id.dialog_image_container);

        Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(),getActivity());


        mImageView.setImageBitmap(bitmap);


        return new AlertDialog.Builder(getActivity()).
                setView(v).
                create();
    }


    public static ImagePreviewFragment newInstance(File photoFile){

        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE,photoFile);

        ImagePreviewFragment imgFragment = new ImagePreviewFragment();
        imgFragment.setArguments(args);

        return imgFragment;

    }


}
