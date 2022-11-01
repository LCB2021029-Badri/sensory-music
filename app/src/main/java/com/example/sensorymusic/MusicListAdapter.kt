package com.example.sensorymusic

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sensorymusic.databinding.RecyclerItemBinding

class MusicListAdapter(val songList: ArrayList<AudioModel>,var context: Context):RecyclerView.Adapter<MusicListAdapter.ItemViewHolder>() {

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

        //to highlight the music being played on the recycler list
        if(MyMediaPalyer.currentIndex == position){
            holder.itemView.apply {
                titleTextView!!.setTextColor(Color.parseColor("#F3DF26"))
            }
        }
        else{
            holder.itemView.apply {
                titleTextView!!.setTextColor(Color.parseColor("#1B9CA1"))
            }

        }

        holder.itemView.apply {
            titleTextView!!.setText(songData.title)
        }

        //to enable the playing of music when we click the song
        holder.itemView.setOnClickListener {
            //navigate to another activity

            //first we reset the media player i.e.even if its playing already we stop it
            MyMediaPalyer.getInstance()?.reset()
            //assigning the index after reset
            MyMediaPalyer.currentIndex = position
            //then we pass it to next intent/activity with few attributes
            val intent:Intent = Intent(context,MusicPlayerActivity::class.java)
            intent.putExtra("LIST",songList)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return songList.size
    }
}