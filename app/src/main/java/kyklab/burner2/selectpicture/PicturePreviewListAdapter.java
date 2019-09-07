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
import com.bumptech.glide.signature.ObjectKey;

import java.util.List;

import kyklab.burner2.R;
import kyklab.burner2.picture.PictureItem;
import kyklab.burner2.utils.PrefManager;

public class PicturePreviewListAdapter extends RecyclerView.Adapter<PicturePreviewListAdapter.ViewHolder> {
    private final Context pContext;
    private final List<PictureItem> mPictureList;
    private int mCheckedPosition;

    public PicturePreviewListAdapter(Context pContext, List<PictureItem> mPictureList) {
        this.pContext = pContext;
        this.mPictureList = mPictureList;
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
        PictureItem pictureItem = mPictureList.get(position);
        Object thumbnail = pictureItem.getThumbnail() != null
                ? pictureItem.getThumbnail() : pictureItem.getPicture();
        ObjectKey key = new ObjectKey(pictureItem.getVersionMetadata());
        Glide.with(pContext)
                .load(thumbnail)
                .signature(key)
                .into(holder.picturePreview);
        if (mCheckedPosition == position) {
            holder.picturePreviewChecked.setVisibility(View.VISIBLE);
        } else {
            holder.picturePreviewChecked.setVisibility(View.GONE);
        }
        holder.pictureText.setText(pictureItem.getName());
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
        return mPictureList.size();
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PrefManager.getInstance().setSelectedPictureIndex(getAdapterPosition());
                    PicturePreviewListAdapter.this.notifyItemChanged(mCheckedPosition, false);
                    PicturePreviewListAdapter.this.notifyItemChanged(getAdapterPosition(), true);
                    mCheckedPosition = getAdapterPosition();
                }
            });
        }
    }
}
