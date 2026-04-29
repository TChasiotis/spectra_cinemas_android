package com.example.spectra_cinemas_android.fragments

import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.databinding.SeatSelectionViewBinding
import com.example.spectra_cinemas_android.models.Movie
import java.util.Random

class SeatSelectionFragment : Fragment() {

    private var _binding: SeatSelectionViewBinding? = null
    private val binding get() = _binding!!
    
    private var movie: Movie? = null
    private var cinema: String = ""
    private var date: String = ""
    private var time: String = ""
    
    private var hallType: Int = 2 
    private var hallName: String = ""
    private val selectedSeats = mutableListOf<String>()
    private val MAX_SEATS = 5

    companion object {
        fun newInstance(movie: Movie, cinema: String, date: String, time: String): SeatSelectionFragment {
            val fragment = SeatSelectionFragment()
            val args = Bundle()
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
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        _binding = SeatSelectionViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Δραστική εξαφάνιση του Toolbar και της κόκκινης γραμμής
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        (activity as? MainActivity)?.setUIForBooking(true)

        val movieTitle = arguments?.getString("MOVIE_TITLE") ?: ""
        movie = com.example.spectra_cinemas_android.utils.MovieData.getMovies().find { it.title == movieTitle }
        cinema = arguments?.getString("CINEMA") ?: ""
        date = arguments?.getString("DATE") ?: ""
        time = arguments?.getString("TIME") ?: ""

        determineHallType()
        setupBookingInfo()
        createSeats()

        binding.btnToggleView.setOnClickListener {
            toggleView()
        }

        binding.continueBtn.setOnClickListener {
            handleContinue()
        }
    }

    private fun setupBookingInfo() {
        binding.movieTitleLabel.text = movie?.title
        binding.cinemaLabel.text = cinema
        binding.hallLabel.text = hallName
        binding.dateTimeLabel.text = getString(R.string.date_time_format, date, time)
    }

    private fun determineHallType() {
        val uniqueKey = (movie?.title ?: "") + cinema + date + time
        val hash = Math.abs(uniqueKey.hashCode())
        val titleUp = movie?.title?.uppercase() ?: ""
        val isBlockbuster = titleUp.contains("AVATAR") || titleUp.contains("DUNE") || titleUp.contains("BATMAN")
        
        val chance = hash % 100
        if (isBlockbuster) {
            if (chance < 60) setIMAX((hash % 2) + 1) else setStandard((hash % 4) + 3)
        } else {
            if (chance < 20) setIMAX((hash % 2) + 1) else setStandard((hash % 4) + 3)
        }
    }

    private fun setIMAX(number: Int) {
        hallType = 1
        hallName = "IMAX $number"
    }

    private fun setStandard(number: Int) {
        hallType = 2
        hallName = "Αίθουσα $number"
    }

    private fun createSeats() {
        binding.seatsGrid.removeAllViews()
        if (hallType == 1) createLayoutIMAX() else createLayoutStandard()
    }

    private fun createLayoutIMAX() {
        val rows = 10
        val cols = 23
        binding.seatsGrid.columnCount = cols + 2
        val uniqueKey = (movie?.title ?: "") + cinema + date + time
        val rand = Random(uniqueKey.hashCode().toLong())

        for (i in 0 until rows) {
            val rowChar = ('A'.code + i).toChar()
            addRowLabel(rowChar, i, 0)
            for (j in 1..cols) {
                createSingleSeat(rowChar, cols - j + 1, i, j, rand)
            }
            addRowLabel(rowChar, i, cols + 1)
        }
    }

    private fun createLayoutStandard() {
        val rows = 10
        val seatsPerSide = 10
        binding.seatsGrid.columnCount = (seatsPerSide * 2) + 2
        val uniqueKey = (movie?.title ?: "") + cinema + date + time
        val rand = Random(uniqueKey.hashCode().toLong())

        for (i in 0 until rows) {
            val rowChar = ('A'.code + i).toChar()
            addRowLabel(rowChar, i, 0)
            for (j in 0 until seatsPerSide) {
                createSingleSeat(rowChar, (seatsPerSide * 2) - j, i, j + 1, rand)
            }
            for (j in 0 until seatsPerSide) {
                createSingleSeat(rowChar, seatsPerSide - j, i, j + 11, rand)
            }
            addRowLabel(rowChar, i, 21)
        }
    }

    private fun createSingleSeat(row: Char, seatNum: Int, gridRow: Int, gridCol: Int, rand: Random) {
        val seatId = "$row$seatNum"
        val seatBtn = ToggleButton(requireContext())
        val sizeInDp = if (hallType == 1) 22 else 28 
        val size = (sizeInDp * resources.displayMetrics.density).toInt()

        val params = GridLayout.LayoutParams().apply {
            width = size
            height = size
            setMargins(1, 1, 1, 1)
            rowSpec = GridLayout.spec(gridRow)
            columnSpec = GridLayout.spec(gridCol)
        }
        
        seatBtn.layoutParams = params
        seatBtn.text = seatNum.toString()
        seatBtn.textOn = seatNum.toString()
        seatBtn.textOff = seatNum.toString()
        seatBtn.setPadding(0, 0, 0, 0)
        seatBtn.minHeight = 0
        seatBtn.minWidth = 0
        seatBtn.gravity = Gravity.CENTER
        seatBtn.textSize = if (hallType == 1) 6f else 8f
        seatBtn.setTextColor(Color.WHITE)
        seatBtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.f_btn_active_background)
        
        if (rand.nextDouble() < 0.20) {
            seatBtn.isEnabled = false
            seatBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E50914"))
        } else {
            seatBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4D000000"))
            seatBtn.setOnClickListener {
                handleSeatClick(seatBtn, seatId, seatBtn.isChecked)
            }
        }
        binding.seatsGrid.addView(seatBtn)
    }

    private fun addRowLabel(rowChar: Char, row: Int, col: Int) {
        val label = TextView(requireContext())
        label.text = rowChar.toString()
        label.setTextColor(Color.WHITE)
        label.gravity = Gravity.CENTER
        label.setTypeface(null, Typeface.BOLD)
        
        val sizeInDp = if (hallType == 1) 22 else 28
        val size = (sizeInDp * resources.displayMetrics.density).toInt()
        val params = GridLayout.LayoutParams().apply {
            width = size
            height = size
            rowSpec = GridLayout.spec(row)
            columnSpec = GridLayout.spec(col)
        }
        binding.seatsGrid.addView(label, params)
    }

    private fun handleSeatClick(button: ToggleButton, seatId: String, isSelected: Boolean) {
        if (isSelected) {
            if (selectedSeats.size >= MAX_SEATS) {
                button.isChecked = false
                return
            }
            selectedSeats.add(seatId)
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00FF00"))
            button.setTextColor(Color.BLACK)
            updatePOV(seatId)
        } else {
            selectedSeats.remove(seatId)
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4D000000"))
            button.setTextColor(Color.WHITE)
            if (selectedSeats.isNotEmpty()) updatePOV(selectedSeats.last())
            else binding.seatViewBackground.setImageResource(R.drawable.l_spectra_background)
        }
        binding.continueBtn.isEnabled = selectedSeats.isNotEmpty()
    }

    private fun updatePOV(seatId: String) {
        val row = seatId.substring(0, 1).lowercase()
        val num = seatId.substring(1)
        val type = if (hallType == 1) "i" else "s"
        val resName = "h_${type}_${row}${num}"
        val resId = resources.getIdentifier(resName, "drawable", requireContext().packageName)
        if (resId != 0) binding.seatViewBackground.setImageResource(resId)
    }

    private fun toggleView() {
        val isHidden = binding.centerContent.visibility == View.GONE
        if (isHidden) {
            binding.centerContent.visibility = View.VISIBLE
            binding.seatsOverlay.visibility = View.VISIBLE
            binding.bookingInfoLayout.visibility = View.GONE
            binding.btnToggleView.text = "ΘΕΑΣΗ ΑΙΘΟΥΣΑΣ"
        } else {
            binding.centerContent.visibility = View.GONE
            binding.seatsOverlay.visibility = View.GONE
            binding.bookingInfoLayout.visibility = View.VISIBLE
            binding.btnToggleView.text = "ΕΠΙΣΤΡΟΦΗ"
        }
    }

    private fun handleContinue() {
        val fragment = BookingCanteenFragment.newInstance(
            movie?.title ?: "",
            cinema,
            hallName,
            date,
            time,
            selectedSeats
        )
        (activity as? MainActivity)?.replaceFragment(fragment, "Κυλικείο")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        (activity as? MainActivity)?.setUIForBooking(false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        _binding = null
    }
}
