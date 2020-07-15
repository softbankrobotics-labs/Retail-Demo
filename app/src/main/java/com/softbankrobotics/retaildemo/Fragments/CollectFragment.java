package com.softbankrobotics.retaildemo.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.softbankrobotics.retaildemo.Barcode.BarcodeTrackerFactory;
import com.softbankrobotics.retaildemo.Camera.CameraSource;
import com.softbankrobotics.retaildemo.Camera.CameraSourcePreview;
import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

import java.io.IOException;

public class CollectFragment extends Fragment {

    private static final String TAG = "MSI_CollectFragment";

    private MainActivity ma;
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private Button buttonDemoTicket;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int RC_HANDLE_GMS = 9001;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_collect;
        this.ma = (MainActivity) getActivity();
        if(ma != null){
            Integer themeId = ma.getThemeId();
            if(themeId != null){
                final Context contextThemeWrapper = new ContextThemeWrapper(ma, themeId);
                LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
                return localInflater.inflate(fragmentId, container, false);
            }else{
                return inflater.inflate(fragmentId, container, false);
            }
        }else{
            Log.e(TAG, "could not get mainActivity, can't create fragment");
            return null;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView orderInput = view.findViewById(R.id.edit_order_number);
        view.findViewById(R.id.button_home_collect).setOnClickListener(v ->
                ma.setFragment(new MainMenuFragment()));
        buttonDemoTicket = view.findViewById(R.id.button_test_ticket_collect);
        buttonDemoTicket.setOnClickListener(v-> {
            ma.status.currentTicket = orderInput.getText().toString();
            ma.setFragment(new CollectConfirmFragment());
                });
        TextView orderContext = view.findViewById(R.id.text_order_number);
        Resources resources = getResources();
        view.findViewById(R.id.button_done_order_number).setOnClickListener(v -> {
            if(!(orderInput.getText().length() == 0)){
                ma.status.currentTicket = orderInput.getText().toString();
                ma.setFragment(new CollectConfirmFragment());
            }else{
                orderContext.setText(resources.getString(R.string.TextValidOrder));
            }
        });
        orderInput.setOnEditorActionListener(
            (v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.
                        if(!(orderInput.getText().length() == 0)){
                            ma.status.currentTicket = orderInput.getText().toString();
                            ma.setFragment(new CollectConfirmFragment());
                        }else{
                            orderContext.setText(resources.getString(R.string.TextValidOrder));
                        }
                    }
                }
                return false; // pass on to other listeners.
            }
        );
        mPreview = view.findViewById(R.id.camera_preview);
        Log.d(TAG, "onViewCreated: Build.SERIAL : "+Build.SERIAL);
        new Handler().postDelayed(()->{
            if (!Build.SERIAL.equalsIgnoreCase("EMULATOR30X0X12X0")) {
                try {
                    boolean autoFocus = true;
                    boolean useFlash = false;
                    // Check for the camera permission before accessing the camera.  If the
                    // permission is not granted yet, request permission.
                    int rc = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
                    if (rc == PackageManager.PERMISSION_GRANTED) {
                        createCameraSource(autoFocus, useFlash);
                        mPreview.start(mCameraSource);
                    } else {
                        requestCameraPermission();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        },350);
    }

    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = ma.getApplicationContext();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(this.getContext());
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = ma.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(ma, R.string.low_storage_error,
                        Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        ma.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraSource.Builder builder = new CameraSource.Builder(ma.getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedPreviewSize(metrics.widthPixels, metrics.heightPixels)
                .setRequestedFps(24.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }


    public void pressDemoTicketButton(){
        ma.runOnUiThread(buttonDemoTicket::performClick);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }



        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = true;
            boolean useFlash = false;
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.d(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = (dialog, id) -> getActivity().finish();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
        }
    }

    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ma.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(ma, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.d(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    // Stops the camera
    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

}
