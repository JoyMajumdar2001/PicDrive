package com.qdot.picdrive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.storage.StorageReference
import com.qdot.picdrive.databinding.ImgLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageAdapter(private var imgList: List<StorageReference>, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ImgLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ImgLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imgList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(imgList[position]){
                CoroutineScope(Dispatchers.IO).launch {
                    this@with.downloadUrl.addOnCompleteListener{
                        if (it.isSuccessful){
                            binding.imgView.load(it.result){
                                crossfade(true)
                                error(R.drawable.ai_bg)
                                placeholder(R.drawable.ai_bg)
                            }
                        }
                    }
                }
                binding.moreBtn.setOnClickListener {
                    MoreBottomSheet(this,fragmentManager).show(fragmentManager,"FRAG_MORE")
                }
                binding.imgView.setOnClickListener {
                    this.downloadUrl.addOnCompleteListener {
                        ImageViewBottomSheet(it.result).show(fragmentManager,"IMG")
                    }
                }
            }
        }
    }
}