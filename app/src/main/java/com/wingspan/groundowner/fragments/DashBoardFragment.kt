package com.wingspan.groundowner.fragments

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.wingspan.groundowner.R
import com.wingspan.groundowner.activities.MainActivity
import com.wingspan.groundowner.databinding.AlertSettingDialogBinding
import com.wingspan.groundowner.databinding.FragmentDashBoardBinding
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashBoardFragment : Fragment() {
    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding!!
    lateinit var alertDialog:AlertDialog

    @Inject
    lateinit var sharedPreferences:UserPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentDashBoardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        setObservers()
    }
    private fun setObservers() {
        binding.apply {
            tvName.text=sharedPreferences.getUsername()
            ivProfile.setDebouncedClickListener(){
                rightDialog()
            }
        }
    }

    private fun rightDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = AlertSettingDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        // Set window layout parameters
        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.TOP or Gravity.END) // Position at top-right

        // Set transparent background
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Apply slide-in animation (right to left)
        val slideIn = ObjectAnimator.ofFloat(dialogBinding.root, "translationX", 1000f, 0f)
        slideIn.duration = 300 // Adjust duration as needed
        slideIn.start()

        dialogBinding.apply{
            btnLogout.setDebouncedClickListener {
                logout()
            }
            tvProfileName.text=sharedPreferences.getUsername()
            tvMobile.text=sharedPreferences.getMobileNumber()
            tvEmail.text=sharedPreferences.getEmail()
        }

        dialog.show()
    }

    private fun logout() {

        val builder= AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure want to Logout?")
        builder.setPositiveButton("OK"){dialog,which->
            sharedPreferences= UserPreferences(requireContext())
            sharedPreferences.logoutAdmin()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()

        }
        builder.setNegativeButton("CANCEL"){dialog,which->
            dialog.dismiss()
        }
        alertDialog=builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }


    private fun setUI() {
        binding.apply {
            Log.d("sharedpre","-->Shared ${sharedPreferences.getUsername()}")
            tvName.text=sharedPreferences.getUsername()
            ivProfile.setDebouncedClickListener(){
                rightDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}