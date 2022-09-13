package fr.grandvauxclement.contactappexam.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.grandvauxclement.contactappexam.ContactApplication
import fr.grandvauxclement.contactappexam.ContactViewModel
import fr.grandvauxclement.contactappexam.ContactViewModelFactory
import fr.grandvauxclement.contactappexam.R
import fr.grandvauxclement.contactappexam.data.Contact
import fr.grandvauxclement.contactappexam.databinding.FragmentContactDetailBinding

class ContactDetailFragment : Fragment() {
    private val navigationArgs: ContactDetailFragmentArgs by navArgs()
    lateinit var item: Contact

    private val viewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory(
            (activity?.application as ContactApplication).database.contactDao()
        )
    }

    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Binds views with the passed in item data.
     */
    private fun bind(contact: Contact) {
        binding.apply {
            contactFullName.text = "${contact.lastname} ${contact.firstname}"
            contactCompany.text = contact.company
            contactAddress.text = contact.address
            contactNumTel.text = contact.numTel
            contactEmail.text = contact.email
            contactSectorActivity.text = contact.sectorActivity

            favoriteItem.text = displayGoodTextForFavoriteButton(contact.favors)
            favoriteItem.setOnClickListener { updateFavoriteContact()  }
            deleteItem.setOnClickListener { showConfirmationDialog() }
            editItem.setOnClickListener { editItem() }

            callButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, (Uri.parse("tel:" + contact.numTel)))
                startActivity(intent)
            }

            localizeButton.setOnClickListener {
                val queryUrl: Uri = Uri.parse("https://www.google.fr/maps/place/${contact.address}")
                val intent = Intent(Intent.ACTION_VIEW, queryUrl)
                startActivity(intent)
            }

            smsButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO, (Uri.parse("smsto:" + contact.numTel)))
                intent.putExtra("sms_body", "Salut, je test l'envoie d'un sms ! :)")
                startActivity(intent)
            }
        }
    }

    /**
     * Navigate to the Edit item screen.
     */
    private fun editItem() {
        val action = ContactDetailFragmentDirections.actionContactDetailFragmentToAddContactFragment(
            getString(R.string.edit_fragment_title),
             item.id
        )
        this.findNavController().navigate(action)
    }

    private fun displayGoodTextForFavoriteButton(isFavorite: Int) : String {
        if (isFavorite == 1) {
            return "Supprimer des Favoris"
        } else{
            return "Ajouter aux favoris"
        }
    }

    private fun updateFavoriteContact() {
        var isFavorite : Int
        if (item.favors == 1){
            isFavorite = 0
        }else {
            isFavorite = 1
        }
        viewModel.updateItem(
            item.id,
            item.lastname,
            item.firstname,
            item.company,
            item.address,
            item.numTel,
            item.email,
            item.sectorActivity,
            isFavorite
        )
        findNavController().navigateUp()
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()
            }
            .show()
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteItem() {
        viewModel.deleteItem(item)
        findNavController().navigateUp()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.itemId
        // Retrieve the item details using the itemId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
            item = selectedItem
            bind(item)
        }
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}