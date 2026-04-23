package com.example.spectra_cinemas_android.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.databinding.ProfileViewBinding
import com.example.spectra_cinemas_android.databinding.DialogAddCardBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class ProfileFragment : Fragment() {

    private var _binding: ProfileViewBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileViewBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        db = Firebase.firestore
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()

        binding.btnLogoutProfile.setOnClickListener {
            auth.signOut()
            (activity as? MainActivity)?.let { mainActivity ->
                mainActivity.setLoggedIn(false)
                mainActivity.replaceFragment(MoviesFragment(), "Ταινίες")
            }
        }

        binding.btnDeleteCard.setOnClickListener {
            showDeleteCardConfirmation()
        }

        binding.btnAddCardProfile.setOnClickListener {
            showAddCardDialog()
        }

        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountConfirmation()
        }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && isAdded) {
                    val name = document.getString("fullName") ?: "N/A"
                    val email = document.getString("email") ?: "N/A"
                    val cardInfo = document.get("cardInfo") as? Map<*, *>

                    binding.tvProfileName.text = "Ονοματεπώνυμο: $name"
                    binding.tvProfileEmail.text = "Email: $email"

                    if (cardInfo != null) {
                        val fullNumber = cardInfo["cardNumber"] as? String ?: ""
                        val last4 = if (fullNumber.length >= 4) fullNumber.takeLast(4) else "****"
                        binding.tvCardNumber.text = "**** **** **** $last4"
                        binding.layoutCardInfo.visibility = View.VISIBLE
                        binding.btnAddCardProfile.visibility = View.GONE
                    } else {
                        binding.layoutCardInfo.visibility = View.GONE
                        binding.btnAddCardProfile.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun showAddCardDialog() {
        val dialogBinding = DialogAddCardBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
        builder.setView(dialogBinding.root)
        
        val dialog = builder.create()
        dialog.show()

        setupCardLogic(dialogBinding)

        dialogBinding.root.findViewById<View>(android.R.id.content)?.let { 
             // This is a bit tricky with custom layout in AlertDialog. 
             // Let's add buttons to the layout or use the builder's buttons.
        }
        
        // Let's re-use the builder to have standard buttons but with custom logic
        dialog.dismiss()
        
        val finalDialog = AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
            .setTitle("Προσθήκη Κάρτας")
            .setView(dialogBinding.root)
            .setPositiveButton("Αποθήκευση", null) // Set to null first to prevent auto-dismiss
            .setNegativeButton("Ακύρωση", null)
            .create()

        finalDialog.setOnShowListener {
            val saveBtn = finalDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveBtn.setOnClickListener {
                val cardNumber = dialogBinding.etDialogCardNumber.text.toString().replace(" ", "")
                val expiry = dialogBinding.etDialogExpiry.text.toString()
                val cvv = dialogBinding.etDialogCVV.text.toString()

                if (validateCard(cardNumber, expiry, cvv)) {
                    saveCard(cardNumber, expiry, cvv)
                    finalDialog.dismiss()
                }
            }
        }
        finalDialog.show()
        setupCardLogic(dialogBinding)
    }

    private fun setupCardLogic(dialogBinding: DialogAddCardBinding) {
        // Card Number Formatting & Detection
        dialogBinding.etDialogCardNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val original = s.toString().replace(" ", "")
                val formatted = StringBuilder()
                for (i in original.indices) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ")
                    formatted.append(original[i])
                }
                
                if (formatted.toString() != s.toString()) {
                    dialogBinding.etDialogCardNumber.removeTextChangedListener(this)
                    dialogBinding.etDialogCardNumber.setText(formatted.toString())
                    dialogBinding.etDialogCardNumber.setSelection(formatted.length)
                    dialogBinding.etDialogCardNumber.addTextChangedListener(this)
                }

                // Detect Card Type
                val logo = when {
                    original.startsWith("4") -> R.drawable.p_visa
                    original.matches(Regex("^(5[1-5]|2[2-7]).*")) -> R.drawable.p_mastercard
                    original.startsWith("34") || original.startsWith("37") -> R.drawable.p_american_express
                    else -> R.drawable.p_card_default
                }
                dialogBinding.etDialogCardNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, logo, 0)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Expiry Date Formatting (MM/YY)
        dialogBinding.etDialogExpiry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2 && !s.contains("/")) {
                    dialogBinding.etDialogExpiry.setText("$s/")
                    dialogBinding.etDialogExpiry.setSelection(3)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validateCard(number: String, expiry: String, cvv: String): Boolean {
        if (number.length < 15) {
            Toast.makeText(requireContext(), "Μη έγκυρος αριθμός κάρτας", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val isAmex = number.startsWith("34") || number.startsWith("37")
        if (isAmex && number.length != 15) {
            Toast.makeText(requireContext(), "Η American Express απαιτεί 15 ψηφία", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isAmex && number.length != 16) {
            Toast.makeText(requireContext(), "Ο αριθμός κάρτας πρέπει να έχει 16 ψηφία", Toast.LENGTH_SHORT).show()
            return false
        }

        if (expiry.length != 5) {
            Toast.makeText(requireContext(), "Μη έγκυρη ημερομηνία λήξης (MM/YY)", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val parts = expiry.split("/")
            val month = parts[0].toInt()
            val year = parts[1].toInt() + 2000
            
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1

            if (month < 1 || month > 12) {
                Toast.makeText(requireContext(), "Μήνας 01-12", Toast.LENGTH_SHORT).show()
                return false
            }

            if (year < currentYear || (year == currentYear && month < currentMonth)) {
                Toast.makeText(requireContext(), "Η κάρτα έχει λήξει", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: Exception) {
            return false
        }

        if (cvv.length < 3) {
            Toast.makeText(requireContext(), "Μη έγκυρο CVV", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveCard(number: String, expiry: String, cvv: String) {
        val userId = auth.currentUser?.uid ?: return
        val cardData = mapOf(
            "cardNumber" to number,
            "expiry" to expiry,
            "cvv" to cvv
        )

        db.collection("users").document(userId).update("cardInfo", cardData)
            .addOnSuccessListener {
                loadUserProfile()
                Toast.makeText(requireContext(), "Η κάρτα προστέθηκε επιτυχώς!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteCardConfirmation() {
        AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
            .setTitle("Διαγραφή Κάρτας")
            .setMessage("Είστε σίγουροι ότι θέλετε να διαγράψετε την προτιμώμενη κάρτα;")
            .setPositiveButton("Διαγραφή") { _, _ -> deletePreferredCard() }
            .setNegativeButton("Ακύρωση", null)
            .show()
    }

    private fun deletePreferredCard() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).update("cardInfo", null)
            .addOnSuccessListener {
                loadUserProfile()
                Toast.makeText(requireContext(), "Η κάρτα διαγράφηκε", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteAccountConfirmation() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_card, null) // Reusing a layout or creating new
        // For delete, we need a password field
        val passwordInput = com.google.android.material.textfield.TextInputEditText(requireContext())
        passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordInput.hint = "Κωδικός πρόσβασης"
        
        val container = android.widget.FrameLayout(requireContext())
        val params = android.widget.FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(60, 20, 60, 0)
        passwordInput.layoutParams = params
        container.addView(passwordInput)

        AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
            .setTitle("ΔΙΑΓΡΑΦΗ ΛΟΓΑΡΙΑΣΜΟΥ")
            .setMessage("Για την ασφάλειά σας, παρακαλώ εισάγετε τον κωδικό σας για να επιβεβαιώσετε τη διαγραφή.")
            .setView(container)
            .setPositiveButton("ΟΡΙΣΤΙΚΗ ΔΙΑΓΡΑΦΗ") { _, _ ->
                val password = passwordInput.text.toString()
                if (password.isNotEmpty()) {
                    reauthenticateAndDelete(password)
                } else {
                    Toast.makeText(requireContext(), "Απαιτείται κωδικός", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("ΑΚΥΡΩΣΗ", null)
            .show()
    }

    private fun reauthenticateAndDelete(password: String) {
        val user = auth.currentUser ?: return
        val credential = EmailAuthProvider.getCredential(user.email!!, password)

        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                deleteUserAccount()
            } else {
                Toast.makeText(requireContext(), "Λάθος κωδικός πρόσβασης", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUserAccount() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        db.collection("users").document(userId).delete()
            .addOnSuccessListener {
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Ο λογαριασμός διαγράφηκε οριστικά", Toast.LENGTH_LONG).show()
                        (activity as? MainActivity)?.let { mainActivity ->
                            mainActivity.setLoggedIn(false)
                            mainActivity.replaceFragment(MoviesFragment(), "Ταινίες")
                        }
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
