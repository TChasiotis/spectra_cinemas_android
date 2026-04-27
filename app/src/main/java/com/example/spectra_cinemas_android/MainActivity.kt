package com.example.spectra_cinemas_android

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.os.Build
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.databinding.ActivityMainBinding
import com.example.spectra_cinemas_android.fragments.*
import com.example.spectra_cinemas_android.models.Movie
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var isLoggedIn = false
    
    // ΜΝΗΜΗ ΚΡΑΤΗΣΗΣ
    var pendingMovie: Movie? = null
    var pendingCinema: String = ""
    var pendingDate: String = ""
    var pendingTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Συγχρονισμός με το Firebase για να μην μπερδεύεται η ροή
        isLoggedIn = Firebase.auth.currentUser != null && Firebase.auth.currentUser?.isEmailVerified == true

        setSupportActionBar(binding.toolbar)
        supportActionBar?.hide()
        binding.toolbar.visibility = View.GONE
        
        applyImmersiveMode()

        binding.btnOpenDrawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

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

    @Suppress("DEPRECATION")
    fun applyImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun updateMenuVisibility() {
        val menu = binding.navView.menu
        menu.findItem(R.id.nav_login).isVisible = !isLoggedIn
        menu.findItem(R.id.nav_register).isVisible = !isLoggedIn
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
                Firebase.auth.signOut()
                isLoggedIn = false
                updateMenuVisibility()
                replaceFragment(MoviesFragment(), "Ταινίες")
            }
            R.id.nav_profile -> replaceFragment(ProfileFragment(), "Προφίλ")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun setLoggedIn(status: Boolean) {
        isLoggedIn = status
        updateMenuVisibility()
    }

    fun setUIForBooking(isBooking: Boolean) {
        if (isBooking) {
            binding.btnOpenDrawer.visibility = View.GONE
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            binding.btnOpenDrawer.visibility = View.VISIBLE
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
        }
        applyImmersiveMode()
    }

    fun replaceFragment(fragment: Fragment, title: String) {
        val isSelection = fragment is SeatSelectionFragment || fragment is BookingAuthFragment
        setUIForBooking(isSelection)
        applyImmersiveMode()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack()
                binding.root.post { updateUIBasedOnCurrentFragment() }
            } else {
                super.onBackPressed()
            }
        }
    }
    
    private fun updateUIBasedOnCurrentFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        val isSelection = currentFragment is SeatSelectionFragment || currentFragment is BookingAuthFragment
        setUIForBooking(isSelection)
    }
}
