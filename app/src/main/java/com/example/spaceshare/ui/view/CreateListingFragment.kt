package com.example.spaceshare.ui.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.aemerse.slider.model.CarouselItem
import com.example.spaceshare.CropActivity
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentCreateListingBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.CreateListingViewModel
import com.example.spaceshare.utils.DecimalInputFilter
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateListingFragment : Fragment() {

    private var auth = FirebaseAuth.getInstance()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentCreateListingBinding
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    @Inject
    lateinit var createListingViewModel: CreateListingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_listing, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        auth = FirebaseAuth.getInstance()

        configureButtons()
        configureFilters()
        configureCropActivity()
        configureObservers()
    }

    private fun configureButtons() {
        // Close button
        binding.btnCloseListing.setOnClickListener {
            navController.popBackStack()
        }

        // Add photo button
        binding.btnAddPhoto.setOnClickListener {
            startForResult.launch(Intent(requireActivity(), CropActivity::class.java))
        }

        // Publish button
        binding.btnPublish.setOnClickListener {
            publishListing(binding.titleTextInput.text.toString(),
                binding.priceTextInput.text.toString().toDouble(),
                binding.descriptionTextInput.text.toString())
        }
    }

    private fun configureFilters() {
        binding.priceTextInput.filters = arrayOf<InputFilter>(DecimalInputFilter)
    }

    private fun configureObservers() {
        createListingViewModel.imageUris.observe(viewLifecycleOwner) { uris ->
            binding.carousel.registerLifecycle(lifecycle)
            val list = mutableListOf<CarouselItem>()
            uris.forEach { uri ->
                list.add(CarouselItem(imageUrl = uri.toString()))
            }
            binding.carousel.setData(list)
        }
    }

    private fun configureCropActivity() {
        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                @Suppress("DEPRECATION")
                val imageUri = result.data?.getParcelableExtra<Uri>("imageUri")
                if (imageUri != null) {
                    createListingViewModel.addImageUri(imageUri)
                }
            }
        }
    }

    private fun publishListing(title: String, price: Double, description: String) {
        val hostID = auth.currentUser?.uid
        val listing = Listing(title = title, price = price, description = description, hostId = hostID)
        createListingViewModel.publishListing(listing)
    }
}