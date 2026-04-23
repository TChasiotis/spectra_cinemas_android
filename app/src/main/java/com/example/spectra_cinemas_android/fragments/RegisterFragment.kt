package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.R
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
        setupCardLogic()

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

    private fun setupCardLogic() {
        // Card Number Formatting & Detection
        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val original = s.toString().replace(" ", "")
                val formatted = StringBuilder()
                for (i in original.indices) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ")
                    formatted.append(original[i])
                }
                
                if (formatted.toString() != s.toString()) {
                    binding.etCardNumber.removeTextChangedListener(this)
                    binding.etCardNumber.setText(formatted.toString())
                    binding.etCardNumber.setSelection(formatted.length)
                    binding.etCardNumber.addTextChangedListener(this)
                }

                // Detect Card Type
                val logo = when {
                    original.startsWith("4") -> R.drawable.p_visa
                    original.matches(Regex("^(5[1-5]|2[2-7]).*")) -> R.drawable.p_mastercard
                    original.startsWith("34") || original.startsWith("37") -> R.drawable.p_american_express
                    else -> R.drawable.p_card_default
                }
                binding.etCardNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, logo, 0)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Expiry Date Formatting (MM/YY)
        binding.etExpiryDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2 && !s.contains("/")) {
                    binding.etExpiryDate.setText("$s/")
                    binding.etExpiryDate.setSelection(3)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
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

        if (!DateUtils.isAdult(day.toInt(), monthStr.toInt(), yearStr.toInt())) {
            Toast.makeText(requireContext(), "Η εγγραφή επιτρέπεται μόνο σε άτομα άνω των 18 ετών", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Οι κωδικοί δεν ταιριάζουν", Toast.LENGTH_SHORT).show()
            return
        }

        var cardInfo: Map<String, String>? = null
        if (binding.switchAddCard.isChecked) {
            val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
            val expiry = binding.etExpiryDate.text.toString()
            val cvv = binding.etCVV.text.toString()

            if (!validateCard(cardNumber, expiry, cvv)) return
            
            cardInfo = mapOf(
                "cardNumber" to cardNumber,
                "expiry" to expiry,
                "cvv" to cvv
            )
        }

        binding.btnRegister.isEnabled = false
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val userProfile = hashMapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "birthDate" to "$day/$monthStr/$yearStr",
                        "cardInfo" to cardInfo
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

    private fun validateCard(number: String, expiry: String, cvv: String): Boolean {
        if (number.length < 15) {
            Toast.makeText(requireContext(), "Μη έγκυρος αριθμός κάρτας", Toast.LENGTH_SHORT).show()
            return false
        }
        val isAmex = number.startsWith("34") || number.startsWith("37")
        if ((isAmex && number.length != 15) || (!isAmex && number.length != 16)) {
            Toast.makeText(requireContext(), "Λάθος αριθμός ψηφίων κάρτας", Toast.LENGTH_SHORT).show()
            return false
        }
        if (expiry.length != 5) {
            Toast.makeText(requireContext(), "Μη έγκυρη ημερομηνία λήξης", Toast.LENGTH_SHORT).show()
            return false
        }
        try {
            val parts = expiry.split("/")
            if (DateUtils.isCardExpired(parts[0].toInt(), parts[1].toInt())) {
                Toast.makeText(requireContext(), "Η κάρτα έχει λήξει", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: Exception) { return false }
        
        if (cvv.length < 3) {
            Toast.makeText(requireContext(), "Μη έγκυρο CVV", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
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
