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

import java.io.File;
import java.util.List;

import kyklab.burner2.R;

public class FMAdapter extends RecyclerView.Adapter<FMAdapter.ViewHolder> {
    private final Context pContext;
    private final FMAdapterCallback mCallback;
    private final List<File> mFileList;

    public FMAdapter(Context pContext, List<File> mFileList) {
        this.pContext = pContext;
        this.mFileList = mFileList;
        this.mCallback = (FMAdapterCallback) pContext;
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
        File file = mFileList.get(position);
        if (file.isDirectory()) {
            Glide.with(pContext).clear(holder.fileIconView);
            holder.fileIconView.setImageResource(R.drawable.ic_folder_36dp);
        } else {
            Glide.with(pContext).load(file).centerCrop()
                    .into(holder.fileIconView);
        }
        holder.fileNameView.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView fileIconView;
        private final TextView fileNameView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileIconView = itemView.findViewById(R.id.fileIconView);
            fileNameView = itemView.findViewById(R.id.fileNameView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = mFileList.get(getAdapterPosition());
                    if (file.isDirectory()) {
                        mCallback.enterDirectory(file.getName());
                    } else {
                        mCallback.customPictureSelected(file.getAbsolutePath());
                    }
                }
            });
        }
    }
}
