package com.comixa.app.ui.labs.lab3.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.comixa.app.R
import com.comixa.app.adapter.lab3.ArticlesAdapter
import com.comixa.app.databinding.FragmentLab3ItemsBinding
import com.comixa.app.viewmodel.Lab3.Lab3ItemsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Lab3ItemsFragment : Fragment() {

    private var _binding: FragmentLab3ItemsBinding? = null
    private val binding get() = _binding!!

    private val vm: Lab3ItemsViewModel by viewModels()
    private lateinit var adapter: ArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLab3ItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ArticlesAdapter { article ->
            val args = bundleOf("url" to article.link)
            findNavController().navigate(R.id.nav_lab3_web, args)
        }
        binding.rvArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticles.adapter = adapter

        val sourceId = requireArguments().getLong("sourceId", -1L)
        if (sourceId <= 0L) {
            return
        }
        vm.setSource(sourceId)

        binding.swipe.setOnRefreshListener { vm.refresh() }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.articles.collectLatest { list ->
                adapter.submitList(list)
                binding.stateEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.swipe.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
