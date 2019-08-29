package kyklab.burner2.selectpicture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import kyklab.burner2.R;
import kyklab.burner2.utils.PrefManager;

public class PicturePreviewListAdapter extends RecyclerView.Adapter<PicturePreviewListAdapter.ViewHolder> {
    private final Context pContext;
    private final List<PictureItem> mThumbnailList;
    private int mCheckedPosition;

    public PicturePreviewListAdapter(Context pContext, List<PictureItem> mThumbnailList) {
        this.pContext = pContext;
        this.mThumbnailList = mThumbnailList;
        this.mCheckedPosition = PrefManager.getInstance().getSelectedPictureIndex();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_picture_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PictureItem picture = mThumbnailList.get(position);
        Glide.with(pContext).load(picture.getPicture())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.picturePreview);
        if (mCheckedPosition == position) {
            holder.picturePreviewChecked.setVisibility(View.VISIBLE);
        } else {
            holder.picturePreviewChecked.setVisibility(View.GONE);
        }
        holder.pictureText.setText(picture.getName());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            for (Object payload : payloads) {
                if (payload instanceof Boolean) {
                    holder.picturePreviewChecked.setVisibility(
                            (boolean) payload ? View.VISIBLE : View.GONE
                    );
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mThumbnailList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picturePreview;
        private final ImageView picturePreviewChecked;
        private final TextView pictureText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            picturePreview = itemView.findViewById(R.id.picturePreview);
            picturePreviewChecked = itemView.findViewById(R.id.picturePreviewChecked);
            pictureText = itemView.findViewById(R.id.pictureText);

            picturePreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PrefManager.getInstance().setSelectedPictureIndex(getAdapterPosition());
                    PrefManager.getInstance().setUseCustomPicture(false);
                    PicturePreviewListAdapter.this.notifyItemChanged(mCheckedPosition, false);
                    PicturePreviewListAdapter.this.notifyItemChanged(getAdapterPosition(), true);
                    mCheckedPosition = getAdapterPosition();
                }
            });
        }
    }
}
