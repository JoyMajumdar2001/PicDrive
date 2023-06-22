package com.qdot.picdrive

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.nareshchocha.filepickerlibrary.models.ImageOnly
import com.nareshchocha.filepickerlibrary.models.PickMediaConfig
import com.nareshchocha.filepickerlibrary.ui.FilePicker
import com.qdot.picdrive.databinding.ActivityMainBinding
import id.passage.android.Passage
import id.passage.android.PassageUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var passage: Passage
    private lateinit var storage : FirebaseStorage
    private lateinit var uid : String
    private lateinit var progressViewModel: ProgressViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        passage = Passage(this)
        storage = Firebase.storage
        binding.toolBar.setNavigationOnClickListener {
            finish()
        }
        progressViewModel = ViewModelProvider(this)[ProgressViewModel::class.java]
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = passage.getCurrentUser()
            if (currentUser != null) {
                withContext(Dispatchers.Main) {
                    setUpUI(currentUser)
                    uid = currentUser.id.toString()
                }
            } else {
                startActivity(Intent(this@MainActivity,AuthActivity::class.java))
                finish()
            }
        }
    }

    private fun setUpUI(currentUser: PassageUser) {
        binding.mainRv.layoutManager = GridLayoutManager(this,2)
        CoroutineScope(Dispatchers.IO).launch {
            storage.reference.child(currentUser.id.toString()).listAll()
                .addOnSuccessListener {
                    val adapter = ImageAdapter(it.items,supportFragmentManager)
                    binding.mainRv.adapter = adapter
                }
        }
        binding.toolBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.profile_item -> {
                    UserInfoSheet(currentUser,passage,this).show(supportFragmentManager,"FRAG")
                    return@setOnMenuItemClickListener false
                }

                else -> {
                    Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show()
                    return@setOnMenuItemClickListener false
                }
            }
        }

        binding.pickImgBtn.setOnClickListener {
            imgLauncher.launch(
                FilePicker.Builder(this)
                    .addPickMedia(
                        PickMediaConfig(
                            popUpIcon = R.drawable.upload_ic,
                            popUpText = "Image",
                            allowMultiple = false,
                            maxFiles = 0,
                            mPickMediaType = ImageOnly,
                            askPermissionTitle = null,
                            askPermissionMessage = null,
                            settingPermissionTitle = null,
                            settingPermissionMessage = null,
                        ),
                    )
                    .build()
            )
        }
    }

    private val imgLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                val uri = res.data?.data!!
                val imgRef = storage.reference.child(uid+"/"+uri.lastPathSegment)
                progressViewModel.setDoneOrNot(false)
                imgRef.putFile(uri).addOnFailureListener{
                    Toast.makeText(this@MainActivity,it.message,Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener {
                    Toast.makeText(this@MainActivity,"File uploaded",Toast.LENGTH_SHORT).show()
                    progressViewModel.setDoneOrNot(true)
                }.addOnProgressListener {
                    progressViewModel.setDoneOrNot(false)
                    progressViewModel.setProgress((it.bytesTransferred/it.totalByteCount)*100)
                }
                FileUploadSheet(res.data?.data!!).show(supportFragmentManager,"FRAG_FILE")
            }
        }
}