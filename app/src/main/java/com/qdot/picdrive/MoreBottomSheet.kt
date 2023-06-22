package com.qdot.picdrive

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.StorageReference
import com.qdot.picdrive.databinding.MoreLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MoreBottomSheet(private val storageReference: StorageReference, private val fragmentManager: FragmentManager) : BottomSheetDialogFragment() {
    private var _binding: MoreLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MoreLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.view_item -> {
                    storageReference.downloadUrl.addOnCompleteListener {
                        ImageViewBottomSheet(it.result).show(fragmentManager,"IMG")
                        dismiss()
                    }
                    return@setNavigationItemSelectedListener false
                }
                R.id.share_item -> {
                    val tempFile = File(requireContext().cacheDir,storageReference.name+".png")
                    storageReference.getFile(tempFile).
                        addOnSuccessListener {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(tempFile))
                            intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image")
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                            intent.type = "image/png"
                            requireContext().startActivity(Intent.createChooser(intent, "Share Via"))
                        }
                    return@setNavigationItemSelectedListener false
                }
                else -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        storageReference.delete()
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(),
                                "File deleted",
                                Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                    }
                    return@setNavigationItemSelectedListener false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}