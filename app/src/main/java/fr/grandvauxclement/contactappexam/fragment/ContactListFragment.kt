package fr.grandvauxclement.contactappexam.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import fr.grandvauxclement.contactappexam.ContactApplication
import fr.grandvauxclement.contactappexam.ContactViewModel
import fr.grandvauxclement.contactappexam.ContactViewModelFactory
import fr.grandvauxclement.contactappexam.R
import fr.grandvauxclement.contactappexam.adapter.ContactListAdapter
import fr.grandvauxclement.contactappexam.databinding.ContactListFragmentBinding

class ContactListFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory(
            (activity?.application as ContactApplication).database.contactDao()
        )
    }

    private var _binding: ContactListFragmentBinding? = null
    private val binding get() = _binding!!
    private val navigationArgs: ContactListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ContactListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ContactListAdapter {
            val action =
                ContactListFragmentDirections.actionContactListFragmentToContactDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.
        viewModel.allContacts.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
        if (navigationArgs.favourite == "false"){
            binding.kindContactDisplay.text = "Tous les Contacts"
        } else {
            binding.kindContactDisplay.text = "Mes Contacts Favories"
        }


        binding.floatingFavouriteButton.setOnClickListener {
            var goFavoriteDisplay = false
            if (navigationArgs.favourite == "false"){
                goFavoriteDisplay = true
            }

            viewModel.displayFavouriteContact(goFavoriteDisplay)
            val action = ContactListFragmentDirections.actionContactListFragmentToContactListFragment().setFavourite(goFavoriteDisplay.toString())
            findNavController().navigate(action)
        }

        binding.floatingActionButton.setOnClickListener {
            val action = ContactListFragmentDirections.actionContactListFragmentToAddContactFragment(
                getString(R.string.add_fragment_title),
                -1
            )
            this.findNavController().navigate(action)
        }
    }
}