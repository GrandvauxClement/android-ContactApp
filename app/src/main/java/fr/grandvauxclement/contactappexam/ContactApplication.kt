package fr.grandvauxclement.contactappexam

import android.app.Application
import fr.grandvauxclement.contactappexam.data.ContactRoomDatabase

class ContactApplication: Application() {

    val database: ContactRoomDatabase by lazy { ContactRoomDatabase.getDatabase(this)}
}