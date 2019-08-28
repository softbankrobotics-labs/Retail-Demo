package com.softbankrobotics.retaildemo.Utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TokenFile {
    private static String CredentialsFileName = "emailLogin";
    private static String TAG = "Token";

    public static void disconnect(Context context) {
        deleteFile(context, CredentialsFileName);
    }

    public static String getEmailCredentials(Context context) {
        return readStringFromFile(context, CredentialsFileName);
    }

    public static void saveEmailCredentials(Context context, String token) {
        writeStringToFile(context, token, CredentialsFileName);
        Log.i(TAG, "Token saved!");
    }

    private static void writeStringToFile(Context applicationContext, String data, String fileName) {
        OutputStreamWriter outputStreamWriter = null;
        try {
            Log.d(TAG, "writeStringToFile: started");
            outputStreamWriter = new OutputStreamWriter(applicationContext.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
        } catch (IOException e) {
            Log.d("Exception", "File write failed: " + e.getMessage(), e);
        } finally {
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            } catch (IOException e) {

                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "writeStringToFile:  Finished");
    }




    private static String readStringFromFile(Context applicationContext, String fileName) {

        String ret = "";
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader;
        InputStream inputStream = null;

        try {
            inputStream = applicationContext.openFileInput(fileName);

            if (inputStream != null) {
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Can not read file: " + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }
        return ret;
    }

    private static void deleteFile(Context context, String filename) {
        boolean ret = context.deleteFile(filename);
        Log.i(TAG, "file deleted: " + ret);
    }

}
