package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.databinding.LoginViewBinding
import com.example.spectra_cinemas_android.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var _binding: LoginViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginViewBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.tvForgotPassword.setOnClickListener {
            resetPassword()
        }

        binding.tvNoAccount.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(RegisterFragment(), "Εγγραφή")
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show()
            return
        }

        // ΕΙΔΙΚΟΣ ΕΛΕΓΧΟΣ ΓΙΑ ADMIN
        if (email == "123456789" && password == "spectra") {
            val mainActivity = (activity as? MainActivity)
            mainActivity?.setLoggedIn(true, asAdmin = true)
            mainActivity?.replaceFragment(AdminDatabaseFragment(), "Διαχείριση Βάσης")
            return
        }

        binding.btnLogin.isEnabled = false
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Επιτυχής είσοδος
                        NotificationHelper.sendNotification(requireContext(), "Σύνδεση", "Συνδεθήκατε επιτυχώς στο λογαριασμό σας.")
                        val mainActivity = (activity as? MainActivity)
                        mainActivity?.setLoggedIn(true)
                        
                        // Δυναμική πλοήγηση: Αν υπάρχει εκκρεμής κράτηση, πήγαινε εκεί
                        if (mainActivity?.pendingMovie != null) {
                            mainActivity.replaceFragment(
                                SeatSelectionFragment.newInstance(
                                    mainActivity.pendingMovie!!,
                                    mainActivity.pendingCinema,
                                    mainActivity.pendingDate,
                                    mainActivity.pendingTime
                                ),
                                "Επιλογή Θέσεων"
                            )
                        } else {
                            mainActivity?.replaceFragment(MoviesFragment(), "Ταινίες")
                        }
                    } else {
                        auth.signOut()
                        Toast.makeText(requireContext(), "Παρακαλώ επαληθεύστε το email σας.", Toast.LENGTH_LONG).show()
                        binding.btnLogin.isEnabled = true
                    }
                } else {
                    Toast.makeText(requireContext(), "Σφάλμα σύνδεσης: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    binding.btnLogin.isEnabled = true
                }
            }
    }

    private fun resetPassword() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Εισάγετε το email σας", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Ο σύνδεσμος επαναφοράς στάλθηκε", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
