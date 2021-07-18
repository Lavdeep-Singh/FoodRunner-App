package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodrunner.*
import com.example.foodrunner.fragment.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.drawer_header.*
import kotlinx.android.synthetic.main.drawer_header.view.*

class MainActivityFragment : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fragment)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolBar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)

        navigationView.menu.getItem(0).isCheckable = true
        navigationView.menu.getItem(0).isChecked = true

        setUpToolBar()//enabling tool bar to work as action bar
        openDashboardFragment()//dashboard fragment will be first screen in front os user

        val actionBarDrawerToggle= ActionBarDrawerToggle(this@MainActivityFragment,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            previousMenuItem = it
            it.isCheckable = true
            it.isChecked = true

            when (it.itemId) {
                R.id.dashboard -> {
                    //here we are replacing the frame layout with the our com.example.bookhub.fragment's layout
                    supportFragmentManager.beginTransaction().replace(R.id.frame, HomeFragment()).commit()
                    supportActionBar?.title="Home"//changing the title of the toolbar
                    navigationView.setCheckedItem(R.id.dashboard)
                    drawerLayout.closeDrawers()

                    //after the com.example.bookhub.fragment's layout is set,then we close our drawer


                }
                R.id.favourites -> {
                    drawerLayout.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouritesFragment())
                        .commit()
                    supportActionBar?.title = "Favourites"

                }
                R.id.profile -> {
                    drawerLayout.closeDrawers()
                    supportFragmentManager.beginTransaction().replace(R.id.frame, ProfileFragment())
                        .commit()
                    supportActionBar?.title = "Profile"

                }
                R.id.aboutApp -> {
                    drawerLayout.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FaqFragment())
                        .commit()
                    supportActionBar?.title = "FAQ's"

                }
                R.id.orderHistory ->{
                    drawerLayout.closeDrawers()
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        OrderHistoryFragment()
                    )
                        .commit()
                    supportActionBar?.title="Order History"
                }
                R.id.logout ->{
                    drawerLayout.closeDrawers()
                    val dialog= AlertDialog.Builder(this@MainActivityFragment)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to exit?")
                    dialog.setPositiveButton("No"){
                            text,listener->

                    }
                    dialog.setNegativeButton("Yes") { text, listener ->
                        sharedPreferences =
                            getSharedPreferences(getString(R.string.prefrences_file_name), Context.MODE_PRIVATE)
                        startActivity(Intent(this@MainActivityFragment, LoginActivity::class.java))
                        sharedPreferences.edit().putBoolean("dataFile",false).apply()
                        getSharedPreferences("name", MODE_PRIVATE).edit().clear().apply()
                        getSharedPreferences("mobile_number", MODE_PRIVATE).edit().clear().apply()
                        getSharedPreferences("user_id", MODE_PRIVATE).edit().clear().apply()
                        getSharedPreferences("email", MODE_PRIVATE).edit().clear().apply()
                        getSharedPreferences("address", MODE_PRIVATE).edit().clear().apply()
                        Toast.makeText(this@MainActivityFragment,"Logged out", Toast.LENGTH_SHORT).show()
                        finishAffinity()
                    }
                    dialog.create()
                    dialog.show()


                }



            }

            return@setNavigationItemSelectedListener true
        }


    }

    fun setUpToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            val user_name:SharedPreferences =
                getSharedPreferences(getString(R.string.reg_name), Context.MODE_PRIVATE)
            header_user_name.text = (user_name.getString("name","N/A")).toString()

        }
        return super.onOptionsItemSelected(item)

    }

    fun openDashboardFragment(){
        supportFragmentManager.beginTransaction().replace(R.id.frame, HomeFragment()).commit()
        supportActionBar?.title="Home"
        navigationView.setCheckedItem(R.id.dashboard)


    }

    override fun onBackPressed() {
        val myFrag= supportFragmentManager.findFragmentById(R.id.frame)
        when(myFrag){
            !is HomeFragment ->{
                openDashboardFragment()
            }else->{
            super.onBackPressed()
        }
        }
    }


}