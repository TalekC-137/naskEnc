package com.example.naskenc

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.naskenc.fetcher.CryptedNotesRow
import com.example.naskenc.models.CryptedModel
import com.xwray.groupie.GroupieAdapter
import kotlinx.android.synthetic.main.activity_main.*
import net.sqlcipher.database.SQLiteDatabase
import java.io.*
import java.security.AccessController.getContext
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    val adapter = GroupieAdapter()

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    var TAG = "encryption_debug"
    var KEY = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generateAndReadAES()
        SQLiteDatabase.loadLibs(this)
        generateAndReadAES()
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    getDatabase()  //decrypts and shows the the database
                    Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("cancel")
            .build()


        rv_messages.adapter = adapter

        btn_encrypt.setOnClickListener {
            addtoDatabase()
            val imm: InputMethodManager =
                baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(et_message.getWindowToken(), 0)

        }
        btn_decrypt.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
            val imm: InputMethodManager =
                baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(et_message.getWindowToken(), 0)

        }

        btn_test.setOnClickListener {
            val i = Intent(this, BlueActivity::class.java)
            startActivity(i)
        }

    }


    fun getDatabase(){
        val databaseHelper:DatabaseHelper = DatabaseHelper(this)
        if(databaseHelper.viewEmployee(KEY).isNotEmpty()) {
            val noteList: ArrayList<CryptedModel> = databaseHelper.viewEmployee(KEY)
            if(adapter.itemCount ==0){
            noteList.forEach {
                adapter.add(CryptedNotesRow(it))
                }
            }else{
                adapter.clear()
                getDatabase()
            }
        }else{
            Toast.makeText(this, "the database is empty", Toast.LENGTH_LONG).show()
        }


    }

    fun addtoDatabase(){

        val databaseHelper:DatabaseHelper = DatabaseHelper(this)
        if(et_title.text.isNotEmpty() && et_message.text.isNotEmpty()){

            val status = databaseHelper.addRow(CryptedModel(0, et_title.text.toString(), et_message.text.toString()), KEY)
            if (status > -1) {
                Toast.makeText(applicationContext, "note saved", Toast.LENGTH_LONG).show()
                et_message.text.clear()
                et_title.text.clear()
            }
        }else{
            Toast.makeText(this, "uzupełnij oba pola", Toast.LENGTH_SHORT).show()
        }

    }


    fun generateAndReadAES(){
        var sharedPref = this?.getPreferences(Context.MODE_PRIVATE)

        if(sharedPref.getString(getString(R.string.shared_key), "pusta") == "pusta"){
            //first app lauch - generate EAS key

                val EASkey = getRandomString(6)

            sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putString(getString(R.string.shared_key), EASkey)
                apply()
            }
            Log.d(TAG, "the app is launched for the first time and the key has been generated $EASkey")
        }else{
            //not the first time launching the app and the key is already generated

            KEY = sharedPref.getString(getString(R.string.shared_key), "pusta").toString()
            Log.d(TAG, "the app is launched with generated key $KEY")
        }
    }

    fun getRandomString(length: Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789!@#$%^&*()"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

}



