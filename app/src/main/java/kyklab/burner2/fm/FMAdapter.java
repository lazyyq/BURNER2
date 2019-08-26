package kyklab.burner2.fm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import kyklab.burner2.R;

public class FMAdapter extends RecyclerView.Adapter<FMAdapter.ViewHolder> {
    private final Context pContext;
    private final List<FileItem> mFileList;

    public FMAdapter(Context pContext, List<FileItem> mFileList) {
        this.pContext = pContext;
        this.mFileList = mFileList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileItem fileItem = mFileList.get(position);
        if (fileItem.isDirectory()) {
            Glide.with(pContext).load(fileItem.getIcon()).into(holder.fileIconView);
        } else {
            Glide.with(pContext).load(fileItem.getIcon()).centerCrop().into(holder.fileIconView);
        }
        holder.fileNameView.setText(fileItem.getName());
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView fileIconView;
        private final TextView fileNameView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileIconView = itemView.findViewById(R.id.fileIconView);
            fileNameView = itemView.findViewById(R.id.fileNameView);
        }
    }
}
