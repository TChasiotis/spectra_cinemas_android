package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.databinding.RegisterViewBinding
import com.example.spectra_cinemas_android.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private var _binding: RegisterViewBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegisterViewBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        db = Firebase.firestore
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateSpinners()

        binding.btnRegister.setOnClickListener {
            performRegistration()
        }

        binding.tvHaveAccount.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(LoginFragment(), "Σύνδεση")
        }
    }

    private fun performRegistration() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etRegEmail.text.toString().trim()
        val password = binding.etRegPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        
        val day = binding.spinnerDay.text.toString()
        val monthStr = binding.spinnerMonth.text.toString()
        val yearStr = binding.spinnerYear.text.toString()

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || day.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty()) {
            Toast.makeText(requireContext(), "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show()
            return
        }

        // Έλεγχος ορίου ηλικίας 15+
        if (!DateUtils.isAdult(day.toInt(), monthStr.toInt(), yearStr.toInt())) {
            Toast.makeText(requireContext(), "Η εγγραφή επιτρέπεται μόνο σε άτομα άνω των 15 ετών", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Οι κωδικοί δεν ταιριάζουν", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnRegister.isEnabled = false
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val age = DateUtils.getAge(day.toInt(), monthStr.toInt(), yearStr.toInt())
                    
                    val userProfile = hashMapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "birthDate" to "$day/$monthStr/$yearStr",
                        "age" to age,
                        "cardInfo" to null 
                    )

                    db.collection("users").document(userId).set(userProfile)
                        .addOnSuccessListener {
                            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                                Toast.makeText(requireContext(), "Ελέγξτε το email σας για επαλήθευση.", Toast.LENGTH_LONG).show()
                                (activity as? MainActivity)?.replaceFragment(EmailVerificationFragment(), "Επαλήθευση")
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Σφάλμα: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    binding.btnRegister.isEnabled = true
                }
            }
    }

    private fun setupDateSpinners() {
        val daysAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, DateUtils.getDaysList())
        binding.spinnerDay.setAdapter(daysAdapter)
        val monthsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, DateUtils.getMonthsList())
        binding.spinnerMonth.setAdapter(monthsAdapter)
        val yearsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, DateUtils.getYearsList())
        binding.spinnerYear.setAdapter(yearsAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
