/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.fcu.member;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fcu.R;
import com.fcu.photocollage.PhotoMovieMain;

public class LoginActivity extends Fragment {
	// LogCat tag
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private Button btnLogin;
	private Button btnLinkToRegister;
	private EditText inputEmail;
	private EditText inputPassword;
	private ProgressDialog pDialog;
	private SessionManager session;
	private View thisView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.activity_login, container, false);
		
		inputEmail = (EditText) thisView.findViewById(R.id.email);
		inputPassword = (EditText) thisView.findViewById(R.id.password);
		btnLogin = (Button) thisView.findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) thisView.findViewById(R.id.btnLinkToRegisterScreen);

		// Progress dialog
		pDialog = new ProgressDialog(getActivity());
		pDialog.setCancelable(false);

		// Session manager
		session = new SessionManager(getActivity().getApplicationContext());

		// Check if user is already logged in or not
		if (session.isLoggedIn()) {
			// User is already logged in. Take him to main activity
			Fragment fragment = new MemberActivity();
    		FragmentManager fragmentManager = getActivity().getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			//Intent intent = new Intent(getActivity(), MemberActivity.class);
			//startActivity(intent);
			//finish();
		}

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();

				// Check for empty data in the form
				if (email.trim().length() > 0 && password.trim().length() > 0) {
					// login user
					checkLogin(email, password);
				} else {
					// Prompt user to enter credentials
					Toast.makeText(getActivity().getApplicationContext(),
							"Please enter the credentials!", Toast.LENGTH_LONG)
							.show();
				}
			}

		});

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
//				Intent i = new Intent(getActivity().getApplicationContext(),
//						RegisterActivity.class);
//				startActivity(i);				
//				finish();
				Fragment fragment = new RegisterActivity();
	    		FragmentManager fragmentManager = getActivity().getFragmentManager();
	            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			}
		});
		
		return thisView;
	}

	/**
	 * function to verify login details in mysql db
	 * */
	private void checkLogin(final String email, final String password) {
		// Tag used to cancel the request
		String tag_string_req = "req_login";

		pDialog.setMessage("Logging in ...");
		showDialog();

		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_REGISTER, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");

							// Check for error node in json
							if (!error) {
								// user successfully logged in
								// Create login session
								session.setLogin(true);

								// Launch main activity
//								Intent intent = new Intent(LoginActivity.this,
//										MemberActivity.class);
//								startActivity(intent);
//								finish();
								Fragment fragment = new MemberActivity();
					    		FragmentManager fragmentManager = getActivity().getFragmentManager();
					            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
							} else {
								// Error in login. Get the error message
								String errorMsg = jObj.getString("error_msg");
								Toast.makeText(getActivity().getApplicationContext(),
										errorMsg, Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							// JSON error
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Login Error: " + error.getMessage());
						Toast.makeText(getActivity().getApplicationContext(),
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting parameters to login url
				Map<String, String> params = new HashMap<String, String>();
				params.put("tag", "login");
				params.put("email", email);
				params.put("password", password);

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
}
