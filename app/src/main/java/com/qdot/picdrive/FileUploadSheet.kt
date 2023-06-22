package com.qdot.picdrive

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.qdot.picdrive.databinding.FileUploadSheetLayoutBinding

class FileUploadSheet(private val uri: Uri): BottomSheetDialogFragment() {
    private var _binding: FileUploadSheetLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressViewModel: ProgressViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FileUploadSheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.indicatorBar.max = 100
        progressViewModel = ViewModelProvider(requireActivity())[ProgressViewModel::class.java]
        binding.imageView.load(uri)
        binding.fileNameText.text = uri.lastPathSegment
        progressViewModel.progressCount.observe(this){
            binding.indicatorBar.progress = it.toInt()
        }
        progressViewModel.done.observe(this){
            if (it){
                dismiss()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}