package com.example.spectra_cinemas_android.fragments

import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.databinding.BookingPaymentViewBinding
import com.example.spectra_cinemas_android.utils.DateUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class BookingPaymentFragment : Fragment() {

    private var _binding: BookingPaymentViewBinding? = null
    private val binding get() = _binding!!

    private var movieTitle: String = ""
    private var cinemaName: String = ""
    private var hallName: String = ""
    private var date: String = ""
    private var time: String = ""
    private var selectedSeats = listOf<String>()
    private var totalPrice: String = ""
    private var snacksInfo: String = ""

    private var isCardPayment = true
    private var userAge: Int = 0
    private var preferredCard: Map<String, Any>? = null

    companion object {
        fun newInstance(movieTitle: String, cinema: String, hall: String, date: String, time: String, seats: List<String>, total: String, snacks: String): BookingPaymentFragment {
            val fragment = BookingPaymentFragment()
            val args = Bundle()
            args.putString("MOVIE_TITLE", movieTitle)
            args.putString("CINEMA", cinema)
            args.putString("HALL", hall)
            args.putString("DATE", date)
            args.putString("TIME", time)
            args.putStringArrayList("SEATS", ArrayList(seats))
            args.putString("TOTAL", total)
            args.putString("SNACKS", snacks)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        _binding = BookingPaymentViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setUIForBooking(true)

        loadArguments()
        setupSummary()
        fetchUserData()
        setupListeners()
        setupCardFormatting()
    }

    private fun loadArguments() {
        arguments?.let {
            movieTitle = it.getString("MOVIE_TITLE", "")
            cinemaName = it.getString("CINEMA", "")
            hallName = it.getString("HALL", "")
            date = it.getString("DATE", "")
            time = it.getString("TIME", "")
            selectedSeats = it.getStringArrayList("SEATS") ?: listOf()
            totalPrice = it.getString("TOTAL", "0.00€")
            snacksInfo = it.getString("SNACKS", "")
        }
    }

    private fun setupSummary() {
        binding.lblSummaryMovie.text = movieTitle
        binding.lblSummaryCinema.text = cinemaName
        binding.lblSummaryHall.text = hallName
        binding.lblSummaryDate.text = getString(R.string.date_time_format, date, time)
        binding.lblSummarySeats.text = selectedSeats.joinToString(", ")
        binding.lblSummaryTotal.text = totalPrice
    }

    private fun fetchUserData() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc != null && isAdded) {
                    userAge = doc.getLong("age")?.toInt() ?: 0
                    preferredCard = doc.get("cardInfo") as? Map<String, Any>
                    updatePaymentUI()
                }
            }
    }

    private fun updatePaymentUI() {
        if (userAge < 18) {
            isCardPayment = false
            binding.layoutPaymentToggle.visibility = View.GONE
            showCashView()
        } else {
            binding.layoutPaymentToggle.visibility = View.VISIBLE
            if (preferredCard != null) {
                binding.btnTogglePreferred.visibility = View.VISIBLE
                showPreferredCardOption()
            } else {
                binding.btnTogglePreferred.visibility = View.GONE
                showCardForm()
            }
        }
    }

    private fun showPreferredCardOption() {
        isCardPayment = true
        binding.boxCardForm.visibility = View.VISIBLE
        binding.boxCashMessage.visibility = View.GONE
        
        val activeColor = ContextCompat.getColor(requireContext(), R.color.red_primary)
        binding.btnTogglePreferred.backgroundTintList = ColorStateList.valueOf(activeColor)
        binding.btnTogglePreferred.setTextColor(Color.WHITE)
        binding.btnToggleCard.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
        binding.btnToggleCard.setTextColor(Color.WHITE)
        binding.btnToggleCash.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
        binding.btnToggleCash.setTextColor(Color.WHITE)

        val fullNumber = preferredCard!!["cardNumber"] as? String ?: ""
        val first3 = if (fullNumber.length >= 3) fullNumber.take(3) else ""
        val last4 = if (fullNumber.length >= 4) fullNumber.takeLast(4) else "****"
        val isAmex = fullNumber.startsWith("34") || fullNumber.startsWith("37")
        
        binding.txtHolder.setText(preferredCard!!["cardHolder"] as? String ?: "")
        binding.txtCardNumber.setText(if (isAmex) "$first3* **** **** ${fullNumber.takeLast(3)}" else "$first3* **** **** $last4")
        binding.txtCardNumber.isEnabled = false
        binding.txtHolder.isEnabled = false
        binding.txtMonth.visibility = View.GONE
        binding.txtYear.visibility = View.GONE
        binding.txtCVV.visibility = View.GONE
        
        updateCardLogo(fullNumber)
    }

    private fun showCardForm() {
        isCardPayment = true
        binding.boxCardForm.visibility = View.VISIBLE
        binding.boxCashMessage.visibility = View.GONE
        
        val activeColor = ContextCompat.getColor(requireContext(), R.color.red_primary)
        binding.btnToggleCard.backgroundTintList = ColorStateList.valueOf(activeColor)
        binding.btnToggleCard.setTextColor(Color.WHITE)
        binding.btnTogglePreferred.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
        binding.btnTogglePreferred.setTextColor(Color.WHITE)
        binding.btnToggleCash.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
        binding.btnToggleCash.setTextColor(Color.WHITE)
        
        binding.txtCardNumber.isEnabled = true
        binding.txtHolder.isEnabled = true
        binding.txtCardNumber.setText("")
        binding.txtHolder.setText("")
        binding.txtMonth.visibility = View.VISIBLE
        binding.txtYear.visibility = View.VISIBLE
        binding.txtCVV.visibility = View.VISIBLE
        binding.imgCardLogo.setImageResource(R.drawable.p_card_default)
    }

    private fun showCashView() {
        isCardPayment = false
        binding.boxCardForm.visibility = View.GONE
        binding.boxCashMessage.visibility = View.VISIBLE
        
        val activeColor = ContextCompat.getColor(requireContext(), R.color.red_primary)
        binding.btnToggleCash.backgroundTintList = ColorStateList.valueOf(activeColor)
        binding.btnToggleCash.setTextColor(Color.WHITE)
        binding.btnToggleCard.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
        binding.btnToggleCard.setTextColor(Color.WHITE)
        binding.btnTogglePreferred.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
        binding.btnTogglePreferred.setTextColor(Color.WHITE)
    }

    private fun setupListeners() {
        binding.btnTogglePreferred.setOnClickListener { showPreferredCardOption() }
        binding.btnToggleCard.setOnClickListener { showCardForm() }
        binding.btnToggleCash.setOnClickListener { showCashView() }
        binding.btnFinish.setOnClickListener { handleFinish() }
    }

    private fun setupCardFormatting() {
        binding.txtCardNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val digits = s.toString().replace(" ", "")
                updateCardLogo(digits)
                
                // Formatting logic (simplified for briefness, like in FX controller)
                val formatted = StringBuilder()
                for (i in digits.indices) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ")
                    formatted.append(digits[i])
                }
                if (formatted.toString() != s.toString()) {
                    binding.txtCardNumber.removeTextChangedListener(this)
                    binding.txtCardNumber.setText(formatted.toString())
                    binding.txtCardNumber.setSelection(formatted.length)
                    binding.txtCardNumber.addTextChangedListener(this)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateCardLogo(number: String) {
        val logo = when {
            number.startsWith("4") -> R.drawable.p_visa
            number.matches(Regex("^(5[1-5]|2[2-7]).*")) -> R.drawable.p_mastercard
            number.startsWith("34") || number.startsWith("37") -> R.drawable.p_american_express
            else -> R.drawable.p_card_default
        }
        binding.imgCardLogo.setImageResource(logo)
    }

    private fun handleFinish() {
        if (isCardPayment && preferredCard == null) {
            if (!validateInputs()) return
        }
        
        // Αν είναι σε Preferred mode, δεν χρειάζεται validate (είναι ήδη ελεγμένη)
        if (isCardPayment && preferredCard != null && binding.btnTogglePreferred.backgroundTintList == ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_primary))) {
            // OK
        } else if (isCardPayment && !validateInputs()) {
            return
        }

        startLoading()
    }

    private fun validateInputs(): Boolean {
        val holder = binding.txtHolder.text.toString()
        val number = binding.txtCardNumber.text.toString().replace(" ", "")
        val month = binding.txtMonth.text.toString()
        val year = binding.txtYear.text.toString()
        val cvv = binding.txtCVV.text.toString()

        if (holder.isEmpty() || number.isEmpty() || month.isEmpty() || year.isEmpty() || cvv.isEmpty()) {
            showError("Συμπληρώστε όλα τα πεδία")
            return false
        }

        val isAmex = number.startsWith("34") || number.startsWith("37")
        if ((isAmex && number.length != 15) || (!isAmex && number.length != 16)) {
            showError(if (isAmex) "Η Amex θέλει 15 ψηφία" else "Η κάρτα θέλει 16 ψηφία")
            return false
        }

        try {
            if (DateUtils.isCardExpired(month.toInt(), year.toInt())) {
                showError("Η κάρτα έχει λήξει")
                return false
            }
        } catch (e: Exception) { return false }

        return true
    }

    private fun showError(msg: String) {
        binding.lblError.text = msg
        binding.lblError.visibility = View.VISIBLE
    }

    private fun startLoading() {
        binding.btnFinish.visibility = View.GONE
        binding.loadingBox.visibility = View.VISIBLE
        
        Handler(Looper.getMainLooper()).postDelayed({
            // Εδώ θα πηγαίναμε στο BookingFinalFragment
            Toast.makeText(requireContext(), "Η κράτηση ολοκληρώθηκε!", Toast.LENGTH_LONG).show()
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        _binding = null
    }
}
