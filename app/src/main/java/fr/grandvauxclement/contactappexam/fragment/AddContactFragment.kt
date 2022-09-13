package fr.grandvauxclement.contactappexam.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.grandvauxclement.contactappexam.ContactApplication
import fr.grandvauxclement.contactappexam.ContactViewModel
import fr.grandvauxclement.contactappexam.ContactViewModelFactory
import fr.grandvauxclement.contactappexam.R
import fr.grandvauxclement.contactappexam.data.Contact
import fr.grandvauxclement.contactappexam.databinding.FragmentAddContactBinding

class AddContactFragment : Fragment() {

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    // to share the ViewModel across fragments.
    private val viewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory(
            (activity?.application as ContactApplication).database
                .contactDao()
        )
    }
    private val navigationArgs: ContactDetailFragmentArgs by navArgs()

    lateinit var contact: Contact

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.contactLastname.text.toString(),
            binding.contactFirstname.text.toString(),
            binding.contactCompany.text.toString(),
            binding.contactAddress.text.toString(),
            binding.contactNumTel.text.toString(),
            binding.contactEmail.text.toString(),
            binding.contactSectorActivity.selectedItem.toString(),
        )
    }

    /**
     * Binds views with the passed in [Contact] information.
     */
    private fun bind(contact: Contact) {
        //  val price = "%.2f".format(item.itemPrice)
        val myStrings : Array<String> = resources.getStringArray(R.array.sector_activity_list)
        var index = 0;
        var indexSelected = 0;
        myStrings.forEach { string ->
            if (string == contact.sectorActivity){
                indexSelected = index
            }
            index ++
        }
        binding.apply {
            contactLastname.setText(contact.lastname, TextView.BufferType.SPANNABLE)
            contactFirstname.setText(contact.firstname, TextView.BufferType.SPANNABLE)
            contactCompany.setText(contact.company, TextView.BufferType.SPANNABLE)
            contactAddress.setText(contact.address, TextView.BufferType.SPANNABLE)
            contactNumTel.setText(contact.numTel, TextView.BufferType.SPANNABLE)
            contactEmail.setText(contact.email, TextView.BufferType.SPANNABLE)
            contactSectorActivity.setSelection(indexSelected)
            saveAction.setOnClickListener { updateItem() }
        }
    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.contactLastname.text.toString(),
                binding.contactFirstname.text.toString(),
                binding.contactCompany.text.toString(),
                binding.contactAddress.text.toString(),
                binding.contactNumTel.text.toString(),
                binding.contactEmail.text.toString(),
                binding.contactSectorActivity.selectedItem.toString(),
                0
            )
            val action = AddContactFragmentDirections.actionAddContactFragmentToContactListFragment()
            findNavController().navigate(action)
        } else {
            val toast = Toast.makeText(this.context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    /**
     * Updates an existing Item in the database and navigates up to list fragment.
     */
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                this.navigationArgs.itemId,
                this.binding.contactLastname.text.toString(),
                this.binding.contactFirstname.text.toString(),
                this.binding.contactCompany.text.toString(),
                this.binding.contactAddress.text.toString(),
                this.binding.contactNumTel.text.toString(),
                this.binding.contactEmail.text.toString(),
                this.binding.contactSectorActivity.selectedItem.toString(),
                0
            )
            val action = AddContactFragmentDirections.actionAddContactFragmentToContactListFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Called when the view is created.
     * The itemId Navigation argument determines the edit item  or add new item.
     * If the itemId is positive, this method retrieves the information from the database and
     * allows the user to update it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                contact = selectedItem
                bind(contact)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewItem()
            }
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}