package ch.hsr.mge.gadgeothek;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterFragment extends Fragment {

    public interface IHandleRegisterFragment {
        void onAttemptRegistration(String email, String password, String name, String studentnumber);
        void onCancelRegistration();
        String getEmail();
        String getPassword();
        void setEmail(String email);
        void setPassword(String password);
    }

    // UI references.
    EditText mEmailView;
    EditText mPasswordView;
    EditText mNameView;
    EditText mStudentnumberView;

    private IHandleRegisterFragment activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        _onAttach_API_independent(activity);
    }

    /**
     * Code duplication for API Level 22 support
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _onAttach_API_independent(activity);
    }

    private void _onAttach_API_independent(Context activity){
        if (activity instanceof IHandleRegisterFragment) {
            this.activity = (IHandleRegisterFragment) activity;
        } else {
            throw new AssertionError("Activity must implement IHandleLoginFragment");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String email = activity.getEmail();
        String password = activity.getPassword();
        mEmailView.setText(email);
        mPasswordView.setText(password);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up the login form.
        mEmailView = (EditText) getView().findViewById(R.id.register_email);
        mPasswordView = (EditText) getView().findViewById(R.id.register_password);
        mNameView = (EditText) getView().findViewById(R.id.name);
        mStudentnumberView = (EditText) getView().findViewById(R.id.studentnumber);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) getView().findViewById(R.id.btn_register);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        Button mCancelButton = (Button) getView().findViewById(R.id.btn_cancel_register);
        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRegistration();
            }
        });

        mEmailView.setText(activity.getEmail());
        mPasswordView.setText(activity.getPassword());

    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegistration() {



        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNameView.setError(null);
        mStudentnumberView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();
        String studentnumber = mStudentnumberView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check the inputfields from the bottom to top
        // 1. Password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // 2. Email
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // 3. Student Number
        if (TextUtils.isEmpty(studentnumber)) {
            mStudentnumberView.setError(getString(R.string.error_field_required));
            focusView = mStudentnumberView;
            cancel = true;
        }

        // 4. Full Name
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            activity.onAttemptRegistration(email, password, name, studentnumber);

        }
    }

    private void cancelRegistration(){
        activity.setEmail(mEmailView.getText().toString());
        activity.setPassword(mPasswordView.getText().toString());
        activity.onCancelRegistration();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }




}

