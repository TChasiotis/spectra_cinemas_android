package com.example.spectra_cinemas_android.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.adapters.BookingCanteenAdapter
import com.example.spectra_cinemas_android.databinding.BookingCanteenViewBinding
import com.example.spectra_cinemas_android.models.Movie
import com.example.spectra_cinemas_android.models.Snack
import com.example.spectra_cinemas_android.utils.CanteenData
import com.example.spectra_cinemas_android.utils.MovieData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class BookingCanteenFragment : Fragment() {

    private var _binding: BookingCanteenViewBinding? = null
    private val binding get() = _binding!!

    private var movie: Movie? = null
    private var cinemaName: String = ""
    private var hallName: String = ""
    private var date: String = ""
    private var time: String = ""
    private var selectedSeats = listOf<String>()

    private var allProducts: List<Snack> = emptyList()
    private val cart = mutableMapOf<Snack, Int>()
    private lateinit var adapter: BookingCanteenAdapter
    private var currentType = "SNACK"

    companion object {
        fun newInstance(movieTitle: String, cinema: String, hall: String, date: String, time: String, seats: List<String>): BookingCanteenFragment {
            val fragment = BookingCanteenFragment()
            val args = Bundle()
            args.putString("MOVIE_TITLE", movieTitle)
            args.putString("CINEMA", cinema)
            args.putString("HALL", hall)
            args.putString("DATE", date)
            args.putString("TIME", time)
            args.putStringArrayList("SEATS", ArrayList(seats))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BookingCanteenViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setUIForBooking(true)

        val movieTitle = arguments?.getString("MOVIE_TITLE") ?: ""
        movie = MovieData.getMovies().find { it.title == movieTitle }
        cinemaName = arguments?.getString("CINEMA") ?: ""
        hallName = arguments?.getString("HALL") ?: ""
        date = arguments?.getString("DATE") ?: ""
        time = arguments?.getString("TIME") ?: ""
        selectedSeats = arguments?.getStringArrayList("SEATS") ?: listOf()

        // Ανάκτηση καλαθιού και φίλτρου μετά από περιστροφή
        savedInstanceState?.let { bundle ->
            currentType = bundle.getString("CURRENT_TYPE", "SNACK")
            val snackNames = bundle.getStringArrayList("CART_NAMES") ?: arrayListOf()
            val snackQtys = bundle.getIntegerArrayList("CART_QTYS") ?: arrayListOf()
            for (i in snackNames.indices) {
                val snack = CanteenData.getAllSnacks().find { it.name == snackNames[i] }
                if (snack != null) {
                    cart[snack] = snackQtys[i]
                }
            }
        }

        setupUI()
        // Χρήση τοπικών δεδομένων για ταχύτητα στην παρουσίαση
        allProducts = CanteenData.getAllSnacks()
        setupRecyclerView()
        updateFilter()
        setupBottomSheet()
        updateSummary()
    }

    private fun loadProducts() {
        // Καταργήθηκε για την παρουσίαση - χρησιμοποιούμε απευθείας τα τοπικά
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("CURRENT_TYPE", currentType)
        val snackNames = ArrayList<String>()
        val snackQtys = ArrayList<Int>()
        for ((snack, qty) in cart) {
            snackNames.add(snack.name)
            snackQtys.add(qty)
        }
        outState.putStringArrayList("CART_NAMES", snackNames)
        outState.putIntegerArrayList("CART_QTYS", snackQtys)
    }

    private fun setupUI() {
        binding.movieTitleLabel.text = movie?.title
        binding.lblDate.text = getString(R.string.date_time_format, date, time)
        binding.lblCinema.text = hallName

        binding.btnSnacks.setOnClickListener {
            currentType = "SNACK"
            updateFilter()
        }
        binding.btnDrinks.setOnClickListener {
            currentType = "DRINK"
            updateFilter()
        }

        binding.continueBtn.setOnClickListener {
            handleContinue()
        }
    }

    private fun updateFilter() {
        val activeColor = ContextCompat.getColor(requireContext(), R.color.red_primary)
        if (currentType == "SNACK") {
            binding.btnSnacks.setBackgroundResource(R.drawable.f_btn_active_background)
            binding.btnSnacks.backgroundTintList = ColorStateList.valueOf(activeColor)
            binding.btnSnacks.setTextColor(Color.WHITE)
            
            binding.btnDrinks.background = null
            binding.btnDrinks.backgroundTintList = null
            binding.btnDrinks.setTextColor(Color.WHITE)
        } else {
            binding.btnDrinks.setBackgroundResource(R.drawable.f_btn_active_background)
            binding.btnDrinks.backgroundTintList = ColorStateList.valueOf(activeColor)
            binding.btnDrinks.setTextColor(Color.WHITE)
            
            binding.btnSnacks.background = null
            binding.btnSnacks.backgroundTintList = null
            binding.btnSnacks.setTextColor(Color.WHITE)
        }
        adapter.updateData(allProducts.filter { it.type == currentType })
    }

    private fun setupRecyclerView() {
        adapter = BookingCanteenAdapter(allProducts.filter { it.type == currentType }, cart) {
            updateSummary()
        }
        binding.itemsContainer.layoutManager = LinearLayoutManager(requireContext())
        binding.itemsContainer.adapter = adapter
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding.bottomSheetSummary ?: return
        val behavior = BottomSheetBehavior.from(bottomSheet)
        binding.summaryHeader?.setOnClickListener {
            if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.arrowToggle?.rotation = 180f
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.arrowToggle?.rotation = 0f
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun updateSummary() {
        binding.summaryContainer.removeAllViews()
        var total = 0.0
        val ticketPrice = 7.0

        // Seats
        for (seat in selectedSeats) {
            addSummaryRow("ΘΕΣΗ $seat", ticketPrice)
            total += ticketPrice
        }

        // Snacks
        for ((snack, qty) in cart) {
            if (qty > 0) {
                addSummaryRow("$qty X ${snack.name}", snack.price * qty)
                total += (snack.price * qty)
            }
        }

        binding.totalLabel.text = String.format(Locale.getDefault(), "%.2f€", total)
    }

    private fun addSummaryRow(text: String, price: Double) {
        val row = LinearLayout(requireContext())
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 4, 0, 4)
        }

        val txtLabel = TextView(requireContext())
        txtLabel.text = text
        txtLabel.setTextColor(Color.WHITE)
        txtLabel.textSize = 14f
        val paramsLabel = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        txtLabel.layoutParams = paramsLabel

        val priceLabel = TextView(requireContext())
        priceLabel.text = String.format(Locale.getDefault(), "%.2f€", price)
        priceLabel.setTextColor(Color.WHITE)
        priceLabel.textSize = 14f

        row.addView(txtLabel)
        row.addView(priceLabel)
        binding.summaryContainer.addView(row)
    }

    private fun handleContinue() {
        val seatsStr = selectedSeats
        var total = 0.0
        val ticketPrice = 7.0
        for (seat in selectedSeats) total += ticketPrice
        
        val snacksSb = StringBuilder()
        for ((snack, qty) in cart) {
            if (qty > 0) {
                total += (snack.price * qty)
                snacksSb.append("$qty X ${snack.name}, ")
            }
        }
        val finalSnacks = if (snacksSb.isEmpty()) "-" else snacksSb.toString().removeSuffix(", ")
        val finalPrice = String.format(Locale.getDefault(), "%.2f€", total)

        val fragment = BookingPaymentFragment.newInstance(
            movie?.title ?: "",
            cinemaName,
            hallName,
            date,
            time,
            selectedSeats,
            finalPrice,
            finalSnacks
        )
        (activity as? MainActivity)?.replaceFragment(fragment, "Πληρωμή")
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setUIForBooking(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
