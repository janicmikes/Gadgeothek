package ch.hsr.mge.gadgeothek;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;

import java.util.Stack;

import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;

public class LoginActivity extends AppCompatActivity implements LoginFragment.IHandleLoginFragment, RegisterFragment.IHandleRegisterFragment {
    LoginFragment loginFragment;
    RegisterFragment registerFragment;

    String mEmail = "";
    String mPassword = "";
    String mServer = "http://mge1.dev.ifs.hsr.ch/public";


    private Stack<Pair<Fragment, String>> history = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();

        setTitle(getString(R.string.title_activity_login));

        // add the starting fragment
        getFragmentManager().beginTransaction().replace(R.id.login_fragment_container, loginFragment).commit();
        history.push(new Pair<Fragment, String>(loginFragment, getString(R.string.title_activity_login)));
    }

    @Override
    public void onBackPressed() {
        if (history.size() > 1) {
            history.pop();
        }
        if (history.size()>0 ) {
            getFragmentManager().beginTransaction().replace(R.id.login_fragment_container, history.peek().first).commit();
            setTitle(history.peek().second);
        }else{
            Log.e("Gadgeothek", "Warning: LoginActivity History was empty in Login Activity. Shouldn't happen. Ignored Gracefully.");
        }
    }

    @Override
    public void onAttemptLogin(final String email, String password) {
        LibraryService.setServerAddress(loginFragment.mServerView.getText().toString());
        LibraryService.login(email, password,
                new Callback<Boolean>() {

                    @Override
                    public void onCompletion(Boolean input) {
                        if (input) {
                            history.clear();

                            setPassword("");

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra(MainActivity.ARG_LOGIN_EMAIL, email);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        } else {
                            loginFragment.mPasswordView.setError("Invalid Password");
                            snackIt("Login fehlgeschlagen.");
                        }
                    }

                    @Override
                    public void onError(String message) {
                        if (message.equals("incorrect password")){
                            loginFragment.mPasswordView.setError("Invalid Password");
                            loginFragment.mPasswordView.requestFocus();
                        } else if (message.equals("user does not exist")){
                            loginFragment.mEmailView.setError("This user is not registered");
                            loginFragment.mEmailView.requestFocus();
                        }
                        snackIt("Error: " + message);
                    }
                }

        );
    }



    @Override
    public void onStartRegistration() {
        if(loginFragment.mServerView.getText().toString().contains("http://")){
            LibraryService.setServerAddress(loginFragment.mServerView.getText().toString());

            history.push(new Pair<Fragment, String>(registerFragment, getString(R.string.title_activity_register)));

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
    public String getServer() {
        return mServer;
    }

    @Override
    public void setEmail(String email) {
        mEmail = email;
    }

    @Override
    public void setPassword(String password) {
        mPassword = password;
    }

    @Override
    public void setServer(String server) {
        mServer = server;
    }

    @Override
    public void onAttemptRegistration(String email, String password, String name, String studentnumber) {
        LibraryService.register(email, password,name,studentnumber,

                new Callback<Boolean>() {

                    @Override
                    public void onCompletion(Boolean input) {
                        if (input) {
                            snackIt("Registration successful");
                            onCancelRegistration();
                        } else {
                            registerFragment.mEmailView.setError(getString(R.string.error_invalid_email));
                            snackIt("Registration failed");
                        }
                    }

                    @Override
                    public void onError(String message) {
                        snackIt("Server-Fehler:" + message);
                    }
                }

        );
    }

    @Override
    public void onCancelRegistration() {
        history.push(new Pair<Fragment, String>(loginFragment, getString(R.string.title_activity_login)));

        setTitle(R.string.title_activity_login);
        getFragmentManager().beginTransaction().replace(R.id.login_fragment_container, loginFragment).commit();
    }



    private void snackIt(String message) {
        Snackbar snackbar = Snackbar.make (findViewById(R.id.activity_login), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
