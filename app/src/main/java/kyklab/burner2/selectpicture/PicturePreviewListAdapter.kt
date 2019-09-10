package kyklab.burner2.selectpicture

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey

import kyklab.burner2.R
import kyklab.burner2.picture.PictureItem
import kyklab.burner2.utils.PrefManager

class PicturePreviewListAdapter(private val pContext: Context, private val mPictureList: List<PictureItem<*, *>>) : RecyclerView.Adapter<PicturePreviewListAdapter.ViewHolder>() {
    private var mCheckedPosition: Int = 0

    init {
        this.mCheckedPosition = PrefManager.instance.selectedPictureIndex
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pictureItem = mPictureList[position]
        val thumbnail = if (pictureItem.thumbnail != null)
            pictureItem.thumbnail
        else
            pictureItem.picture
        val key = ObjectKey(pictureItem.versionMetadata)
        Glide.with(pContext)
                .load(thumbnail)
                .signature(key)
                .into(holder.picturePreview)
        if (mCheckedPosition == position) {
            holder.picturePreviewChecked.visibility = View.VISIBLE
        } else {
            holder.picturePreviewChecked.visibility = View.GONE
        }
        holder.pictureText.setText(pictureItem.name)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int,
                                  payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            for (payload in payloads) {
                if (payload is Boolean) {
                    holder.picturePreviewChecked.visibility = if (payload) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mPictureList.size
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val picturePreview: ImageView
        private val picturePreviewChecked: ImageView
        private val pictureText: TextView

        init {
            picturePreview = itemView.findViewById(R.id.picturePreview)
            picturePreviewChecked = itemView.findViewById(R.id.picturePreviewChecked)
            pictureText = itemView.findViewById(R.id.pictureText)

            itemView.setOnClickListener {
                PrefManager.instance.selectedPictureIndex = adapterPosition
                this@PicturePreviewListAdapter.notifyItemChanged(mCheckedPosition, false)
                this@PicturePreviewListAdapter.notifyItemChanged(adapterPosition, true)
                mCheckedPosition = adapterPosition
            }
        }
    }
}
