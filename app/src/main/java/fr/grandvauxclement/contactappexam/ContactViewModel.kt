package fr.grandvauxclement.contactappexam

import androidx.lifecycle.*
import fr.grandvauxclement.contactappexam.data.Contact
import fr.grandvauxclement.contactappexam.data.ContactDao
import kotlinx.coroutines.launch

class ContactViewModel(private val contactDao: ContactDao): ViewModel() {
    // Cache all items form the database using LiveData.
    var allContacts: LiveData<List<Contact>> = contactDao.getAllContact().asLiveData()

    fun displayFavouriteContact(goFavoriteDisplay : Boolean) {
        allContacts = if (goFavoriteDisplay) {
            contactDao.findByIsFavorite().asLiveData()
        }else {
            contactDao.getAllContact().asLiveData()
        }
    }

    /**
     * Updates an existing Item in the database.
     */
    fun updateItem(
        id: Int,
        lastname: String,
        firstname: String,
        company: String,
        address: String,
        numTel: String,
        email: String,
        sectorActivity: String,
        favors: Int
    ) {
        val updatedItem = getUpdatedItemEntry(id, lastname, firstname, company, address, numTel,
        email, sectorActivity, favors)
        updateItem(updatedItem)
    }


    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateItem(contact: Contact) {
        viewModelScope.launch {
            contactDao.update(contact)
        }
    }

    /**
     * Inserts the new Item into database.
     */
    fun addNewItem( lastname: String,
                    firstname: String,
                    company: String,
                    address: String,
                    numTel: String,
                    email: String,
                    sectorActivity: String,
                    favors: Int) {
        val newItem = getNewItemEntry(lastname, firstname, company, address, numTel, email, sectorActivity, favors)
        insertItem(newItem)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertItem(contact: Contact) {
        viewModelScope.launch {
            contactDao.insert(contact)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteItem(contact: Contact) {
        viewModelScope.launch {
            contactDao.delete(contact)
        }
    }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveItem(id: Int): LiveData<Contact> {
        return contactDao.findById(id).asLiveData()
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(
        lastname: String,
        firstname: String,
        company: String,
        address: String,
        numTel: String,
        email: String,
        sectorActivity: String,
    ): Boolean {
        if (lastname.isBlank() || firstname.isBlank() || company.isBlank() || address.isBlank() ||
                numTel.isBlank() || email.isBlank() || sectorActivity.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [Contact] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Contact database.
     */
    private fun getNewItemEntry(
        lastname: String,
        firstname: String,
        company: String,
        address: String,
        numTel: String,
        email: String,
        sectorActivity: String,
        favors: Int
    ): Contact {
        return Contact(
            lastname = lastname,
            firstname = firstname,
            company = company,
            address = address,
            numTel = numTel,
            email = email,
            sectorActivity = sectorActivity,
            favors = favors
        )
    }

    /**
     * Called to update an existing entry in the Contact database.
     * Returns an instance of the [Contact] entity class with the item info updated by the user.
     */
    private fun getUpdatedItemEntry(
        id: Int,
        lastname: String,
        firstname: String,
        company: String,
        address: String,
        numTel: String,
        email: String,
        sectorActivity: String,
        favors: Int
    ): Contact {
        return Contact(
            id = id,
            lastname = lastname,
            firstname = firstname,
            company = company,
            address = address,
            numTel = numTel,
            email = email,
            sectorActivity = sectorActivity,
            favors = favors
        )
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class ContactViewModelFactory(private val contactDao: ContactDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(contactDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}