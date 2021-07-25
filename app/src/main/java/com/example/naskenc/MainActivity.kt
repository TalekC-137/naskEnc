package com.example.naskenc

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
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
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    val TAG = "fileEncrypter"

    val adapter = GroupieAdapter()

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SQLiteDatabase.loadLibs(this);

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

    }

    private fun createFile(){
        val FILE_NAME = "some_file_name_placeHolder.txt"
        val text = "text_placeHolder"
        val tmpDir: File = File(FILE_NAME)


        var fos: FileOutputStream? = null

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE)

         //   fos.write(text.toByteArray())
            Toast.makeText(
                this, "Saved to $filesDir/$FILE_NAME",
                Toast.LENGTH_LONG
            ).show()


        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }


    }

    fun getDatabase(){
        val databaseHelper:DatabaseHelper = DatabaseHelper(this)
        if(databaseHelper.viewEmployee().isNotEmpty()) {
            val noteList: ArrayList<CryptedModel> = databaseHelper.viewEmployee()
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

            val status = databaseHelper.addRow(CryptedModel(0, et_title.text.toString(), et_message.text.toString()))
            if (status > -1) {
                Toast.makeText(applicationContext, "note saved", Toast.LENGTH_LONG).show()
                et_message.text.clear()
                et_title.text.clear()
            }
        }else{
            Toast.makeText(this, "uzupełnij oba pola", Toast.LENGTH_SHORT).show()
        }

    }

}



