package com.example.spectra_cinemas_android

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.databinding.ActivityMainBinding
import com.example.spectra_cinemas_android.fragments.MoviesFragment
import com.example.spectra_cinemas_android.fragments.ComingSoonFragment
import com.example.spectra_cinemas_android.fragments.CinemasFragment
import com.example.spectra_cinemas_android.fragments.CanteenFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            replaceFragment(MoviesFragment(), "Ταινίες")
            binding.navView.setCheckedItem(R.id.nav_movies)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_movies -> replaceFragment(MoviesFragment(), "Ταινίες")
            R.id.nav_coming_soon -> replaceFragment(ComingSoonFragment(), "Προσεχώς")
            R.id.nav_cinemas -> replaceFragment(CinemasFragment(), "Κινηματογράφοι")
            R.id.nav_canteen -> replaceFragment(CanteenFragment(), "Κυλικείο")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun replaceFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
        supportActionBar?.title = title
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }
}
