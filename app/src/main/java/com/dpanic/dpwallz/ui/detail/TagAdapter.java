package com.dpanic.dpwallz.ui.detail;

import java.util.ArrayList;
import javax.inject.Inject;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dpanic.dpwallz.R;
import com.dpanic.dpwallz.busevent.OpenCategoryEvent;
import com.dpanic.dpwallz.data.model.Category;
import com.dpanic.dpwallz.utils.TextUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dpanic on 10/7/2016.
 * Project: DPWallz
 */

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagVH> {

    private Context mContext;
    private ArrayList<String> tagList;

    public TagAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(ArrayList<String> tagList) {
        this.tagList = tagList;
    }

    @Override
    public TagVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        return new TagVH(inflater.inflate(R.layout.tag_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(TagVH holder, int position) {
        holder.itemText.setText(tagList.get(position));
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    class TagVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tag_item_text)
        TextView itemText;

        TagVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.tag_item_container)
        void OnClick() {
            Category category = new Category(tagList.get(getAdapterPosition()), TextUtil.getSearchLink(tagList.get(getAdapterPosition())), "");
            EventBus.getDefault().post(new OpenCategoryEvent(category, false));
        }
    }

}
