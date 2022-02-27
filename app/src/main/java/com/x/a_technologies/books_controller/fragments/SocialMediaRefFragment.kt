package com.x.a_technologies.books_controller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.x.a_technologies.books_controller.R
import com.x.a_technologies.books_controller.databinding.FragmentSocialMediaRefBinding
import com.x.a_technologies.books_controller.datas.DatabaseRef
import com.x.a_technologies.books_controller.models.SocialMediaReferences
import com.x.a_technologies.books_controller.utils.MainViewModel

class SocialMediaRefFragment : Fragment() {

    lateinit var binding: FragmentSocialMediaRefBinding
    lateinit var viewModel: MainViewModel
    lateinit var socialMediaRef: SocialMediaReferences

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
        binding = FragmentSocialMediaRefBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDataLoading(true)
        viewModel.loadSocialMediaRef()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.save.setOnClickListener {
            checkChanged()
        }

    }

    private fun checkChanged(){
        val phoneNumber = binding.phoneNumber.text.toString()
        val telegramUrl = binding.telegram.text.toString()
        val instagramUrl = binding.instagram.text.toString()

        if (phoneNumber != socialMediaRef.phoneNumber ||
            telegramUrl != socialMediaRef.telegramUrl ||
            instagramUrl != socialMediaRef.instagramUrl){

            saveChanged(SocialMediaReferences(phoneNumber, telegramUrl, instagramUrl))

        }else{
            findNavController().popBackStack()
        }
    }

    private fun saveChanged(changedSocial:SocialMediaReferences){
        isSaveLoading(true)

        DatabaseRef.socialMediaRef.setValue(changedSocial).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(requireActivity(), getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show()
                isSaveLoading(false)
                findNavController().popBackStack()
            }else{
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                isSaveLoading(false)
            }
        }
    }

    private fun initObservers() {
        viewModel.socialMediaRefData.observe(requireActivity()) {
            socialMediaRef = it
            initViews()
            isDataLoading(false)
        }

        viewModel.error.observe(requireActivity()) {
            Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            binding.loadDataProgressBar.visibility = View.GONE
        }
    }

    private fun initViews(){
        binding.phoneNumber.setText(socialMediaRef.phoneNumber)
        binding.telegram.setText(socialMediaRef.telegramUrl)
        binding.instagram.setText(socialMediaRef.instagramUrl)
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
            binding.phoneNumber.visibility = View.GONE
            binding.telegram.visibility = View.GONE
            binding.instagram.visibility = View.GONE
            binding.loadDataProgressBar.visibility = View.VISIBLE
        } else {
            binding.phoneNumber.visibility = View.VISIBLE
            binding.telegram.visibility = View.VISIBLE
            binding.instagram.visibility = View.VISIBLE
            binding.loadDataProgressBar.visibility = View.GONE
        }
    }

}