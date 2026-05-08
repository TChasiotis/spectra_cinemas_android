package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.database.AppDatabase
import com.example.spectra_cinemas_android.databinding.BookingFinalViewBinding
import com.example.spectra_cinemas_android.models.Ticket
import com.example.spectra_cinemas_android.utils.DateUtils
import com.example.spectra_cinemas_android.utils.MovieData
import com.example.spectra_cinemas_android.utils.NotificationHelper
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Random

class BookingFinalFragment : Fragment() {

    private var _binding: BookingFinalViewBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(movie: String, cinema: String, hall: String, date: String, time: String, seats: String, price: String, status: String, snacks: String): BookingFinalFragment {
            val fragment = BookingFinalFragment()
            val orderId = generateOrderId()
            
            val actualDate = if (date.equals("Σήμερα", ignoreCase = true)) {
                val sdf = java.text.SimpleDateFormat("EEEE d/M", java.util.Locale("el", "GR"))
                sdf.format(java.util.Calendar.getInstance().time).replaceFirstChar { it.uppercase() }
            } else {
                date
            }

            val args = Bundle()
            args.putString("ORDER_ID", orderId)
            args.putString("MOVIE", movie)
            args.putString("CINEMA", cinema)
            args.putString("HALL", hall)
            args.putString("DATE", actualDate)
            args.putString("TIME", time)
            args.putString("SEATS", seats)
            args.putString("PRICE", price)
            args.putString("STATUS", status)
            args.putString("SNACKS", snacks)
            args.putBoolean("IS_NEW", true)
            fragment.arguments = args
            return fragment
        }
// ... rest remains similar

        fun newInstanceFromHistory(ticket: Ticket): BookingFinalFragment {
            val fragment = BookingFinalFragment()
            val args = Bundle()
            args.putString("ORDER_ID", ticket.orderId)
            args.putString("MOVIE", ticket.movieTitle)
            args.putString("CINEMA", ticket.cinemaName)
            args.putString("HALL", ticket.hallName)
            args.putString("DATE", ticket.date)
            args.putString("TIME", ticket.time)
            args.putString("SEATS", ticket.seats)
            args.putString("PRICE", ticket.price)
            args.putString("STATUS", ticket.paymentStatus)
            args.putString("SNACKS", ticket.snacks)
            args.putBoolean("FROM_HISTORY", true)
            fragment.arguments = args
            return fragment
        }

        private fun generateOrderId(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val sb = StringBuilder("#")
            val rnd = Random()
            repeat(8) { sb.append(chars[rnd.nextInt(chars.length)]) }
            return sb.toString()
        }

        private fun saveTicketToCloud(ticket: Ticket) {
            val uid = Firebase.auth.currentUser?.uid ?: return
            Firebase.firestore.collection("users").document(uid)
                .collection("bookings")
                .document(ticket.orderId.replace("#", ""))
                .set(ticket)
        }

        private fun scheduleBookingNotifications(context: android.content.Context, ticket: Ticket) {
            val movieTimeMillis = com.example.spectra_cinemas_android.utils.DateUtils.parseBookingTimeToMillis(ticket.date, ticket.time)
            val currentTime = System.currentTimeMillis()

            // 1. Υπενθύμιση για έναρξη ταινίας (10 λεπτά πριν)
            val movieStartDelay = (movieTimeMillis - 10 * 60 * 1000) - currentTime
            if (movieStartDelay > 0) {
                NotificationHelper.scheduleNotification(
                    context,
                    "Έναρξη Ταινίας",
                    "Η ταινία ${ticket.movieTitle} ξεκινάει σε 10 λεπτά!",
                    movieStartDelay,
                    ticket.orderId
                )
            }

            // 2. Υπενθύμιση για πληρωμή στο ταμείο (αν εκκρεμεί, 20 λεπτά πριν το "ταμείο", άρα 40' πριν την ταινία)
            if (ticket.paymentStatus.contains("ΤΑΜΕΙΟ", ignoreCase = true)) {
                val cashPaymentDelay = (movieTimeMillis - 40 * 60 * 1000) - currentTime
                if (cashPaymentDelay > 0) {
                    NotificationHelper.scheduleNotification(
                        context,
                        "Πληρωμή στο Ταμείο",
                        "Παρακαλούμε προσέλθετε στο ταμείο σε 20 λεπτά για να εξοφλήσετε την κράτησή σας.",
                        cashPaymentDelay,
                        ticket.orderId
                    )
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BookingFinalViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val fromHistory = arguments?.getBoolean("FROM_HISTORY", false) ?: false
        (activity as? MainActivity)?.setUIForBooking(!fromHistory)

        if (fromHistory) {
            binding.lblSuccessHeader?.visibility = View.GONE
            binding.lblSuccessMessage?.visibility = View.GONE
        } else {
            // Προγραμματισμός Ειδοποιήσεων μόνο για ΝΕΑ κράτηση
            val orderId = arguments?.getString("ORDER_ID") ?: ""
            val movieTitle = arguments?.getString("MOVIE") ?: ""
            val cinema = arguments?.getString("CINEMA") ?: ""
            val hall = arguments?.getString("HALL") ?: ""
            val date = arguments?.getString("DATE") ?: ""
            val time = arguments?.getString("TIME") ?: ""
            val seats = arguments?.getString("SEATS") ?: ""
            val price = arguments?.getString("PRICE") ?: ""
            val snacks = arguments?.getString("SNACKS") ?: ""
            val status = arguments?.getString("STATUS") ?: ""

            val ticket = Ticket(orderId, movieTitle, cinema, hall, date, time, seats, price, snacks, status)
            scheduleBookingNotifications(requireContext(), ticket)

            // ΑΠΟΘΗΚΕΥΣΗ ΚΡΑΤΗΣΗΣ
            if (Firebase.auth.currentUser != null) {
                saveTicketToCloud(ticket)
            } else {
                // Επισκέπτης: Αποθήκευση στη Room
                val db = AppDatabase.getDatabase(requireContext())
                viewLifecycleOwner.lifecycleScope.launch {
                    db.appDao().insertTicket(ticket)
                }
            }
        }

        loadData()
        setupListeners()
    }

    private fun loadData() {
        val args = arguments ?: return
        val orderId = args.getString("ORDER_ID", "")
        val movieTitle = args.getString("MOVIE", "")
        val cinema = args.getString("CINEMA", "")
        val hall = args.getString("HALL", "")
        val date = args.getString("DATE", "")
        val time = args.getString("TIME", "")
        val seats = args.getString("SEATS", "")
        val price = args.getString("PRICE", "")
        val status = args.getString("STATUS", "")
        val snacks = args.getString("SNACKS", "")

        val displayDate = DateUtils.getDisplayDate(date)
        binding.lblMovie.text = movieTitle
        binding.lblCinema.text = cinema
        binding.lblHall.text = hall
        binding.lblDate.text = displayDate
        binding.lblTime.text = time
        binding.lblSeats.text = seats
        binding.lblOrderId.text = orderId

        // Φόρτωση Poster
        val movie = MovieData.getMovies().find { it.title == movieTitle }
        movie?.let { binding.ticketMoviePoster.setImageResource(it.imageResId) }

        // Δημιουργία QR Code
        generateQRCode(orderId, movieTitle, cinema, hall, date, time, seats, price, status, snacks)
    }

    private fun generateQRCode(orderId: String, movie: String, cinema: String, hall: String, date: String, time: String, seats: String, price: String, status: String, snacks: String) {
        try {
            val dataToEncode = "SPECTRA CINEMAS TICKET\n" +
                    "----------------------\n" +
                    "STATUS: $status\n" +
                    "Price: $price\n" +
                    "----------------------\n" +
                    "Order ID: $orderId\n" +
                    "Movie: $movie\n" +
                    "Cinema: $cinema\n" +
                    "Hall: $hall\n" +
                    "Date: $date | Time: $time\n" +
                    "Seats: $seats\n" +
                    "----------------------\n" +
                    "ITEMS: $snacks\n" +
                    "----------------------\n"

            val encodedData = URLEncoder.encode(dataToEncode, StandardCharsets.UTF_8.toString())
            val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=$encodedData"

            Picasso.get().load(qrUrl).into(binding.qrCodeImage)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupListeners() {
        binding.btnHome.setOnClickListener {
            (activity as? MainActivity)?.let {
                it.setLoggedIn(true) // Διασφάλιση σύνδεσης
                it.replaceFragment(MoviesFragment(), "Ταινίες")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
