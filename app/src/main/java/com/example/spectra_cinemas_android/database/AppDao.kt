package com.example.spectra_cinemas_android.database

import androidx.room.*
import com.example.spectra_cinemas_android.models.*

@Dao
interface AppDao {

    // --- CINEMAS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCinema(cinema: Cinema): Long

    @Update
    suspend fun updateCinema(cinema: Cinema): Int

    @Delete
    suspend fun deleteCinema(cinema: Cinema): Int

    @Query("DELETE FROM cinemas WHERE name = :name")
    suspend fun deleteCinemaByName(name: String): Int

    @Query("SELECT * FROM cinemas")
    suspend fun getAllCinemas(): List<Cinema>

    @Query("SELECT * FROM cinemas")
    fun getAllCinemasLive(): kotlinx.coroutines.flow.Flow<List<Cinema>>

    @Query("SELECT * FROM cinemas WHERE city = :city")
    suspend fun getCinemasByCity(city: String): List<Cinema>

    @Query("SELECT * FROM snacks WHERE price < :maxPrice")
    fun getCheapSnacksLive(maxPrice: Double): kotlinx.coroutines.flow.Flow<List<Snack>>

    @Query("SELECT * FROM halls")
    suspend fun getAllHalls(): List<Hall>

    @Query("SELECT * FROM halls")
    fun getAllHallsLive(): kotlinx.coroutines.flow.Flow<List<Hall>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHall(hall: Hall): Long

    @Update
    suspend fun updateHall(hall: Hall): Int

    @Delete
    suspend fun deleteHall(hall: Hall): Int

    @Query("DELETE FROM halls WHERE title = :title")
    suspend fun deleteHallByTitle(title: String): Int

    @Query("SELECT * FROM offices")
    suspend fun getAllOffices(): List<Office>

    @Query("SELECT * FROM offices")
    fun getAllOfficesLive(): kotlinx.coroutines.flow.Flow<List<Office>>

    // --- SNACKS (Κυλικείο) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnack(snack: Snack): Long

    @Delete
    suspend fun deleteSnack(snack: Snack): Int

    @Query("DELETE FROM snacks WHERE name = :name")
    suspend fun deleteSnackByName(name: String): Int

    @Query("SELECT * FROM snacks WHERE price < :maxPrice")
    suspend fun getCheapSnacks(maxPrice: Double): List<Snack>

    @Query("SELECT * FROM snacks WHERE type = :type")
    suspend fun getSnacksByType(type: String): List<Snack>

    // --- OFFICES (Επικοινωνία) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffice(office: Office): Long

    @Update
    suspend fun updateOffice(office: Office): Int

    @Delete
    suspend fun deleteOffice(office: Office): Int

    @Query("DELETE FROM offices WHERE title = :title")
    suspend fun deleteOfficeByTitle(title: String): Int

    @Query("SELECT * FROM offices WHERE title LIKE '%' || :query || '%'")
    suspend fun searchOffices(query: String): List<Office>

    // --- MOVIES (Υπήρχε ήδη Entity) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie): Long

    @Query("SELECT * FROM movies WHERE isComingSoon = :isComing")
    suspend fun getMoviesByStatus(isComing: Boolean): List<Movie>

    // --- TICKETS (Τοπικό Ιστορικό) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket): Long

    @Query("SELECT * FROM local_tickets ORDER BY orderId DESC")
    suspend fun getAllTickets(): List<Ticket>

    @Delete
    suspend fun deleteTicket(ticket: Ticket): Int

    @Query("SELECT * FROM local_tickets WHERE orderId = :orderId")
    suspend fun getTicketById(orderId: String): Ticket?
}
