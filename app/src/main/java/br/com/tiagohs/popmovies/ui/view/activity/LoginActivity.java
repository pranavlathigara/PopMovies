package br.com.tiagohs.popmovies.ui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONObject;

import java.util.Arrays;

import javax.inject.Inject;

import br.com.tiagohs.popmovies.App;
import br.com.tiagohs.popmovies.BuildConfig;
import br.com.tiagohs.popmovies.R;
import br.com.tiagohs.popmovies.model.db.ProfileDB;
import br.com.tiagohs.popmovies.model.db.UserDB;
import br.com.tiagohs.popmovies.ui.contracts.LoginContract;
import br.com.tiagohs.popmovies.util.PermissionUtils;
import br.com.tiagohs.popmovies.util.PrefsUtils;
import br.com.tiagohs.popmovies.util.ServerUtils;
import br.com.tiagohs.popmovies.util.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity implements LoginContract.LoginView {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final String USER_ID_KEY = "id";
    private static final String USER_NAME_KEY = "name";
    private static final String USER_EMAIL_KEY = "email";
    private static final String USER_PHOTO_KEY = "user_photos";

    @BindView(R.id.login_facebook_button)               Button mLoginFacebookButton;
    @BindView(R.id.login_twitter_button)                Button mLoginTwitterButton;
    @BindView(R.id.login_facebook_button_original)      LoginButton mLoginFacebookOriginalButton;
    @BindView(R.id.login_twitter_button_original)       TwitterLoginButton mLoginTwitterOriginalButton;
    @BindView(R.id.title_app)                           TextView mTitle;

    @Inject
    LoginContract.LoginPresenter mPresenter;

    private CallbackManager mFacebookCallbackManager;

    private TwitterSession mSession;
    private String mUsername;
    private String mEmail;
    private String mName;
    private String mPathFoto;
    private String mToken;

    private int mTypeLogin;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.validate(this, 0);

        onStartConfigurateLoginSDKs();

        if (PrefsUtils.getCurrentUser(LoginActivity.this) != null) {
            startActivity(HomeActivity.newIntent(LoginActivity.this));
            finish();
        }

        ((App) getApplication()).getPopMoviesComponent().inject(this);

        mPresenter.onBindView(this);

        setContentView(R.layout.activity_login);
        onSetupFacebook();
        onSetupTwitter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onUnbindView();
    }

    public void onStartConfigurateLoginSDKs() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    private void onSetupFacebook() {
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mLoginFacebookOriginalButton.registerCallback(mFacebookCallbackManager, facebookCallback());
        mLoginFacebookOriginalButton.setReadPermissions(Arrays.asList(USER_EMAIL_KEY, USER_PHOTO_KEY));

        mLoginFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginFacebookOriginalButton.performClick();
                setFacebookButtonVisibility(View.GONE);
                setTwitterButtonVisibility(View.GONE);
            }
        });
    }

    private void onSetupTwitter() {
        mLoginTwitterOriginalButton.setCallback(twitterCallback());
        mLoginTwitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginTwitterOriginalButton.performClick();
                setTwitterButtonVisibility(View.GONE);
                setFacebookButtonVisibility(View.GONE);
            }
        });
    }

    private FacebookCallback<LoginResult> facebookCallback() {
        mTypeLogin = UserDB.LOGIN_FACEBOOK;

        return new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                mToken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    mName = object.getString(USER_NAME_KEY).toString();
                                    mUsername = object.getString(USER_EMAIL_KEY).toString();
                                    mEmail = object.getString(USER_EMAIL_KEY).toString();

                                    mPathFoto = getString(R.string.facebook_photo, object.getString(USER_ID_KEY).toString());

                                    setUserData();

                                    startActivity(HomeActivity.newIntent(LoginActivity.this));
                                    finish();
                                } catch (Exception e) {
                                    ViewUtils.createToastMessage(LoginActivity.this, getString(R.string.login_facebook_error));
                                    setFacebookButtonVisibility(View.VISIBLE);
                                    setTwitterButtonVisibility(View.VISIBLE);
                                }

                            }

                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", USER_ID_KEY + "," + USER_NAME_KEY + "," + USER_EMAIL_KEY);
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                ViewUtils.createToastMessage(LoginActivity.this, getString(R.string.login_facebook_cancelled));
                setFacebookButtonVisibility(View.VISIBLE);
                setTwitterButtonVisibility(View.VISIBLE);
            }

            @Override
            public void onError(FacebookException e) {
                ViewUtils.createToastMessage(LoginActivity.this, getString(R.string.login_facebook_error));
                setFacebookButtonVisibility(View.VISIBLE);
                setTwitterButtonVisibility(View.VISIBLE);
            }

        };
    }

    private Callback<TwitterSession> twitterCallback() {
        mTypeLogin = UserDB.LOGIN_TWITTER;

        return new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                mSession = result.data;
                mToken = mSession.getAuthToken().token;

                Call<User> call = Twitter.getApiClient(mSession).getAccountService()
                        .verifyCredentials(true, false);
                call.enqueue(new Callback<User>() {

                    @Override
                    public void failure(TwitterException e) {
                        ViewUtils.createToastMessage(LoginActivity.this, getString(R.string.login_twitter_error));
                        setTwitterButtonVisibility(View.VISIBLE);
                        setFacebookButtonVisibility(View.VISIBLE);
                    }
                    @Override
                    public void success(Result<User> userResult) {
                        try {
                            final User user = userResult.data;
                            mUsername = user.name;
                            mName = user.screenName;
                            mPathFoto = user.profileImageUrl;

                            setUserData();

                            startActivity(HomeActivity.newIntent(LoginActivity.this));
                            finish();
                        } catch (Exception e) {
                            ViewUtils.createToastMessage(LoginActivity.this, getString(R.string.login_twitter_error));
                            setTwitterButtonVisibility(View.VISIBLE);
                            setFacebookButtonVisibility(View.VISIBLE);
                        }

                    }

                });

            }

            @Override
            public void failure(TwitterException exception) {
                ViewUtils.createToastMessage(LoginActivity.this, getString(R.string.login_twitter_error));
                setTwitterButtonVisibility(View.VISIBLE);
                setFacebookButtonVisibility(View.VISIBLE);
            }
        };
    }


    private void setUserData() {
        mPresenter.onSaveProfile(mUsername, mEmail, mName, mTypeLogin, mToken, mPathFoto, UserDB.PHOTO_ONLINE);
    }

    @Override
    public boolean isInternetConnected() {
        return ServerUtils.isNetworkConnected(this);
    }

    public void onSaveInSharedPreferences(ProfileDB profileDB) {
        PrefsUtils.setCurrentProfile(profileDB, this);
        PrefsUtils.setCurrentUser(profileDB.getUser(), this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        mLoginTwitterOriginalButton.onActivityResult(requestCode, resultCode, data);
    }

    private void setFacebookButtonVisibility(int visibility) {
        mLoginFacebookButton.setVisibility(visibility);
    }

    private void setTwitterButtonVisibility(int visibility) {
        mLoginTwitterButton.setVisibility(visibility);
    }

}