package ch.hsr.mge.gadgeothek;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginFragment extends Fragment implements OnClickListener {

    public interface IHandleLoginFragment {
        void onAttemptLogin(String email, String password);
        void onStartRegistration();
        String getEmail();
        String getPassword();
    }

    // UI references.
    EditText mServerView;
    EditText mEmailView;
    EditText mPasswordView;

    private IHandleLoginFragment context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        root.findViewById(R.id.btn_sign_in).setOnClickListener(this);
        root.findViewById(R.id.btn_start_registration).setOnClickListener(this);
        return root;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _onAttach_API_independent(context);
    }
    /**
     * Code duplication of onAttach(Context context) to enable API Level 22 compatibility
    */
    public void onAttach(Activity activity){
        super.onAttach(activity);
        _onAttach_API_independent(activity);
    }
    private void _onAttach_API_independent(Context context){
        if (context instanceof IHandleLoginFragment) {
            this.context = (IHandleLoginFragment) context;
        } else {
            throw new AssertionError("Context must implement IHandleLoginFragment");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String email = context.getEmail();
        String password = context.getPassword();
        mEmailView.setText(email);
        mPasswordView.setText(password);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);

        // Set up the login form.
        mServerView = (EditText) getView().findViewById(R.id.server);
        mEmailView = (EditText) getView().findViewById(R.id.login_email);
        mPasswordView = (EditText) getView().findViewById(R.id.login_password);
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



        // Set default Server
        mServerView.setText("http://mge1.dev.ifs.hsr.ch/public");

        mEmailView.setText(context.getEmail());
        mPasswordView.setText(context.getPassword());
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

            context.onAttemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());

        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@hsr.ch");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_sign_in: {
                attemptLogin();
                break;
            }
            case R.id.btn_start_registration: {
                context.onStartRegistration();
                break;
            }

        }
    }
}

