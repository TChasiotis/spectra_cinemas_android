package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.spectra_cinemas_android.database.AppDatabase
import com.example.spectra_cinemas_android.databinding.AdminDatabaseViewBinding
import com.example.spectra_cinemas_android.models.Cinema
import com.example.spectra_cinemas_android.models.Snack
import com.example.spectra_cinemas_android.models.Hall
import com.example.spectra_cinemas_android.models.Office
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class AdminDatabaseFragment : Fragment() {

    private var _binding: AdminDatabaseViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AdminDatabaseViewBinding.inflate(inflater, container, false)
        db = AppDatabase.getDatabase(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tables = arrayOf("Cinemas", "Snacks", "Halls", "Offices")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tables)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tableSpinner.adapter = adapter

        binding.tableSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateHints(tables[position])
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        binding.btnInsert.setOnClickListener { handleInsert() }
        binding.btnUpdate.setOnClickListener { handleUpdate() }
        binding.btnDelete.setOnClickListener { handleDelete() }

        // Room Queries
        binding.btnQueryCheap.setOnClickListener { runCheapSnacksQuery() }
        binding.btnQueryCity.setOnClickListener { runCinemaQuery() }
        binding.btnQuerySearchOffice.setOnClickListener { runSearchOfficeQuery() }

        // Firestore Queries
        binding.btnFsQueryPaid.setOnClickListener { runFsPaidQuery() }
        binding.btnFsQueryPrice.setOnClickListener { runFsPriceQuery() }
        binding.btnFsQueryPending.setOnClickListener { runFsPendingQuery() }
    }

    private fun handleInsert() {
        val id = binding.inputId.text.toString().trim()
        val data = binding.inputData.text.toString().trim()
        val parts = data.split(",").map { it.trim() }

        if (id.isEmpty()) {
            showToast("Παρακαλώ εισάγετε ID/Όνομα")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                when (binding.tableSpinner.selectedItem?.toString()) {
                    "Cinemas" -> {
                        val city = parts.getOrNull(0) ?: ""
                        val address = parts.getOrNull(1) ?: ""
                        val phone = parts.getOrNull(2) ?: ""
                        db.appDao().insertCinema(Cinema(name = id, city = city, address = address, phone = phone))
                        showToast("Cinema Inserted")
                    }
                    "Snacks" -> {
                        val price = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                        val type = parts.getOrNull(1)?.uppercase() ?: "SNACK"
                        db.appDao().insertSnack(Snack(name = id, price = price, type = type))
                        showToast("Snack Inserted ($type)")
                    }
                    "Halls" -> {
                        val cName = parts.getOrNull(0) ?: ""
                        val seats = parts.getOrNull(1) ?: "200"
                        val rows = parts.getOrNull(2) ?: "10"
                        val exits = parts.getOrNull(3) ?: "2"
                        val tech = parts.getOrNull(4) ?: "Standard"
                        db.appDao().insertHall(Hall(title = id, cinemaName = cName, imageResId = 0, seats = seats, rows = rows, exits = exits, tech = tech))
                        showToast("Hall Inserted")
                    }
                    "Offices" -> {
                        val sub = parts.getOrNull(0) ?: ""
                        val addr = parts.getOrNull(1) ?: ""
                        val ph = parts.getOrNull(2) ?: ""
                        val mail = parts.getOrNull(3) ?: ""
                        db.appDao().insertOffice(Office(title = id, subtitle = sub, imageResId = 0, address = addr, phone = ph, email = mail))
                        showToast("Office Inserted")
                    }
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun handleUpdate() {
        val id = binding.inputId.text.toString().trim()
        val data = binding.inputData.text.toString().trim()
        val parts = data.split(",").map { it.trim() }

        if (id.isEmpty()) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                when (binding.tableSpinner.selectedItem?.toString()) {
                    "Cinemas" -> {
                        val city = parts.getOrNull(0) ?: ""
                        val address = parts.getOrNull(1) ?: ""
                        val phone = parts.getOrNull(2) ?: ""
                        db.appDao().updateCinema(Cinema(name = id, city = city, address = address, phone = phone))
                        showToast("Cinema Updated")
                    }
                    "Snacks" -> {
                        val price = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                        val type = parts.getOrNull(1)?.uppercase() ?: "SNACK"
                        db.appDao().insertSnack(Snack(name = id, price = price, type = type))
                        showToast("Snack Updated")
                    }
                    "Halls" -> {
                        val cName = parts.getOrNull(0) ?: ""
                        val seats = parts.getOrNull(1) ?: "200"
                        val rows = parts.getOrNull(2) ?: "10"
                        val exits = parts.getOrNull(3) ?: "2"
                        val tech = parts.getOrNull(4) ?: "Standard"
                        db.appDao().updateHall(Hall(title = id, cinemaName = cName, imageResId = 0, seats = seats, rows = rows, exits = exits, tech = tech))
                        showToast("Hall Updated")
                    }
                    "Offices" -> {
                        val sub = parts.getOrNull(0) ?: ""
                        val addr = parts.getOrNull(1) ?: ""
                        val ph = parts.getOrNull(2) ?: ""
                        val mail = parts.getOrNull(3) ?: ""
                        db.appDao().updateOffice(Office(title = id, subtitle = sub, imageResId = 0, address = addr, phone = ph, email = mail))
                        showToast("Office Updated")
                    }
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun handleDelete() {
        val id = binding.inputId.text.toString().trim()
        if (id.isEmpty()) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val table = binding.tableSpinner.selectedItem?.toString()
                val deletedRows = when (table) {
                    "Cinemas" -> db.appDao().deleteCinemaByName(id)
                    "Snacks" -> db.appDao().deleteSnackByName(id)
                    "Halls" -> db.appDao().deleteHallByTitle(id)
                    "Offices" -> db.appDao().deleteOfficeByTitle(id)
                    else -> 0
                }
                
                if (deletedRows > 0) {
                    showToast("$table Deleted ($id)")
                } else {
                    showToast("Δεν βρέθηκε εγγραφή με αυτό το ID")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun runCheapSnacksQuery() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val results = db.appDao().getCheapSnacks(5.0)
                binding.txtResults.text = "Cheap Snacks (<5€):\n" + results.joinToString("\n") { "${it.name}: ${it.price}€" }
            } catch (e: Exception) {
                binding.txtResults.text = "Error: ${e.message}"
            }
        }
    }

    private fun runCinemaQuery() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val results = db.appDao().getCinemasByCity("Αθήνα")
                binding.txtResults.text = "Cinemas in Αθήνα:\n" + results.joinToString("\n") { it.name }
            } catch (e: Exception) {
                binding.txtResults.text = "Error: ${e.message}"
            }
        }
    }

    private fun runSearchOfficeQuery() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val results = db.appDao().searchOffices("Κεντρικά")
                binding.txtResults.text = "Offices like 'Κεντρικά':\n" + results.joinToString("\n") { it.title }
            } catch (e: Exception) {
                binding.txtResults.text = "Error: ${e.message}"
            }
        }
    }

    private fun runFsPaidQuery() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(uid).collection("bookings")
            .whereEqualTo("paymentStatus", "ΕΞΟΦΛΗΘΗΚΕ (Κάρτα)")
            .get()
            .addOnSuccessListener { docs ->
                binding.txtResults.text = "Paid Bookings (Firestore):\n" + docs.joinToString("\n") { it.getString("movieTitle") ?: "" }
            }
    }

    private fun runFsPriceQuery() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(uid).collection("bookings")
            .whereGreaterThan("price", "15.00€")
            .get()
            .addOnSuccessListener { docs ->
                binding.txtResults.text = "Expensive Bookings (>15€):\n" + docs.joinToString("\n") { it.getString("movieTitle") ?: "" }
            }
    }

    private fun runFsPendingQuery() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(uid).collection("bookings")
            .whereNotEqualTo("paymentStatus", "ΕΞΟΦΛΗΘΗΚΕ (Κάρτα)")
            .get()
            .addOnSuccessListener { docs ->
                binding.txtResults.text = "Pending Bookings (Firestore):\n" + docs.joinToString("\n") { it.getString("movieTitle") ?: "" }
            }
    }

    private fun updateHints(table: String) {
        val format: String
        when (table) {
            "Cinemas" -> {
                binding.inputId.hint = "Όνομα Σινεμά"
                format = "Πόλη, Διεύθυνση, Τηλέφωνο"
            }
            "Snacks" -> {
                binding.inputId.hint = "Όνομα Σνακ"
                format = "Τιμή, Τύπος (SNACK/DRINK)"
            }
            "Halls" -> {
                binding.inputId.hint = "Τίτλος Αίθουσας"
                format = "Όνομα Σινεμά, Θέσεις, Σειρές, Έξοδοι, Τεχνολογία"
            }
            "Offices" -> {
                binding.inputId.hint = "Τίτλος Γραφείου"
                format = "Υπότιτλος, Διεύθυνση, Τηλέφωνο, Email"
            }
            else -> format = ""
        }
        binding.txtFormatHelp.text = "Μορφή: $format"
        binding.inputData.hint = format
    }

    private fun showToast(msg: String) {
        if (!isAdded) return
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
