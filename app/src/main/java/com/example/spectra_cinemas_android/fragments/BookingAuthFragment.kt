package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.databinding.BookingAuthViewBinding
import com.example.spectra_cinemas_android.models.Movie

class BookingAuthFragment : Fragment() {

    private var _binding: BookingAuthViewBinding? = null
    private val binding get() = _binding!!
    
    private var movie: Movie? = null
    private var cinema: String = ""
    private var date: String = ""
    private var time: String = ""

    companion object {
        fun newInstance(movie: Movie, cinema: String, date: String, time: String): BookingAuthFragment {
            val fragment = BookingAuthFragment()
            val args = Bundle()
            // Θα χρησιμοποιήσουμε προσωρινά τον τίτλο για να βρούμε την ταινία μετά
            // ή θα την κάνουμε Parcelable. Για τώρα, ας την βρούμε από το MovieData.
            args.putString("MOVIE_TITLE", movie.title)
            args.putString("CINEMA", cinema)
            args.putString("DATE", date)
            args.putString("TIME", time)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BookingAuthViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieTitle = arguments?.getString("MOVIE_TITLE") ?: ""
        movie = com.example.spectra_cinemas_android.utils.MovieData.getMovies().find { it.title == movieTitle }
        cinema = arguments?.getString("CINEMA") ?: ""
        date = arguments?.getString("DATE") ?: ""
        time = arguments?.getString("TIME") ?: ""

        binding.btnBookingLogin.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(LoginFragment(), "Σύνδεση")
        }

        binding.btnBookingRegister.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(RegisterFragment(), "Εγγραφή")
        }

        binding.btnBookingGuest.setOnClickListener {
            showGuestDetailsDialog()
        }
    }

    private fun showGuestDetailsDialog() {
        val context = requireContext()
        val layout = android.widget.LinearLayout(context)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(60, 40, 60, 0)

        val etName = android.widget.EditText(context)
        etName.hint = "Ονοματεπώνυμο"
        etName.setTextColor(android.graphics.Color.WHITE)
        etName.setHintTextColor(android.graphics.Color.GRAY)

        val etEmail = android.widget.EditText(context)
        etEmail.hint = "Email (για το εισιτήριο)"
        etEmail.setTextColor(android.graphics.Color.WHITE)
        etEmail.setHintTextColor(android.graphics.Color.GRAY)
        etEmail.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        layout.addView(etName)
        layout.addView(etEmail)

        androidx.appcompat.app.AlertDialog.Builder(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
            .setTitle("Στοιχεία Επισκέπτη")
            .setMessage("Παρακαλώ δώστε τα στοιχεία σας για να σας σταλεί το εισιτήριο.")
            .setView(layout)
            .setPositiveButton("Συνέχεια") { _, _ ->
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                
                if (name.isNotEmpty() && email.contains("@")) {
                    movie?.let { m ->
                        val fragment = SeatSelectionFragment.newInstance(m, cinema, date, time)
                        // Μεταφορά των στοιχείων στο επόμενο fragment αν χρειαστεί
                        (activity as? MainActivity)?.replaceFragment(fragment, "Επιλογή Θέσεων")
                    }
                } else {
                    android.widget.Toast.makeText(context, "Παρακαλώ δώστε έγκυρα στοιχεία", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Ακύρωση", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
