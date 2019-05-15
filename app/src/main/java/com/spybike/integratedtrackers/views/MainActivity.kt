package com.spybike.integratedtrackers.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.spybike.integratedtrackers.R
import com.spybike.integratedtrackers.views.fragments.MapFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_home)
        if (savedInstanceState == null){
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
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


    private fun showGoogleMap() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, MapFragment.newInstance(), "GoogleMap")
            .addToBackStack(null)
            .commit()
    }
}
