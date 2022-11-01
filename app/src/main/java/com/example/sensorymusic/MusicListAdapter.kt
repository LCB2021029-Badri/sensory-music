package com.example.sensorymusic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sensorymusic.databinding.RecyclerItemBinding

class MusicListAdapter(val songList: ArrayList<AudioModel>,context: Context):RecyclerView.Adapter<MusicListAdapter.ItemViewHolder>() {

    private var binding: RecyclerItemBinding? = null
    inner class ItemViewHolder(itemBinding: RecyclerItemBinding): RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var songData: AudioModel = songList.get(position)
        var titleTextView = binding?.musicTitleText
        var iconImageView = binding?.iconView
        holder.itemView.apply {
            titleTextView!!.setText(songData.title)
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

}