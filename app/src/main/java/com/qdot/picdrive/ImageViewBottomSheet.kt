package com.qdot.picdrive

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.qdot.picdrive.databinding.ImageViewZoomLayoutBinding

class ImageViewBottomSheet(private val imgUrl : Uri) : BottomSheetDialogFragment() {
    private var _binding: ImageViewZoomLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ImageViewZoomLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scaleImage.load(imgUrl){
            placeholder(R.drawable.ai_bg)
            error(R.drawable.ai_bg)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}