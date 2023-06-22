package com.qdot.picdrive

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.qdot.picdrive.databinding.ModelBottomSheetLayoutBinding
import id.passage.android.Passage
import id.passage.android.PassageUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserInfoSheet(private val user : PassageUser, private val passage: Passage,private val activity: Activity) : BottomSheetDialogFragment() {
    private var _binding: ModelBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ModelBottomSheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileView.load("https://api.dicebear.com/6.x/thumbs/png"){
            placeholder(R.drawable.profile_ic)
            error(R.drawable.profile_ic)
            crossfade(true)
            transformations(CircleCropTransformation())
        }
        binding.emailText.text = user.email
        binding.idText.text = user.status
        binding.dateChip.text = "Last updated at ${user.updatedAt.toString().substring(0,10)}"
        binding.logoutBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                passage.signOutCurrentUser().apply {
                    withContext(Dispatchers.Main){
                        dismiss()
                        activity.startActivity(Intent(activity, AuthActivity::class.java))
                        activity.finish()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}