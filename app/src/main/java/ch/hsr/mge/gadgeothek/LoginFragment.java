package ch.hsr.mge.gadgeothek;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.mge.gadgeothek.service.LibraryService;

/**
 * A login screen that offers login via email/password.
 */
public class LoginFragment extends Fragment implements OnClickListener, AdapterView.OnItemSelectedListener {

    EditText server;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface IHandleLoginFragment {
        void onAttemptLogin(String email, String password);
        void onStartRegistration();
    }

    public enum Errors {
        INVALID_PASSWORD,
        OTHER
    }

    private static final String SERVER_ADDRESS = "http://10.0.2.2:8080/public";

    // UI references.
    EditText mServerView;
    EditText mEmailView;
    EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private IHandleLoginFragment activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        root.findViewById(R.id.btn_sign_in).setOnClickListener(this);
        root.findViewById(R.id.btn_start_registration).setOnClickListener(this);
        return root;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof IHandleLoginFragment) {
            this.activity = (IHandleLoginFragment) activity;
        } else {
            throw new AssertionError("Activity must implement IHandleLoginFragment");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);


        //TODO: get server address from settings
        Log.d("Gadgeothek", "Setting Server Address to: " + SERVER_ADDRESS);
        LibraryService.setServerAddress(SERVER_ADDRESS);

        // Set up the login form.
        mServerView = (EditText) getView().findViewById(R.id.server);
        mEmailView = (EditText) getView().findViewById(R.id.email);
        mPasswordView = (EditText) getView().findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

//        Button mSignInButton = (Button) getView().findViewById(R.id.btn_sign_in);
//        mSignInButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                attemptLogin();
//            }
//        });

//        Button mRegistrationButton = (Button) getView().findViewById(R.id.btn_start_registration);
//        mRegistrationButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("Gadgeothek", "GOTO Registration Activity");
//                Fragment fragment = new RegisterFragment();
//                Bundle arguments = new Bundle();
//                arguments.putString(RegisterFragment.ARG_EMAIL, mEmailView.getText().toString());
//                arguments.putString(RegisterFragment.ARG_PASSWORD, mPasswordView.getText().toString());
//                fragment.setArguments(arguments);
//                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
//            }
//        });

        mLoginFormView = getView().findViewById(R.id.login_form);
        mProgressView = getView().findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            activity.onAttemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());

        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@hsr.ch");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_sign_in: {
                attemptLogin();
                break;
            }
            case R.id.btn_start_registration: {
                activity.onStartRegistration();
                break;
            }

        }
    }

    public void handleError(Errors error){
        handleError(error,null);
    }

    public void handleError(Errors error, String message){
        switch (error){
            case INVALID_PASSWORD: {
                Log.w("Gadgeothek", "Login fehlgeschlagen.");
                //TODO: Set focus on Password Field (and set Hint)
                break;
            }
            case OTHER: {
                Log.w("Gadgeothek", "Login fehlgeschlagen." + message);
                break;
            }
        }
    }
}

