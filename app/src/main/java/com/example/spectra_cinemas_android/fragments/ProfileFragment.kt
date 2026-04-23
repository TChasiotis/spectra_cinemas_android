package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.databinding.ProfileViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
            deletePreferredCard()
        }

        binding.btnAddCardProfile.setOnClickListener {
            // Εδώ θα μπορούσε να ανοίξει ένα Dialog για προσθήκη κάρτας
            Toast.makeText(requireContext(), "Η λειτουργία προσθήκης κάρτας θα υλοποιηθεί σύντομα", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && isAdded) {
                    val name = document.getString("fullName") ?: "N/A"
                    val email = document.getString("email") ?: "N/A"
                    val cardInfo = document.get("cardInfo") as? Map<String, String>

                    binding.tvProfileName.text = "Ονοματεπώνυμο: $name"
                    binding.tvProfileEmail.text = "Email: $email"

                    if (cardInfo != null) {
                        val fullNumber = cardInfo["cardNumber"] ?: ""
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
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Σφάλμα φόρτωσης: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deletePreferredCard() {
        val userId = auth.currentUser?.uid ?: return
        
        val updates = hashMapOf<String, Any?>(
            "cardInfo" to null
        )

        db.collection("users").document(userId).update(updates)
            .addOnSuccessListener {
                binding.layoutCardInfo.visibility = View.GONE
                binding.btnAddCardProfile.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Η κάρτα διαγράφηκε επιτυχώς", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Σφάλμα διαγραφής: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
