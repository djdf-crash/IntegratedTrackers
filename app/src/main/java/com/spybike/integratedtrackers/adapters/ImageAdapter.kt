package com.spybike.integratedtrackers.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.spybike.integratedtrackers.R
import com.spybike.integratedtrackers.enums.TagsFragment
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.utils.AppConstants
import com.spybike.integratedtrackers.utils.Connections
import com.spybike.integratedtrackers.utils.Connections.context
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.synthetic.main.image_item.view.*

class ImageAdapter(var filter: FilterModel?) : RecyclerView.Adapter<ViewHolder>() {

    val items = listOf(TagsFragment.VELOCITY, TagsFragment.GSM, TagsFragment.BATTERY)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setNewFilter(f: FilterModel?){
        filter = f
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var url = ""
        when(items[position]){
            TagsFragment.BATTERY ->
                url = AppConstants.BATTERY_URL
            TagsFragment.GSM ->
                url = AppConstants.GSM_URL
            TagsFragment.VELOCITY ->
                url = AppConstants.VELOCITY_URL
        }

        if (filter != null) {
            Picasso.get()
                .load(url + Connections.getParametersURL(filter!!))
                .error(R.drawable.ic_error)
                .into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        holder.imageView.visibility = View.GONE
                        holder.progressBar.visibility = View.VISIBLE
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        holder.progressBar.visibility = View.GONE
                        holder.imageView.setImageDrawable(errorDrawable)
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        holder.imageView.visibility = View.VISIBLE
                        holder.progressBar.visibility = View.GONE
                        holder.imageView.setImageBitmap(bitmap)
                    }

                })
            holder.imageView.setOnClickListener {
                ImageViewer.Builder(context, listOf(url + Connections.getParametersURL(filter!!))).show()
            }
        }else{
            holder.imageView.setImageResource(R.drawable.ic_error)
        }
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    val imageView: ImageView = view.imageSource
    val progressBar: ProgressBar = view.progressBar
}