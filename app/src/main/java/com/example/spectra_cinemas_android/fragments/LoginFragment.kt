package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.databinding.LoginViewBinding
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

        binding.btnLogin.isEnabled = false
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Επιτυχής είσοδος και το email είναι επαληθευμένο
                        Toast.makeText(requireContext(), "Καλώς ήρθατε!", Toast.LENGTH_SHORT).show()
                        (activity as? MainActivity)?.let { mainActivity ->
                            mainActivity.setLoggedIn(true)
                            mainActivity.replaceFragment(MoviesFragment(), "Ταινίες")
                        }
                    } else {
                        // Το email δεν έχει επαληθευτεί
                        auth.signOut()
                        Toast.makeText(requireContext(), "Παρακαλώ επαληθεύστε το email σας πριν συνδεθείτε.", Toast.LENGTH_LONG).show()
                        binding.btnLogin.isEnabled = true
                    }
                } else {
                    // Αποτυχία εισόδου
                    Toast.makeText(requireContext(), "Σφάλμα σύνδεσης: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    binding.btnLogin.isEnabled = true
                }
            }
    }

    private fun resetPassword() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Εισάγετε το email σας για επαναφορά κωδικού", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Ο σύνδεσμος επαναφοράς στάλθηκε στο email σας", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Σφάλμα: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
