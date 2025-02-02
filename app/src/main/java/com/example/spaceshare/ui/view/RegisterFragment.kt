package com.example.spaceshare.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentRegisterBinding
import com.example.spaceshare.ui.viewmodel.AuthViewModel
import com.example.spaceshare.utils.ToastUtil
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    @Inject
    lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        auth = FirebaseAuth.getInstance()

        configureObservers()
        configureButtons()
    }

    private fun configureObservers() {
        authViewModel.registerStatus.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                navController.navigate(R.id.action_registerFragment_to_loginFragment)
            } else {
                ToastUtil.showShortToast(requireContext(), result.message)
            }
        }
    }

    private fun configureButtons() {
        binding.btnRegister.setOnClickListener {
            authViewModel.register(binding.registerEmail.text.toString(), binding.registerPassword.text.toString())
        }
    }
}