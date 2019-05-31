package com.spybike.integratedtrackers.views

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.spybike.integratedtrackers.R
import com.spybike.integratedtrackers.adapters.DevicesSpinnerAdapter
import com.spybike.integratedtrackers.enums.TagsFragment
import com.spybike.integratedtrackers.utils.AppConstants
import com.spybike.integratedtrackers.utils.PreferenceHelper
import com.spybike.integratedtrackers.utils.PreferenceHelper.customPrefs
import com.spybike.integratedtrackers.views.fragments.FilterFragment
import com.spybike.integratedtrackers.views.fragments.ImageFragment
import com.spybike.integratedtrackers.views.fragments.MapFragment
import com.spybike.integratedtrackers.viewvmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    var mAccountMenu: MenuItem? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        initView(savedInstanceState)

        subscribeMainLiveData()

    }

    override fun onResume() {
        super.onResume()
        customPrefs(this).registerOnSharedPreferenceChangeListener(this)
        val cookie = customPrefs(this).getString(AppConstants.SHARED_COOKIES, "")!!
        if (cookie.isNotEmpty()){
            val mapCookies = HashMap<String, String>()
            mapCookies["JSESSIONID"] = cookie
            PreferenceHelper.initCookies(this, mapCookies)
            viewModel.updateUserInfo()
            viewModel.updateDevicesUserInfo()
        }
    }

    override fun onPause() {
        super.onPause()
        customPrefs(this).unregisterOnSharedPreferenceChangeListener(this)
//        PreferenceHelper.initCookies(this,null)
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

        val adapter = DevicesSpinnerAdapter(this, R.layout.row, ArrayList())
        nav_view.getHeaderView(0).spinnerDeviceIDs.adapter = adapter
        nav_view.getHeaderView(0).spinnerDeviceIDs.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setSelectDeviceUser((parent?.adapter as DevicesSpinnerAdapter).listData[position])
            }
        }

        if (savedInstanceState == null) {
            nav_view.setCheckedItem(R.id.nav_home)
            showFragment(MapFragment.newInstance(), "GoogleMap")
        }
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
                    PreferenceHelper.initCookies(this, it)
                    viewModel.updateUserInfo()
                    viewModel.updateDevicesUserInfo()
                }
            }
        })

        viewModel.getUserInfoLiveData().observe(this, Observer {
            if (it != null){
                nav_view.getHeaderView(0).name_user.text = "User name: ${it.userName}"
                nav_view.getHeaderView(0).balance_user.text = "Balance ${it.balance}${it.currency}"
            }else{
                nav_view.getHeaderView(0).name_user.text = "Please login"
                nav_view.getHeaderView(0).balance_user.text = ""
            }
        })

        viewModel.getUserDevicesLiveData().observe(this, Observer {
            if (it != null){
                nav_view.getHeaderView(0).textDevices.visibility = View.VISIBLE
                nav_view.getHeaderView(0).spinnerDeviceIDs.visibility = View.VISIBLE
                (nav_view.getHeaderView(0).spinnerDeviceIDs.adapter as DevicesSpinnerAdapter).setDataList(it)
            }else{
                nav_view.getHeaderView(0).textDevices.visibility = View.GONE
                nav_view.getHeaderView(0).spinnerDeviceIDs.visibility = View.GONE
                (nav_view.getHeaderView(0).spinnerDeviceIDs.adapter as DevicesSpinnerAdapter).clearDataList()
            }
        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage("Do you want to exit the application?")
            alertDialogBuilder.setPositiveButton("Yes"
            ) { _, _ -> this@MainActivity.finish() }
            alertDialogBuilder.setNegativeButton("No"
            ) { dialog, _ -> dialog.cancel() }
            alertDialogBuilder.create().show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        mAccountMenu = menu.findItem(R.id.action_menu_settings)
        if (customPrefs(this).getString(AppConstants.SHARED_COOKIES, "") == ""){
            mAccountMenu?.icon = ContextCompat.getDrawable(this, R.drawable.ic_action_login)
        }else{
            mAccountMenu?.icon = ContextCompat.getDrawable(this, R.drawable.ic_action_no_login)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_menu_settings ->
                if (customPrefs(this).getString(AppConstants.SHARED_COOKIES, "") == "") {
                    showLoginDialog()
                    return true
                }else{
                    logOut()
                }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logOut(): Boolean {
        nav_view.getHeaderView(0).textDevices.visibility = View.GONE
        nav_view.getHeaderView(0).spinnerDeviceIDs.visibility = View.GONE
        (nav_view.getHeaderView(0).spinnerDeviceIDs.adapter as DevicesSpinnerAdapter).clearDataList()
        nav_view.getHeaderView(0).name_user.text = "Please login"
        nav_view.getHeaderView(0).balance_user.text = ""
        return customPrefs(this).edit().putString(AppConstants.SHARED_COOKIES, "").commit()
    }

    private fun showLoginDialog() {
        val alert = AlertDialog.Builder(this)
        val layout = LinearLayout(applicationContext)
        layout.setPadding(8, 8, 8, 8)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 8, 16, 8)
        layout.layoutParams = params
        layout.orientation = LinearLayout.VERTICAL
        layout.clipToPadding = true

        alert.setTitle("LogIn")
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
            if (login.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
                viewModel.login(login.text.toString(), password.text.toString())
            }
        }

        alert.setNegativeButton("Cancel", { _, _ ->
            // Canceled.
        })
        alert.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        var tag: String? = null
        when (item.itemId) {
            R.id.nav_home -> {
                fragment = MapFragment.newInstance()
                tag = TagsFragment.MAPS.name
            }
            R.id.nav_statistic -> {
                fragment = ImageFragment.newInstance()
                tag = TagsFragment.BATTERY.name
            }
            R.id.nav_filter -> {
                fragment = FilterFragment.newInstance()
                tag = TagsFragment.FILTER.name
            }
        }
        if (fragment != null) {
            showFragment(fragment, tag)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSharedPreferenceChanged(sh: SharedPreferences?, key: String?) {
        if (key == AppConstants.SHARED_COOKIES){
            if (sh?.getString(key,"") == "") {
                mAccountMenu?.icon = ContextCompat.getDrawable(this, R.drawable.ic_action_login)
            }else{
                mAccountMenu?.icon = ContextCompat.getDrawable(this, R.drawable.ic_action_no_login)
            }
        }
    }

    private fun showFragment(fragment: Fragment, tag: String?) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }

}
