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
            movie?.let { m ->
                (activity as? MainActivity)?.replaceFragment(
                    SeatSelectionFragment.newInstance(m, cinema, date, time),
                    "Επιλογή Θέσεων"
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
