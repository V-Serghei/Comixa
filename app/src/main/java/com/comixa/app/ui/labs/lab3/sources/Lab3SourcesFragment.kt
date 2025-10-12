package com.comixa.app.ui.labs.lab3.sources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.comixa.app.R
import com.comixa.app.databinding.FragmentLab3SourcesBinding
import com.comixa.app.databinding.DialogAddSourceBinding
import com.comixa.data.rss.SourceEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Lab3SourcesFragment : Fragment() {

    private var _binding: FragmentLab3SourcesBinding? = null
    private val binding get() = _binding!!

    private val vm: Lab3SourcesViewModel by viewModels()
    private lateinit var adapter: SourcesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLab3SourcesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = SourcesAdapter(
            onClick = { openItems(it) },
            onRefresh = { vm.refresh(it.id) },
            onDelete = { vm.delete(it) }
        )
        binding.rvSources.layoutManager = LinearLayoutManager(requireContext()) // ВАЖНО!
        binding.rvSources.adapter = adapter

        binding.fabAdd.setOnClickListener { showAddDialog() }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.sources.collectLatest { list ->
                adapter.submitList(list)
                binding.stateEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun openItems(source: SourceEntity) {
        findNavController().navigate(
            R.id.nav_lab3_items,
            bundleOf("sourceId" to source.id)
        )
    }

    private fun showAddDialog() {
        val dialogBinding = DialogAddSourceBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setTitle("Add RSS URL")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val url = dialogBinding.etUrl.text?.toString().orEmpty()
                if (url.isNotBlank()) vm.add(url)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
