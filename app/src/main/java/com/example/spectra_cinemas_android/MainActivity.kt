package com.example.spectra_cinemas_android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.spectra_cinemas_android.databinding.ActivityMainBinding
import com.example.spectra_cinemas_android.fragments.*
import com.example.spectra_cinemas_android.models.Movie
import com.example.spectra_cinemas_android.database.AppDatabase
import com.example.spectra_cinemas_android.utils.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.color.DynamicColors
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var isLoggedIn = false
    var isAdmin = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            NotificationHelper.createNotificationChannel(this)
        }
    }
    
    // ΜΝΗΜΗ ΚΡΑΤΗΣΗΣ
    var pendingMovie: Movie? = null
    var pendingCinema: String = ""
    var pendingDate: String = ""
    var pendingTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivitiesIfAvailable(this.application)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNotificationPermission()
        
        // Αρχικοποίηση δεδομένων στη Room αν είναι η πρώτη φορά
        initializeDatabase()

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

        NotificationHelper.createNotificationChannel(this)

        if (savedInstanceState == null) {
            handleIntent(intent)
        }
    }

    private fun initializeDatabase() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            // 1. Πάντα προσπαθούμε να βάλουμε τα σινεμά (το REPLACE θα τα αφήσει αν υπάρχουν)
            // Αυτό διασφαλίζει ότι υπάρχουν τα Foreign Keys για τις αίθουσες
            CinemaData.getAllCinemas().forEach { db.appDao().insertCinema(it) }

            // 2. Για τους υπόλοιπους πίνακες
            // Σνακ: Πάντα ενημέρωση των βασικών
            CanteenData.getAllSnacks().forEach { db.appDao().insertSnack(it) }
            
            // Αίθουσες: Πάντα ενημέρωση των βασικών (όπως έκανες στα σινεμά)
            HallData.getHalls().forEach { db.appDao().insertHall(it) }

            if (db.appDao().getAllOffices().isEmpty()) {
                ContactData.getOffices().forEach { db.appDao().insertOffice(it) }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: android.content.Intent) {
        val goToTicket = intent.getBooleanExtra("GO_TO_TICKET", false)
        val orderId = intent.getStringExtra("ORDER_ID")

        if (goToTicket && orderId != null) {
            // Αναζήτηση της κράτησης στο Cloud και μετάβαση
            openTicketFromNotification(orderId)
        } else {
            replaceFragment(MoviesFragment(), "Ταινίες")
            binding.navView.setCheckedItem(R.id.nav_movies)
        }
    }

    private fun openTicketFromNotification(orderId: String) {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(uid)
            .collection("bookings").document(orderId.replace("#", ""))
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val ticket = com.example.spectra_cinemas_android.models.Ticket(
                        doc.getString("orderId") ?: "",
                        doc.getString("movieTitle") ?: "",
                        doc.getString("cinemaName") ?: "",
                        doc.getString("hallName") ?: "",
                        doc.getString("date") ?: "",
                        doc.getString("time") ?: "",
                        doc.getString("seats") ?: "",
                        doc.getString("price") ?: "",
                        doc.getString("snacks") ?: "",
                        doc.getString("paymentStatus") ?: ""
                    )
                    replaceFragment(BookingFinalFragment.newInstanceFromHistory(ticket), "Εισιτήριο")
                }
            }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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
        menu.findItem(R.id.nav_profile).isVisible = isLoggedIn && !isAdmin
        menu.findItem(R.id.nav_logout).isVisible = isLoggedIn
        menu.findItem(R.id.nav_admin).isVisible = isAdmin
        menu.findItem(R.id.nav_history).isVisible = isLoggedIn && !isAdmin
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
            R.id.nav_admin -> replaceFragment(AdminDatabaseFragment(), "Διαχείριση Βάσης")
            R.id.nav_login -> replaceFragment(LoginFragment(), "Σύνδεση")
            R.id.nav_register -> replaceFragment(RegisterFragment(), "Εγγραφή")
            R.id.nav_logout -> {
                Firebase.auth.signOut()
                isLoggedIn = false
                isAdmin = false
                updateMenuVisibility()
                NotificationHelper.sendNotification(this, "Αποσύνδεση", "Αποσυνδεθήκατε επιτυχώς από το λογαριασμό σας.")
                replaceFragment(MoviesFragment(), "Ταινίες")
            }
            R.id.nav_profile -> replaceFragment(ProfileFragment(), "Προφίλ")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun setLoggedIn(status: Boolean, asAdmin: Boolean = false) {
        isLoggedIn = status
        isAdmin = asAdmin
        updateMenuVisibility()
    }

    fun setUIForBooking(isBooking: Boolean) {
        if (isBooking) {
            binding.btnOpenDrawer.visibility = View.GONE
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
        } else {
            binding.btnOpenDrawer.visibility = View.VISIBLE
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START)
        }
        applyImmersiveMode()
    }

    fun replaceFragment(fragment: Fragment, title: String) {
        val isBookingFlow = fragment is SeatSelectionFragment || 
                           fragment is BookingAuthFragment || 
                           fragment is BookingCanteenFragment || 
                           fragment is BookingPaymentFragment
        
        setUIForBooking(isBookingFlow)
        applyImmersiveMode()

        // Ενημέρωση επιλογής στο μενού
        val menuId = when (fragment) {
            is MoviesFragment -> R.id.nav_movies
            is CinemasFragment -> R.id.nav_cinemas
            is HallsFragment -> R.id.nav_halls
            is ComingSoonFragment -> R.id.nav_coming_soon
            is CanteenFragment -> R.id.nav_canteen
            is ContactFragment -> R.id.nav_contact
            is HistoryFragment -> R.id.nav_history
            is LoginFragment -> R.id.nav_login
            is RegisterFragment -> R.id.nav_register
            is ProfileFragment -> R.id.nav_profile
            else -> null
        }
        menuId?.let { 
            binding.navView.menu.findItem(it)?.isChecked = true
            binding.navView.setCheckedItem(it) 
        }

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
        val isBookingFlow = currentFragment is SeatSelectionFragment || 
                           currentFragment is BookingAuthFragment || 
                           currentFragment is BookingCanteenFragment || 
                           currentFragment is BookingPaymentFragment
        setUIForBooking(isBookingFlow)
    }
}
