package com.example.spectra_cinemas_android.fragments

import android.content.res.ColorStateList
import android.graphics.Color
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
import com.example.spectra_cinemas_android.utils.DateUtils
import com.example.spectra_cinemas_android.utils.NotificationHelper
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

        binding.btnEditName.setOnClickListener {
            showEditNameDialog()
        }
    }

    private fun showEditNameDialog() {
        val currentName = binding.tvProfileName.text.toString().substringAfter(": ").trim()
        val input = com.google.android.material.textfield.TextInputEditText(requireContext())
        input.setText(currentName)
        input.setSelection(currentName.length)
        
        val container = android.widget.FrameLayout(requireContext())
        val params = android.widget.FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(60, 20, 60, 0)
        input.layoutParams = params
        container.addView(input)

        AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
            .setTitle("Επεξεργασία Ονόματος")
            .setView(container)
            .setPositiveButton("ΑΠΟΘΗΚΕΥΣΗ") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty() && newName != currentName) {
                    updateUserNameInFirestore(newName)
                }
            }
            .setNegativeButton("ΑΚΥΡΩΣΗ", null)
            .show()
    }

    private fun updateUserNameInFirestore(newName: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).update("fullName", newName)
            .addOnSuccessListener {
                loadUserProfile()
                Toast.makeText(requireContext(), "Το όνομα ενημερώθηκε", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Σφάλμα κατά την ενημέρωση", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && isAdded) {
                    val name = document.getString("fullName") ?: "N/A"
                    val email = document.getString("email") ?: "N/A"
                    val birthDate = document.getString("birthDate") ?: "N/A"
                    val age = document.getLong("age") ?: 0
                    val cardInfo = document.get("cardInfo") as? Map<*, *>

                    binding.tvProfileName.text = "Ονοματεπώνυμο: $name"
                    binding.tvProfileEmail.text = "Email: $email"
                    binding.tvProfileBirthDate.text = "Ημ. Γέννησης: $birthDate"

                    if (cardInfo != null) {
                        val fullNumber = cardInfo["cardNumber"] as? String ?: ""
                        val isAmex = fullNumber.startsWith("34") || fullNumber.startsWith("37")
                        
                        val first3 = if (fullNumber.length >= 3) fullNumber.take(3) else ""
                        
                        if (isAmex) {
                            val last3 = if (fullNumber.length >= 3) fullNumber.takeLast(3) else "***"
                            binding.tvCardNumber.text = "$first3* **** **** $last3"
                        } else {
                            val last4 = if (fullNumber.length >= 4) fullNumber.takeLast(4) else "****"
                            binding.tvCardNumber.text = "$first3* **** **** $last4"
                        }
                        
                        val logo = when {
                            fullNumber.startsWith("4") -> R.drawable.p_visa
                            fullNumber.matches(Regex("^(5[1-5]|2[2-7]).*")) -> R.drawable.p_mastercard
                            isAmex -> R.drawable.p_american_express
                            else -> R.drawable.p_card_default
                        }
                        binding.ivCardIcon.setImageResource(logo)
                        binding.ivCardIcon.imageTintList = null

                        binding.layoutCardInfo.visibility = View.VISIBLE
                        binding.btnAddCardProfile.visibility = View.GONE
                        binding.cvCardSection.visibility = View.VISIBLE
                    } else {
                        binding.layoutCardInfo.visibility = View.GONE
                        if (age >= 18) {
                            binding.btnAddCardProfile.visibility = View.VISIBLE
                            binding.cvCardSection.visibility = View.VISIBLE
                        } else {
                            binding.btnAddCardProfile.visibility = View.GONE
                            binding.cvCardSection.visibility = View.GONE
                        }
                    }
                }
            }
    }

    private fun showAddCardDialog() {
        val dialogBinding = DialogAddCardBinding.inflate(layoutInflater)
        
        val finalDialog = AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
            .setTitle("Προσθήκη Κάρτας")
            .setView(dialogBinding.root)
            .setPositiveButton("Αποθήκευση", null)
            .setNegativeButton("Ακύρωση", null)
            .create()

        finalDialog.setOnShowListener {
            val saveBtn = finalDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveBtn.setOnClickListener {
                val cardHolder = dialogBinding.etDialogCardHolder.text.toString()
                val cardNumber = dialogBinding.etDialogCardNumber.text.toString().replace(" ", "")
                val expiry = dialogBinding.etDialogExpiry.text.toString()
                val cvv = dialogBinding.etDialogCVV.text.toString()

                if (validateCard(cardHolder, cardNumber, expiry, cvv)) {
                    saveCard(cardHolder, cardNumber, expiry, cvv)
                    finalDialog.dismiss()
                }
            }
        }
        
        setupCardLogic(dialogBinding)
        finalDialog.show()
    }

    private fun setupCardLogic(dialogBinding: DialogAddCardBinding) {
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

                val logo = when {
                    original.startsWith("4") -> R.drawable.p_visa
                    original.matches(Regex("^(5[1-5]|2[2-7]).*")) -> R.drawable.p_mastercard
                    original.startsWith("34") || original.startsWith("37") -> R.drawable.p_american_express
                    else -> R.drawable.p_card_default
                }
                // Χρήση του ξεχωριστού ImageView αντί για Compound Drawable
                dialogBinding.ivCardLogo.setImageResource(logo)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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

    private fun validateCard(holder: String, number: String, expiry: String, cvv: String): Boolean {
        if (holder.isEmpty()) {
            Toast.makeText(requireContext(), "Παρακαλώ εισάγετε το όνομα κατόχου", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val logo = when {
            number.startsWith("4") -> R.drawable.p_visa
            number.matches(Regex("^(5[1-5]|2[2-7]).*")) -> R.drawable.p_mastercard
            number.startsWith("34") || number.startsWith("37") -> R.drawable.p_american_express
            else -> null
        }

        if (logo == null) {
            Toast.makeText(requireContext(), "Δεκτές μόνο κάρτες Visa, Mastercard ή American Express", Toast.LENGTH_LONG).show()
            return false
        }

        if (number.length < 15) {
            Toast.makeText(requireContext(), "Μη έγκυρος αριθμός κάρτας", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val isAmex = number.startsWith("34") || number.startsWith("37")
        if (isAmex && number.length != 15) {
            Toast.makeText(requireContext(), "Οι κάρτες American Express πρέπει να έχουν 15 ψηφία", Toast.LENGTH_LONG).show()
            return false
        }
        if (!isAmex && number.length != 16) {
            Toast.makeText(requireContext(), "Ο αριθμός της κάρτας πρέπει να έχει 16 ψηφία", Toast.LENGTH_LONG).show()
            return false
        }

        if (expiry.length != 5) {
            Toast.makeText(requireContext(), "Μη έγκυρη ημερομηνία λήξης (MM/YY)", Toast.LENGTH_SHORT).show()
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

    private fun saveCard(holder: String, number: String, expiry: String, cvv: String) {
        val userId = auth.currentUser?.uid ?: return
        val cardData = mapOf(
            "cardHolder" to holder,
            "cardNumber" to number,
            "expiry" to expiry,
            "cvv" to cvv
        )

        db.collection("users").document(userId).update("cardInfo", cardData)
            .addOnSuccessListener {
                loadUserProfile()
                NotificationHelper.sendNotification(requireContext(), "Κάρτα Προστέθηκε", "Η προτιμώμενη κάρτα σας αποθηκεύτηκε με επιτυχία.")
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
                NotificationHelper.sendNotification(requireContext(), "Κάρτα Αφαιρέθηκε", "Η προτιμώμενη κάρτα σας αφαιρέθηκε από το λογαριασμό.")
                Toast.makeText(requireContext(), "Η κάρτα διαγράφηκε", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteAccountConfirmation() {
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
                        NotificationHelper.sendNotification(requireContext(), "Λογαριασμός Διαγράφηκε", "Ο λογαριασμός σας και όλα τα δεδομένα διαγράφηκαν οριστικά.")
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
