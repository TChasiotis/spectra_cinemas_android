# Spectra Cinemas Android App 🎬

A comprehensive cinema application for Android, developed with **Kotlin** and **Firebase**. The app allows users to browse movies, manage their profiles, and make ticket bookings.

## 🚀 Features
- **User System:** Registration, Login, and Email Verification using Firebase Auth.
- **User Profile:** Management of personal details and preferred payment card.
- **Security:** Security measures for sensitive actions (e.g., account deletion requires re-authentication).
- **Smart Card Management:** Automatic card type recognition (Visa, Mastercard, Amex), number formatting, and expiration date validation.
- **Cloud Database:** Real-time data synchronization using Google Firestore.

## 🛠 Technologies
- **Language:** Kotlin
- **UI:** XML Layouts, Material Design 3, View Binding
- **Backend:** Firebase (Authentication, Firestore, Analytics)
- **Architecture:** MVVM (Work in Progress)

## 🔒 Security Note
For security reasons, the **`google-services.json`** configuration file is not included in the repository.

### How to run the project:
1. Create a new project in the [Firebase Console](https://console.firebase.google.com/).
2. Enable **Authentication** (Email/Password) and **Firestore Database**.
3. Add an Android app with the package name `com.example.spectra_cinemas_android`.
4. Download the `google-services.json` file and place it in the `app/` directory of the project.
5. Build and run the application.

---
*This application is part of my personal portfolio and demonstrates the ability to implement a full-stack mobile flow and robust security practices.*

---

# Spectra Cinemas Android App 🎬 (Ελληνικά)

Μια ολοκληρωμένη εφαρμογή κινηματογράφου για Android, αναπτυγμένη με **Kotlin** και **Firebase**. Η εφαρμογή επιτρέπει στους χρήστες να περιηγούνται σε ταινίες, να διαχειρίζονται το προφίλ τους και να πραγματοποιούν κρατήσεις εισιτηρίων.

## 🚀 Χαρακτηριστικά
- **Σύστημα Χρηστών:** Εγγραφή, Σύνδεση και Επαλήθευση μέσω Email με το Firebase Auth.
- **Προφίλ Χρήστη:** Διαχείριση προσωπικών στοιχείων και προτιμώμενης κάρτας πληρωμής.
- **Ασφάλεια:** Δικλείδες ασφαλείας για ευαίσθητες ενέργειες (π.χ. διαγραφή λογαριασμού με εκ νέου ταυτοποίηση).
- **Έξυπνη Διαχείριση Καρτών:** Αυτόματη αναγνώριση τύπου κάρτας (Visa, Mastercard, Amex), formatting αριθμού και έλεγχος ημερομηνίας λήξης.
- **Cloud Database:** Χρήση του Google Firestore για συγχρονισμό δεδομένων σε πραγματικό χρόνο.

## 🛠 Τεχνολογίες
- **Γλώσσα:** Kotlin
- **UI:** XML Layouts, Material Design 3, View Binding
- **Backend:** Firebase (Authentication, Firestore, Analytics)
- **Architecture:** MVVM (σε εξέλιξη)

## 🔒 Σημείωση Ασφαλείας
Για λόγους ασφαλείας, το αρχείο ρυθμίσεων **`google-services.json`** δεν περιλαμβάνεται στο αποθετήριο (repository). 

### Πώς να τρέξετε το project:
1. Δημιουργήστε ένα νέο project στο [Firebase Console](https://console.firebase.google.com/).
2. Ενεργοποιήστε το **Authentication** (Email/Password) και το **Firestore Database**.
3. Προσθέστε μια Android εφαρμογή με το package name `com.example.spectra_cinemas_android`.
4. Κατεβάστε το αρχείο `google-services.json` και τοποθετήστε το στον φάκελο `app/` του project.
5. Κάντε Build και τρέξτε την εφαρμογή.

---
*Αυτή η εφαρμογή αποτελεί μέρος του προσωπικού μου portfolio και επιδεικνύει την ικανότητα υλοποίησης πλήρους ροής δεδομένων (Full-stack mobile flow) και ορθών πρακτικών ασφαλείας.*
