package com.example.searchvideo

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.searchvideo.util.PicassoTransformations
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_main.view.*

class MainAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    data class VideoItem(var VideoUrl:String , var documentUrl:String)

    class Videoholder(parent: ViewGroup):RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
    ) {
        fun onBind(item:VideoItem){
            itemView.run {
                Picasso.with(context).load(item.VideoUrl).placeholder(R.drawable.ic_image_black_24dp).resize(800,700).into(video_view)
                video_view.setOnClickListener {
                    ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(item.documentUrl)), null)
                }
            }
        }
    }

    private val videoItemList = ArrayList<VideoItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Videoholder(parent)

    override fun getItemCount() = videoItemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? Videoholder)?.onBind(videoItemList[position])
    }

    fun addVideoItem(VideoUrl: String, documentUrl: String){
        videoItemList.add(VideoItem(VideoUrl, documentUrl))
    }


}