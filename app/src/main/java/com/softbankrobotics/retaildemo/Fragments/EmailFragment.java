package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.softbankrobotics.retaildemo.Utils.Mail;
import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;
import com.softbankrobotics.retaildemo.Utils.Item;

import java.io.IOException;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class EmailFragment extends Fragment {

    private static final String TAG = "MSI_EmailFragment";
    private MainActivity ma;
    private String user = "";
    private String pwd = "";
    private TextView contextText;
    private EditText editTextMail;
    private EditText editTextMailSuffix;
    private Resources resources;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_email;
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
        view.findViewById(R.id.button_back_email).setOnClickListener(v -> ma.setFragment(new ProductSelectionFragment()));
        view.findViewById(R.id.button_home_email).setOnClickListener(v -> ma.setFragment(new MainMenuFragment()));
        editTextMail = view.findViewById(R.id.fragment_email_text_email);
        editTextMailSuffix = view.findViewById(R.id.fragment_email_text_dropdown_email_suffix);

        contextText = getView().findViewById(R.id.text_enter_mail);
        //editTextMail = view.findViewById(R.id.edit_mail);
        FrameLayout buttonDone = view.findViewById(R.id.button_done_mail);
        resources = getResources();
        buttonDone.setOnClickListener(v -> {
            if(isEmailValid(getRecipientEmail(view))){
                if(isOnline() && isNetworkAvailable()){
                    sendMail(getRecipientEmail(view));
                }
                ma.getCurrentChatData().goToBookmarkSameTopic("validmail");
                //hiding the soft keyboard
                hideKeyboard();
                buttonDone.setEnabled(false);
            }else{
                contextText.setText(resources.getString(R.string.TextValidMail));
            }
        });
        editTextMail.setOnEditorActionListener(
            (v, actionId, event) -> onKeyboardSendEmail(view, actionId, event)
        );
        editTextMailSuffix.setOnEditorActionListener(
                (v, actionId, event) -> onKeyboardSendEmail(view, actionId, event)
        );

        initializeEmailSuffixTextDropDown(view);
    }

    private void initializeEmailSuffixTextDropDown(View view){
        String[] choice = resources.getStringArray(R.array.array_email_suffix);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getContext(), R.layout.item_dropdown_menu, choice);
        AutoCompleteTextView emailSuffixTextDropdown = view.findViewById(R.id.fragment_email_text_dropdown_email_suffix);
        emailSuffixTextDropdown.setAdapter(adapter);
        CharSequence currentText = (CharSequence) emailSuffixTextDropdown.getAdapter().getItem(0);
        emailSuffixTextDropdown.setText(currentText.toString(), false);
    }

    private String getRecipientEmail(View view){
        Log.d(TAG, "getRecipientEmail: "+editTextMail.getText().toString()+editTextMailSuffix.getText().toString());
        return editTextMail.getText().toString()+editTextMailSuffix.getText().toString();
    }

    private boolean onKeyboardSendEmail(View view, int actionId, KeyEvent event){
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event != null &&
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (event == null || !event.isShiftPressed()) {
                // the user is done typing.
                if(isEmailValid(getRecipientEmail(view))){
                    hideKeyboard();
                    if(isOnline() && isNetworkAvailable()){
                        sendMail(getRecipientEmail(view));
                    }
                    ma.getCurrentChatData().goToBookmarkSameTopic("validmail");
                    return true;
                }else{
                    contextText.setText(resources.getString(R.string.TextValidMail));
                    return false;
                }
            }
        }
        return false; // pass on to other listeners.

    }

    private void hideKeyboard() {
        try {
            InputMethodManager inputManager =
                    (InputMethodManager) ma.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(
                    editTextMail.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "sendMail: keyboard error : " + e);
        }
    }

    public void onResume() {
        super.onResume();
    }

    private void sendMail(String recipientAddress) {
        user = ma.getEmail();
        pwd = ma.getPassword();
        if(user.length() > 0){
            Mail m = new Mail(user, pwd);
            m.set_from(user);
            m.set_to(recipientAddress);
            String emailTemplate = getMailBody();
            Log.d(TAG,"product String send mail" + ma.status.productString);
            Log.d(TAG,"upsell String send mail" + ma.status.upsellProductString);
            emailTemplate = emailTemplate.replace("Product_1", ma.status.getName(ma.status.productString));
            emailTemplate = emailTemplate.replace("Price_product_1", ma.status.getPrice(ma.status.productString)+"EUR");
            if(ma.status.upsellProductString.length() > 0){
                for(Item i : ma.status.stocks){
                    if(ma.status.upsellProductString.equals(i.getRessourceName())){
                        emailTemplate = emailTemplate.replace("Product_2",i.getUpsellName());
                        emailTemplate = emailTemplate.replace("Price_product_2", ma.status.upsellPrice+"EUR");
                    }
                }
            }else{
                emailTemplate = emailTemplate.replace("Product_2","");
                emailTemplate = emailTemplate.replace("Price_product_2","");
            }
            m.setBody(emailTemplate);
            m.set_subject(ma.getApplication().getResources().getString(R.string.email_subject));
            SendEmailAsync(m);
        }

    }

    private String getMailBody(){
        return ma.status.templateEmail;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ma.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.isConnected();
        } else {
            return false;
        }
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static void SendEmailAsync(Mail m){
        AsyncTask at = new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    if (m.send()) {
                        Log.d(TAG,"Email sent.");
                    } else {
                        Log.d(TAG,"Email failed to send.");
                    }

                    return true;
                } catch (AuthenticationFailedException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Authentication failed.");
                    return false;
                } catch (MessagingException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Email failed to send");
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Unexpected error occured.");
                    return false;
                }
            }
        };
        at.execute();
    }
}
