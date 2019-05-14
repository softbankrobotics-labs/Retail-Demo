package com.example.retaildemo.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.retaildemo.MainActivity;
import com.example.retaildemo.R;

public class EmailFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_EmailFragment";
    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_email;
    private String prevFragment = "";

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        this.mainActivity = (MainActivity) getActivity();
        prevFragment = mainActivity.getPreviousFragment();
        Integer themeId = mainActivity.getThemeId();
        if(themeId != null){
            final Context contextThemeWrapper = new ContextThemeWrapper(mainActivity, themeId);
            LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
            return localInflater.inflate(fragmentId, container, false);
        }
        return null;    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.button_back_email).setOnClickListener(v -> mainActivity.setFragment(new ProductSelectionFragment()));
        view.findViewById(R.id.button_home_email).setOnClickListener(v -> mainActivity.setFragment(new MainMenuFragment()));
        TextView contextText = getView().findViewById(R.id.BannerEmail);
        EditText editTextMail = view.findViewById(R.id.EmailInput);
        Button buttonDone = view.findViewById(R.id.ButtonConfirmMail);
        Resources resources = getResources();
        buttonDone.setOnClickListener(v -> {
            if(isEmailValid(editTextMail.getText())){
                mainActivity.goToBookmarkInTopic("validmail","email");
            }else{
                contextText.setText(resources.getString(R.string.TextValidMail));
            }
        });
        editTextMail.setOnEditorActionListener(
            (v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.
                        if(isEmailValid(editTextMail.getText())){
                            mainActivity.goToBookmarkInTopic("validmail","email");
                            return true;
                        }else{
                            contextText.setText(resources.getString(R.string.TextValidMail));
                            return false;
                        }
                    }
                }
                return false; // pass on to other listeners.
            }
        );
    }

    public void onResume() {
        super.onResume();
    }

    public void onClickBack(){
        //TODO need to see where you go back

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}