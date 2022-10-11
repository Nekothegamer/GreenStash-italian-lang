/*
MIT License

Copyright (c) 2022 Stɑrry Shivɑm // This file is part of GreenStash.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.starry.greenstash.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.starry.greenstash.R
import com.starry.greenstash.database.Item
import com.starry.greenstash.databinding.FragmentHomeBinding
import com.starry.greenstash.ui.adapters.HomeRVAdapter
import com.starry.greenstash.ui.listeners.GoalClickListener
import com.starry.greenstash.ui.viewmodels.HomeViewModel
import com.starry.greenstash.ui.viewmodels.SharedViewModel
import com.starry.greenstash.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(), GoalClickListener {

    private var _binding: FragmentHomeBinding? = null

    /** This property is only valid between onCreateView and
    onDestroyView. */
    private val binding get() = _binding!!

    /** Home fragments view model. */
    private val viewModel: HomeViewModel by viewModels()

    /** Shared view model class. */
    private lateinit var sharedViewModel: SharedViewModel

    /** Home recycle view adapter. */
    private lateinit var adapter: HomeRVAdapter

    /** Navigation options for adding animations when
     * navigating between fragments. */
    @Inject
    lateinit var navOptions: NavOptions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set click listener on add goal fab button.
        binding.fab.setOnClickListener {
            findNavController().navigate(
                R.id.action_HomeFragment_to_InputFragment,
                null, navOptions
            )
        }
        // attach adapter to recycler view.
        adapter = HomeRVAdapter(requireContext(), this)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecyclerView.adapter = adapter
        // observe changes in items array and update homepage accordingly.
        viewModel.allItems.observe(viewLifecycleOwner) { itemList ->
            itemList.let {
                adapter.allItems = it
                checkDataset()
            }
        }
        // hide fab button on scrolling.
        binding.mainRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 0) {
                        // Scroll Down
                        if (binding.fab.isShown) {
                            binding.fab.hide()
                        }
                    } else if (dy < 0) {
                        // Scroll Up
                        if (!binding.fab.isShown) {
                            binding.fab.show()
                        }
                    }
                }
            }
        )
    }

    override fun onDepositClicked(item: Item) {
        if (item.currentAmount >= item.totalAmount) {
            getString(R.string.goal_already_achieved).toSnackbar(binding.root)
        } else {
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.transaction_dialog, null)
            val amountEditText =
                dialogView.findViewById<TextInputEditText>(R.id.transactionInputAmount)
            val notesEditText =
                dialogView.findViewById<TextInputEditText>(R.id.transactionInputNotes)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.transactionDialogTitle)
            dialogTitle.text = getString(R.string.deposit_dialog_title)
            // build alert dialog.
            val alertDialog = MaterialAlertDialogBuilder(requireContext())
            alertDialog.setView(dialogView)
            // set negative button.
            alertDialog.setNegativeButton(getString(R.string.dialog_negative_btn1), null)
            // set positive button.
            alertDialog.setPositiveButton(getString(R.string.dialog_positive_btn1)) { _, _ ->
                if (amountEditText.text!!.validateAmount()) {
                    val newAmount = amountEditText.text.toString().replace(',', '.').toFloat()
                    viewModel.deposit(newAmount, notesEditText.text!!, item, requireContext())
                } else {
                    getString(R.string.amount_empty_err).toSnackbar(binding.root)
                }
            }
            alertDialog.create().show()
        }

    }

    override fun onWithdrawClicked(item: Item) {
        if (item.currentAmount == 0f) {
            getString(R.string.withdraw_btn_error).toSnackbar(binding.root)
        } else {
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.transaction_dialog, null)
            val amountEditText =
                dialogView.findViewById<TextInputEditText>(R.id.transactionInputAmount)
            val notesEditText =
                dialogView.findViewById<TextInputEditText>(R.id.transactionInputNotes)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.transactionDialogTitle)
            dialogTitle.text = getString(R.string.withdraw_dialog_title)
            // build alert dialog.
            val alertDialog = MaterialAlertDialogBuilder(requireContext())
            alertDialog.setView(dialogView)
            // set negative button.
            alertDialog.setNegativeButton(getString(R.string.dialog_negative_btn1), null)
            // set positive button.
            alertDialog.setPositiveButton(getString(R.string.dialog_positive_btn1)) { _, _ ->
                if (amountEditText.text!!.validateAmount()) {
                    val newAmount = amountEditText.text.toString().replace(',', '.').toFloat()
                    viewModel.withdraw(newAmount, notesEditText.text!!, item, requireContext())

                } else {
                    getString(R.string.amount_empty_err).toSnackbar(binding.root)
                }
            }
            alertDialog.create().show()
        }
    }

    override fun onInfoClicked(item: Item) {
        sharedViewModel.setInfoItem(item)
        findNavController().navigate(
            R.id.action_HomeFragment_to_InfoFragment,
            null, navOptions
        )
    }


    override fun onEditClicked(item: Item) {
        sharedViewModel.setEditData(item)
        findNavController().navigate(
            R.id.action_HomeFragment_to_InputFragment,
            null, navOptions
        )
    }

    override fun onDeleteClicked(item: Item) {
        val alertDialog = MaterialAlertDialogBuilder(requireContext())
        alertDialog.setTitle(requireContext().getString(R.string.goal_delete_confirmation))
        alertDialog.setCancelable(false)
        // set negative button.
        alertDialog.setNegativeButton("Cancel") { _, _ ->
        }
        alertDialog.setPositiveButton(getString(R.string.dialog_positive_btn2)) { _, _ ->
            viewModel.deleteItem(item)
            getString(R.string.goal_delete_success).toSnackbar(binding.root)
        }
        alertDialog.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val searchMenu = menu.findItem(R.id.actionSearch).actionView
        (searchMenu as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchItem(newText!!)
                return false
            }
        })

        val filterMenu = menu.findItem(R.id.actionFilter)
        filterMenu.setOnMenuItemClickListener {
            showFilterDialog(); true
        }
    }

    private fun showFilterDialog() {
        val itemList: List<Item> = viewModel.allItems.value!!
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.filter_menu)

        val filterAll = bottomSheetDialog.findViewById<LinearLayout>(R.id.filterAll)
        val filterOngoing = bottomSheetDialog.findViewById<LinearLayout>(R.id.filterOngoing)
        val filterCompleted = bottomSheetDialog.findViewById<LinearLayout>(R.id.filterCompleted)

        filterAll!!.setOnClickListener {
            adapter.allItems = itemList
            bottomSheetDialog.hide()
        }
        filterOngoing!!.setOnClickListener {
            val newList = itemList.filter { it.currentAmount < it.totalAmount }
            if (newList.isNotEmpty()) {
                adapter.allItems = newList
            } else {
                getString(R.string.no_ongoing_goals).toSnackbar(binding.root)
            }
            bottomSheetDialog.hide()
        }
        filterCompleted!!.setOnClickListener {
            val newList = itemList.filter { it.currentAmount >= it.totalAmount }
            if (newList.isNotEmpty()) {
                adapter.allItems = newList
            } else {
                getString(R.string.no_completed_goals).toSnackbar(binding.root)
            }
            bottomSheetDialog.hide()
        }
        bottomSheetDialog.show()
    }

    private fun searchItem(text: String) {
        // create a new array list to filter goals.
        val filteredList: ArrayList<Item> = ArrayList()

        // running a for loop to compare elements.
        for (item in viewModel.allItems.value!!) {
            // check if the entered string matched with any item in recycler view.
            if (item.title.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(item)
            }
        }
        if (viewModel.allItems.value!!.isNotEmpty() && filteredList.isEmpty()) {
            getString(R.string.item_not_found).toToast(requireContext())
        }
        adapter.allItems = filteredList
    }


    private fun checkDataset() {
        if (viewModel.allItems.value?.isEmpty() == true) {
            binding.mainRecyclerView.gone()
            binding.homeEmptyView.visible()
        } else {
            binding.homeEmptyView.gone()
            binding.mainRecyclerView.visible()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}