package com.qdot.picdrive

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.qdot.picdrive.databinding.ActivityAuthBinding
import id.passage.android.Passage
import id.passage.android.exceptions.PassageException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var passage: Passage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        passage = Passage(this)
        binding.toolBar.setNavigationOnClickListener {
            finish()
        }
        binding.loginBtn.setOnClickListener {
            if (binding.emailText.text.isNullOrBlank()){
                Snackbar.make(binding.root,
                "Please enter email",
                Snackbar.LENGTH_SHORT).show()
            }else{
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        passage.loginWithPasskey(binding.emailText.text.toString().trim()).apply {
                            startActivity(Intent(this@AuthActivity,MainActivity::class.java))
                            finish()
                        }
                    }catch (e: PassageException){
                        withContext(Dispatchers.Main) {
                            Snackbar.make(
                                binding.root,
                                e.message.toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        binding.accBtn.setOnClickListener {
            if (binding.emailText.text.isNullOrBlank()){
                Snackbar.make(binding.root,
                    "Please enter email",
                    Snackbar.LENGTH_SHORT).show()
            }else{
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        passage.registerWithPasskey(binding.emailText.text.toString().trim()).apply {
                            startActivity(Intent(this@AuthActivity,MainActivity::class.java))
                            finish()
                        }
                    }catch (e: PassageException){
                        withContext(Dispatchers.Main) {
                            Snackbar.make(
                                binding.root,
                                e.message.toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}