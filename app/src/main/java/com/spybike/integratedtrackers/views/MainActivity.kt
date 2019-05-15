package com.spybike.integratedtrackers.views

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.spybike.integratedtrackers.App
import com.spybike.integratedtrackers.R
import com.spybike.integratedtrackers.utils.AppConstants
import com.spybike.integratedtrackers.views.fragments.MapFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var mAccountMenu: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        (application as App).getSharedPref().registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        (application as App).getSharedPref().unregisterOnSharedPreferenceChangeListener(this)
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
        if ((application as App).getSharedPref().getString(AppConstants.SHARED_NAME, "") == ""){
            mAccountMenu.icon = ContextCompat.getDrawable(this, R.drawable.ic_action)
        }else{
            mAccountMenu.icon = ContextCompat.getDrawable(this, R.drawable.ic_action_login)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_menu_settings ->
                if ((application as App).getSharedPref().getString(AppConstants.SHARED_NAME, "") == "") {
                    showLoginDialog()
                }else{
                    logOut()
                }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logOut(): Boolean {
        return (application as App).getSharedPref().edit().putString(AppConstants.SHARED_NAME, "").commit()
    }

    private fun showLoginDialog(): Boolean {
        return (application as App).getSharedPref().edit().putString(AppConstants.SHARED_NAME, "asteryx").commit()
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
        if (key == AppConstants.SHARED_NAME){
            if (sh?.getString(key,"") == "") {
                mAccountMenu.icon = ContextCompat.getDrawable(this, R.drawable.ic_action)
                sh.edit().putString(AppConstants.SHARED_PASSWORD, "").apply()
            }else{
                mAccountMenu.icon = ContextCompat.getDrawable(this, R.drawable.ic_action_login)
            }
        }
    }

    private fun showGoogleMap() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, MapFragment.newInstance(), "GoogleMap")
            .addToBackStack(null)
            .commit()
    }
}
