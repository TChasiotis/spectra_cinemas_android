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

        binding.switchAddCard.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutCardDetails.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

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
        val month = binding.spinnerMonth.text.toString()
        val year = binding.spinnerYear.text.toString()

        // Βασικοί έλεγχοι
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || day.isEmpty() || month.isEmpty() || year.isEmpty()) {
            Toast.makeText(requireContext(), "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show()
            return
        }

        if (!DateUtils.isAdult(day.toInt(), month.toInt(), year.toInt())) {
            Toast.makeText(requireContext(), "Η εγγραφή επιτρέπεται μόνο σε άτομα άνω των 18 ετών", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Οι κωδικοί δεν ταιριάζουν", Toast.LENGTH_SHORT).show()
            return
        }

        // Έλεγχος κάρτας αν είναι ενεργός ο διακόπτης
        var cardInfo: Map<String, String>? = null
        if (binding.switchAddCard.isChecked) {
            val cardNumber = binding.etCardNumber.text.toString().trim()
            val expiry = binding.etExpiryDate.text.toString().trim()
            val cvv = binding.etCVV.text.toString().trim()

            if (cardNumber.length < 16 || expiry.isEmpty() || cvv.length < 3) {
                Toast.makeText(requireContext(), "Παρακαλώ συμπληρώστε σωστά τα στοιχεία της κάρτας", Toast.LENGTH_SHORT).show()
                return
            }
            cardInfo = mapOf(
                "cardNumber" to cardNumber,
                "expiry" to expiry,
                "cvv" to cvv
            )
        }

        // --- Firebase Registration ---
        binding.btnRegister.isEnabled = false // Απενεργοποίηση για αποφυγή διπλών κλικ
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    
                    // Αποθήκευση επιπλέον στοιχείων στο Firestore
                    val userProfile = hashMapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "birthDate" to "$day/$month/$year",
                        "cardInfo" to cardInfo
                    )

                    db.collection("users").document(userId)
                        .set(userProfile)
                        .addOnSuccessListener {
                            // Αποστολή Verification Email
                            auth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener {
                                    Toast.makeText(requireContext(), "Ο λογαριασμός δημιουργήθηκε! Παρακαλώ ελέγξτε το email σας για επαλήθευση.", Toast.LENGTH_LONG).show()
                                    (activity as? MainActivity)?.replaceFragment(EmailVerificationFragment(), "Επαλήθευση")
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Σφάλμα βάσης: ${e.message}", Toast.LENGTH_SHORT).show()
                            binding.btnRegister.isEnabled = true
                        }
                } else {
                    Toast.makeText(requireContext(), "Σφάλμα εγγραφής: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
