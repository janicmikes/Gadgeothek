package ch.hsr.mge.gadgeothek;

import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Stack;

import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;

import static android.R.id.content;

public class LoginActivity extends AppCompatActivity implements LoginFragment.IHandleLoginFragment, RegisterFragment.IHandleRegisterFragment {
    LoginFragment loginFragment;
    RegisterFragment registerFragment;

    String mEmail = "";
    String mPassword = "";


    private Stack<Fragment> history = new Stack<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();

        setTitle(getString(R.string.title_activity_login));

        // add the starting fragment
        getFragmentManager().beginTransaction().add(R.id.login_fragment_container, loginFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (history.size() > 1) {
            history.pop();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, history.peek()).commit();
        } else {
            // initial fragment reached
        }
    }

    @Override
    public void onAttemptLogin(final String email, String password) {
        LibraryService.setServerAddress(loginFragment.mServerView.getText().toString());
        LibraryService.login(email, password,
                new Callback<Boolean>() {

                    @Override
                    public void onCompletion(Boolean input) {
                        loginFragment.showProgress(false);
                        if (input) {
                            history.clear();
                            //
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra(MainActivity.ARG_LOGIN_EMAIL, email);
                            /** Problem: Runtime Exeption was thrown here:
                                android.util.AndroidRuntimeException: Calling startActivity()
                             * from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag.
                             * Is this really what you want?
                             * FIX: from http://stackoverflow.com/questions/3918517/calling-startactivity-from-outside-of-an-activity-context
                             * Set FLAG_ACTIVITY_NEW_TASK on intent
                             * @TODO: Ask Stocker what the Problem is
                             */
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        } else {
                            loginFragment.mPasswordView.setError("Invalid Password");
                            snackIt("Login fehlgeschlagen.");
                        }
                    }

                    @Override
                    public void onError(String message) {
                        snackIt("Error:" + message);
                    }
                }

        );
    }



    @Override
    public void onStartRegistration() {
        if(loginFragment.mServerView.getText().toString().contains("http://")){
            LibraryService.setServerAddress(loginFragment.mServerView.getText().toString());
            history.push(registerFragment);

            mEmail = loginFragment.mEmailView.getText().toString();
            mPassword = loginFragment.mPasswordView.getText().toString();

            setTitle(R.string.title_activity_register);
            getFragmentManager().beginTransaction().replace(R.id.login_fragment_container, registerFragment).commit();
        } else {
            loginFragment.mServerView.setError("This field is Required");
        }
    }

    @Override
    public String getEmail() {
        return mEmail;
    }

    @Override
    public String getPassword() {
        return mPassword;
    }

    @Override
    public void onAttemptRegistration(String email, String password, String name, String studentnumber) {
        LibraryService.register(email, password,name,studentnumber,

                new Callback<Boolean>() {

                    @Override
                    public void onCompletion(Boolean input) {
                        registerFragment.showProgress(false);
                        if (input) {
                            snackIt("Registration successful");
                            onCancelRegistration();
                        } else {
                            registerFragment.mEmailView.setError(getString(R.string.error_invalid_email));
                            snackIt("Registation failed");
                        }
                    }

                    @Override
                    public void onError(String message) {
                        //TODO: display message in snackbar
                        snackIt("Server-Fehler:" + message);
                        Log.e("Gadgeothek", "Server-Fehler:" + message);
                    }
                }

        );
    }

    @Override
    public void onCancelRegistration() {
        history.push(loginFragment);

        mEmail = registerFragment.mEmailView.getText().toString();
        mPassword = registerFragment.mPasswordView.getText().toString();

        setTitle(R.string.title_activity_login);
        getFragmentManager().beginTransaction().replace(R.id.login_fragment_container, loginFragment).commit();
    }



    private void snackIt(String message) {
        Snackbar snackbar = Snackbar.make (findViewById(R.id.activity_login), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
