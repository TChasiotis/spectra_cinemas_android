package com.example.spectra_cinemas_android.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.databinding.MovieDetailsViewBinding
import com.example.spectra_cinemas_android.models.Movie
import com.example.spectra_cinemas_android.utils.CinemaData
import com.example.spectra_cinemas_android.utils.MovieData
import com.example.spectra_cinemas_android.utils.VideoPlayer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MovieDetailsFragment : Fragment() {

    private var _binding: MovieDetailsViewBinding? = null
    private val binding get() = _binding!!
    
    private var currentMovie: Movie? = null
    private var selectedTimeBtn: Button? = null
    private var selectedDate: String = ""
    private var selectedTime: String = ""

    companion object {
        private const val ARG_MOVIE_TITLE = "arg_movie_title"

        fun newInstance(movieTitle: String): MovieDetailsFragment {
            val args = Bundle()
            args.putString(ARG_MOVIE_TITLE, movieTitle)
            val fragment = MovieDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MovieDetailsViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieTitle = arguments?.getString(ARG_MOVIE_TITLE)
        currentMovie = MovieData.getMovies().find { it.title == movieTitle }

        setupPage()
    }

    private fun setupPage() {
        val movie = currentMovie ?: return

        binding.titleLabel.text = movie.title
        binding.subtitleLabel.text = movie.englishTitle
        binding.descriptionText.text = movie.description

        val tags = movie.tags.split("|")
        if (tags.isNotEmpty()) binding.ratingLabel.text = tags[0].trim()
        if (tags.size > 1) binding.genreLabel.text = tags[1].trim()
        if (tags.size > 2) binding.durationLabel.text = tags[2].trim()

        updateToggleStyles(true)
        showPoster()

        binding.btnPoster.setOnClickListener { 
            updateToggleStyles(true)
            showPoster() 
        }
        binding.btnTrailer.setOnClickListener { 
            updateToggleStyles(false)
            showTrailer() 
        }

        val cinemaNames = CinemaData.getCinemaNames()
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, cinemaNames) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as TextView).setTextColor(Color.BLACK)
                return v
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.cinemaSelector.adapter = adapter

        binding.cinemaSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                refreshShowtimes()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.continueBtn.setOnClickListener {
            handleContinue()
        }
    }

    private fun updateToggleStyles(isPoster: Boolean) {
        val activeColor = ContextCompat.getColor(requireContext(), R.color.red_primary)
        val inactiveColor = "#333333".toColorInt()

        binding.btnPoster.backgroundTintList = ColorStateList.valueOf(if (isPoster) activeColor else inactiveColor)
        binding.btnPoster.setTextColor(if (isPoster) Color.WHITE else Color.GRAY)
        
        binding.btnTrailer.backgroundTintList = ColorStateList.valueOf(if (isPoster) inactiveColor else activeColor)
        binding.btnTrailer.setTextColor(if (isPoster) Color.GRAY else Color.WHITE)
    }

    private fun refreshShowtimes() {
        binding.showtimesContainer.removeAllViews()
        selectedTimeBtn = null
        binding.continueBtn.visibility = View.GONE

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("EEEE d/M", Locale("el", "GR"))

        for (i in 0 until 5) {
            val dateLabelText = if (i == 0) "Σήμερα" else {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val formatted = sdf.format(calendar.time)
                formatted.replaceFirstChar { it.uppercase() }
            }
            addShowtimeRow(dateLabelText, i == 0)
        }
    }

    private fun addShowtimeRow(dateText: String, isToday: Boolean) {
        val dayContainer = LinearLayout(requireContext())
        dayContainer.orientation = LinearLayout.VERTICAL
        dayContainer.setPadding(20, 20, 20, 20)
        val containerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        containerParams.setMargins(0, 0, 0, 30)
        dayContainer.layoutParams = containerParams
        
        dayContainer.setBackgroundResource(R.drawable.f_btn_active_background)
        dayContainer.backgroundTintList = ColorStateList.valueOf("#F21A1A1A".toColorInt())

        val dateLabel = TextView(requireContext())
        dateLabel.text = dateText
        dateLabel.setTextColor(Color.WHITE)
        dateLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        dateLabel.setPadding(30, 10, 30, 10)
        dateLabel.background = ContextCompat.getDrawable(requireContext(), R.drawable.f_btn_active_background)
        dateLabel.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_primary))
        
        val dateParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dateParams.setMargins(0, 0, 0, 20)
        dateLabel.layoutParams = dateParams
        dayContainer.addView(dateLabel)

        val gridLayout = GridLayout(requireContext())
        gridLayout.columnCount = 4
        
        val selectedItem = binding.cinemaSelector.selectedItem
        val cinemaName = selectedItem?.toString() ?: ""
        val variant = Math.abs(currentMovie?.title?.hashCode() ?: 0) % 2
        val times = when {
            cinemaName.contains("Συγγρού") -> if (variant == 0) arrayOf("17:30", "20:00", "22:30", "00:45") else arrayOf("18:15", "20:45", "23:15", "01:00")
            cinemaName.contains("Μαρούσι") -> if (variant == 0) arrayOf("16:45", "19:15", "21:45", "00:15") else arrayOf("17:15", "19:45", "22:15", "00:45")
            cinemaName.contains("Παραλία") -> if (variant == 0) arrayOf("18:15", "21:00", "22:45", "00:00") else arrayOf("19:00", "21:30", "23:30", "00:30")
            cinemaName.contains("Retail Park") -> if (variant == 0) arrayOf("17:15", "19:45", "22:15", "00:45") else arrayOf("18:00", "20:30", "23:00", "01:15")
            cinemaName.contains("Πάτρα") -> if (variant == 0) arrayOf("19:00", "21:30", "23:50") else arrayOf("18:30", "21:00", "23:30")
            cinemaName.contains("Ηράκλειο") -> if (variant == 0) arrayOf("18:30", "21:15", "23:30") else arrayOf("19:15", "21:45", "00:00")
            cinemaName.contains("Λάρισσα") -> if (variant == 0) arrayOf("18:00", "20:30", "23:00") else arrayOf("18:45", "21:15", "23:45")
            cinemaName.contains("Ιωάννινα") -> if (variant == 0) arrayOf("17:45", "20:15", "22:45") else arrayOf("18:30", "21:00", "23:15")
            else -> arrayOf("19:00", "21:00", "23:00")
        }

        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)

        for (time in times) {
            val timeBtn = Button(requireContext())
            timeBtn.text = time
            timeBtn.setTextColor(Color.WHITE)
            timeBtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.f_btn_active_background)
            timeBtn.backgroundTintList = ColorStateList.valueOf("#444444".toColorInt())
            
            val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
            )
            params.width = 0
            params.setMargins(8, 8, 8, 8)
            timeBtn.layoutParams = params

            var isDisabled = false
            if (isToday) {
                if (!time.startsWith("00:") && !time.startsWith("01:")) {
                    try {
                        val parts = time.split(":")
                        val showHour = parts[0].toInt()
                        val showMinute = parts[1].toInt()
                        if (currentHour > showHour || (currentHour == showHour && currentMinute > showMinute)) {
                            isDisabled = true
                        }
                    } catch (e: Exception) {}
                }
            }

            if (isDisabled) {
                timeBtn.isEnabled = false
                timeBtn.alpha = 0.2f
            } else {
                timeBtn.setOnClickListener {
                    selectedTimeBtn?.backgroundTintList = ColorStateList.valueOf("#444444".toColorInt())
                    selectedTimeBtn?.setTextColor(Color.WHITE)
                    
                    selectedTimeBtn = timeBtn
                    selectedDate = dateText
                    selectedTime = time
                    
                    timeBtn.backgroundTintList = ColorStateList.valueOf("#00FF00".toColorInt())
                    timeBtn.setTextColor(Color.BLACK)
                    binding.continueBtn.visibility = View.VISIBLE
                }
            }
            gridLayout.addView(timeBtn)
        }
        dayContainer.addView(gridLayout)
        binding.showtimesContainer.addView(dayContainer)
    }

    private fun showPoster() {
        VideoPlayer.stop()
        val movie = currentMovie ?: return
        val imageView = ImageView(requireContext())
        imageView.setImageResource(movie.imageResId)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        binding.mediaContainer.removeAllViews()
        binding.mediaContainer.addView(imageView)
    }

    private fun showTrailer() {
        val movie = currentMovie ?: return
        VideoPlayer.attachPlayer(requireContext(), binding.mediaContainer, movie.trailerResId, layoutInflater)
    }

    private fun handleContinue() {
        val movie = currentMovie ?: return
        val selectedCinema = binding.cinemaSelector.selectedItem?.toString() ?: ""
        
        val mainActivity = (activity as? MainActivity)
        mainActivity?.pendingMovie = movie
        mainActivity?.pendingCinema = selectedCinema
        mainActivity?.pendingDate = selectedDate
        mainActivity?.pendingTime = selectedTime

        if (Firebase.auth.currentUser != null && Firebase.auth.currentUser?.isEmailVerified == true) {
            mainActivity?.replaceFragment(
                SeatSelectionFragment.newInstance(movie, selectedCinema, selectedDate, selectedTime),
                "Επιλογή Θέσεων"
            )
        } else {
            mainActivity?.replaceFragment(
                BookingAuthFragment.newInstance(movie, selectedCinema, selectedDate, selectedTime),
                "Συνέχεια Κράτησης"
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        VideoPlayer.stop()
        _binding = null
    }
}
