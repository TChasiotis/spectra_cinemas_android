package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.databinding.EmailVerificationViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailVerificationFragment : Fragment() {

    private var _binding: EmailVerificationViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EmailVerificationViewBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCheckStatus.setOnClickListener {
            checkVerificationStatus()
        }

        binding.tvResendEmail.setOnClickListener {
            resendVerificationEmail()
        }

        binding.tvBackToRegister.setOnClickListener {
            auth.signOut()
            (activity as? MainActivity)?.replaceFragment(RegisterFragment(), "Εγγραφή")
        }
    }

    private fun checkVerificationStatus() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnCheckStatus.isEnabled = false

        auth.currentUser?.reload()?.addOnCompleteListener { task ->
            binding.progressBar.visibility = View.GONE
            binding.btnCheckStatus.isEnabled = true

            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null && user.isEmailVerified) {
                    Toast.makeText(requireContext(), "Η επαλήθευση ολοκληρώθηκε!", Toast.LENGTH_SHORT).show()
                    
                    val mainActivity = (activity as? MainActivity)
                    mainActivity?.setLoggedIn(true)
                    
                    // ΕΠΙΣΤΡΟΦΗ ΣΤΗΝ ΚΡΑΤΗΣΗ
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
                    Toast.makeText(requireContext(), "Το email δεν έχει επαληθευτεί ακόμα.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Το email στάλθηκε ξανά!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
