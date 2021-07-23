package com.example.naskenc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.example.naskenc.fetcher.CryptedNotesRow
import com.example.naskenc.models.CryptedModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import net.sqlcipher.database.SQLiteDatabase

class MainActivity : AppCompatActivity() {

    val TAG = "fileEncrypter"

    val adapter = GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SQLiteDatabase.loadLibs(this);

        rv_messages.adapter = adapter

        btn_encrypt.setOnClickListener {

            addtoDatabase()
        }
        btn_decrypt.setOnClickListener {

            getDatabase()

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
            }
        }else{
            Toast.makeText(this, "uzupełnij oba pola", Toast.LENGTH_SHORT).show()
        }

    }

}



