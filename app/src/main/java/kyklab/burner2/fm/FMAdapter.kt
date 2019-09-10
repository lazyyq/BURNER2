package kyklab.burner2.fm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kyklab.burner2.R
import java.io.File

class FMAdapter(private val pContext: Context, private val mFileList: List<File>) : RecyclerView.Adapter<FMAdapter.ViewHolder>() {
    private val mCallback: FMAdapterCallback

    init {
        this.mCallback = pContext as FMAdapterCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = mFileList[position]
        if (file.isDirectory) {
            Glide.with(pContext).clear(holder.fileIconView)
            holder.fileIconView.setImageResource(R.drawable.ic_folder_36dp)
        } else {
            Glide.with(pContext).load(file).centerCrop()
                    .into(holder.fileIconView)
        }
        holder.fileNameView.text = file.name
    }

    override fun getItemCount(): Int {
        return mFileList.size
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileIconView: ImageView
        private val fileNameView: TextView

        init {
            fileIconView = itemView.findViewById(R.id.fileIconView)
            fileNameView = itemView.findViewById(R.id.fileNameView)

            itemView.setOnClickListener {
                val file = mFileList[adapterPosition]
                if (file.isDirectory) {
                    mCallback.enterDirectory(file.name)
                } else {
                    mCallback.selectAsCustomPicture(file.absolutePath)
                }
            }
        }
    }
}
