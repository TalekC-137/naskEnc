package com.example.naskenc.fetcher

import com.example.naskenc.R
import com.example.naskenc.models.CryptedModel
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.row_note.view.*

class CryptedNotesRow(val note: CryptedModel): Item<GroupieViewHolder>(){


     override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.tv_title.text = note.title
         viewHolder.itemView.tv_note.text = note.note

     }

     override fun getLayout(): Int {
         return R.layout.row_note
     }

 }