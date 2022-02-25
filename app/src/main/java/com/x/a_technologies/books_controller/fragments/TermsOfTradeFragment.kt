package com.x.a_technologies.books_controller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.x.a_technologies.books_controller.R
import com.x.a_technologies.books_controller.adapters.TermsOfTradeAdapter
import com.x.a_technologies.books_controller.databinding.FragmentTermsOfTradeBinding
import com.x.a_technologies.books_controller.datas.DatabaseRef
import com.x.a_technologies.books_controller.models.TermsOfTrade
import com.x.a_technologies.books_controller.utils.MainViewModel

class TermsOfTradeFragment : Fragment() {

    lateinit var binding: FragmentTermsOfTradeBinding
    lateinit var viewModel: MainViewModel
    lateinit var termsOfTradeAdapter: TermsOfTradeAdapter
    var termsList = ArrayList<TermsOfTrade>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        initObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTermsOfTradeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        termsOfTradeAdapter = TermsOfTradeAdapter(termsList)
        binding.termsRv.adapter = termsOfTradeAdapter

        isDataLoading(true)
        viewModel.loadTermsOfTrade()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addLayer.setOnClickListener {
            termsList.add(TermsOfTrade(DatabaseRef.termsOfTradeRef.push().key!!))
            termsOfTradeAdapter.notifyItemInserted(termsList.size)
        }

        binding.save.setOnClickListener {
            saveChanged()
        }

    }

    private fun saveChanged(){
        val queryHashMap = HashMap<String, Any>()
        // o'zgarganlarini o'zini queryHashMap ga solish va saqlash for orqali
    }

    private fun isSaveLoading(bool: Boolean) {
        if (bool) {
            binding.save.visibility = View.GONE
            binding.savingProgressBar.visibility = View.VISIBLE
        } else {
            binding.save.visibility = View.VISIBLE
            binding.savingProgressBar.visibility = View.GONE
        }
    }

    private fun isDataLoading(bool: Boolean) {
        if (bool) {
            binding.termsRv.visibility = View.GONE
            binding.addLayer.visibility = View.GONE
            binding.loadDataProgressBar.visibility = View.VISIBLE
        } else {
            binding.termsRv.visibility = View.VISIBLE
            binding.addLayer.visibility = View.VISIBLE
            binding.loadDataProgressBar.visibility = View.GONE
        }
    }

    private fun initObservers(){
        viewModel.termsOfTradeData.observe(requireActivity()){
            termsList.addAll(it)
            termsOfTradeAdapter.notifyDataSetChanged()
            isDataLoading(false)
        }

        viewModel.error.observe(requireActivity()){
            Toast.makeText(requireActivity(), "Error!", Toast.LENGTH_SHORT).show()
            binding.loadDataProgressBar.visibility = View.GONE
        }
    }

}