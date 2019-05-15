package com.spybike.integratedtrackers.views

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.spybike.integratedtrackers.R
import com.spybike.integratedtrackers.utils.AppConstants
import com.spybike.integratedtrackers.utils.PreferenceHelper
import com.spybike.integratedtrackers.utils.PreferenceHelper.customPrefs
import com.spybike.integratedtrackers.views.fragments.MapFragment
import com.spybike.integratedtrackers.viewvmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var mAccountMenu: MenuItem
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        initView(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        customPrefs(this).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        customPrefs(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun initView(savedInstanceState: Bundle?) {

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            nav_view.setCheckedItem(R.id.nav_home)
            showGoogleMap()
        }

        subscribeMainLiveData()
    }

    private fun subscribeMainLiveData() {
        viewModel.getLoginLiveData().observe(this, Observer {
            if (it == null){
                logOut()
            }else{
                if (it.containsKey("error")){
                    Toast.makeText(this, it["error"], Toast.LENGTH_LONG).show()
                    logOut()
                }else{
                    PreferenceHelper.initCookies(it)
                    customPrefs(this).edit().putString(AppConstants.SHARED_USER, it[AppConstants.SHARED_USER]).apply()
                    customPrefs(this).edit().putString(AppConstants.SHARED_PASSWORD, it[AppConstants.SHARED_PASSWORD]).apply()
                    showGoogleMap()
                }
            }
        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        mAccountMenu = menu.findItem(R.id.action_menu_settings)
        if (customPrefs(this).getString(AppConstants.SHARED_USER, "") == ""){
            mAccountMenu.icon = ContextCompat.getDrawable(this, R.drawable.ic_action_login)
        }else{
            mAccountMenu.icon = ContextCompat.getDrawable(this, R.drawable.ic_action_no_login)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_menu_settings ->
                if (customPrefs(this).getString(AppConstants.SHARED_USER, "") == "") {
                    showLoginDialog()
                }else{
                    logOut()
                }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logOut(): Boolean {
        return customPrefs(this).edit().putString(AppConstants.SHARED_USER, "").commit()
    }

    private fun showLoginDialog(): Boolean {
        val alert = AlertDialog.Builder(this)
        val layout = LinearLayout(applicationContext)
        layout.setPadding(8, 8, 8, 8)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.layoutParams = params
        layout.orientation = LinearLayout.VERTICAL
        layout.clipToPadding = true

        alert.setTitle("Авторизація")
        alert.setMessage("Please enter your login and password")

        val login = AppCompatEditText(this)
        login.inputType = InputType.TYPE_CLASS_TEXT
        login.hint = "Login"
        login.layoutParams = params

        val password = AppCompatEditText(this)
        password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        password.hint = "Password"
        password.layoutParams = params

        layout.addView(login)
        layout.addView(password)
        alert.setView(layout)

        alert.setPositiveButton("Ok") { _, _ ->
            // Do something with value!
            //if (login.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
                viewModel.login("asteryx", "xyretsa1")
            //}
        }

        alert.setNegativeButton("Cancel", { _, _ ->
            // Canceled.
        })
        alert.show()
        return customPrefs(this).edit().putString(AppConstants.SHARED_NAME, "asteryx").commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                showGoogleMap()
            }
            R.id.nav_battery -> {

            }
            R.id.nav_gsm -> {

            }
            R.id.nav_velocity -> {

            }
            R.id.nav_filter -> {

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSharedPreferenceChanged(sh: SharedPreferences?, key: String?) {
        if (key == AppConstants.SHARED_USER){
            if (sh?.getString(key,"") == "") {
                mAccountMenu.icon = ContextCompat.getDrawable(this, R.drawable.ic_action_login)
                sh.edit().putString(AppConstants.SHARED_PASSWORD, "").apply()
            }else{
                mAccountMenu.icon = ContextCompat.getDrawable(this, R.drawable.ic_action_no_login)
            }
        }
    }

    private fun showGoogleMap() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, MapFragment.newInstance(), "GoogleMap")
            .commit()
    }
}
