package com.example.spectra_cinemas_android

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.databinding.ActivityMainBinding
import com.example.spectra_cinemas_android.fragments.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var isLoggedIn = false // Προσωρινή μεταβλητή μέχρι να μπει το Firebase Auth

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

        updateMenuVisibility()

        if (savedInstanceState == null) {
            replaceFragment(MoviesFragment(), "Ταινίες")
            binding.navView.setCheckedItem(R.id.nav_movies)
        }
    }

    private fun updateMenuVisibility() {
        val menu = binding.navView.menu
        
        // Όταν ΔΕΝ είναι συνδεδεμένος
        menu.findItem(R.id.nav_login).isVisible = !isLoggedIn
        menu.findItem(R.id.nav_register).isVisible = !isLoggedIn
        
        // Όταν ΕΙΝΑΙ συνδεδεμένος
        menu.findItem(R.id.nav_profile).isVisible = isLoggedIn
        menu.findItem(R.id.nav_logout).isVisible = isLoggedIn
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_movies -> replaceFragment(MoviesFragment(), "Ταινίες")
            R.id.nav_coming_soon -> replaceFragment(ComingSoonFragment(), "Προσεχώς")
            R.id.nav_cinemas -> replaceFragment(CinemasFragment(), "Κινηματογράφοι")
            R.id.nav_canteen -> replaceFragment(CanteenFragment(), "Κυλικείο")
            R.id.nav_halls -> replaceFragment(HallsFragment(), "Αίθουσες")
            R.id.nav_contact -> replaceFragment(ContactFragment(), "Επικοινωνία")
            R.id.nav_history -> replaceFragment(HistoryFragment(), "Ιστορικό")
            
            R.id.nav_login -> replaceFragment(LoginFragment(), "Σύνδεση")
            R.id.nav_register -> replaceFragment(RegisterFragment(), "Εγγραφή")
            
            R.id.nav_logout -> {
                isLoggedIn = false
                updateMenuVisibility()
                replaceFragment(MoviesFragment(), "Ταινίες")
            }
            R.id.nav_profile -> {
                replaceFragment(ProfileFragment(), "Προφίλ")
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Προσθήκη μεθόδου για να αλλάζουμε το login status από τα Fragments
    fun setLoggedIn(status: Boolean) {
        isLoggedIn = status
        updateMenuVisibility()
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
