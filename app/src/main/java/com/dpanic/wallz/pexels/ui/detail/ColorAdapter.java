package com.dpanic.wallz.pexels.ui.detail;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.dpanic.wallz.pexels.R;
import com.dpanic.wallz.pexels.busevent.OpenCategoryEvent;
import com.dpanic.wallz.pexels.data.model.Category;
import com.dpanic.wallz.pexels.utils.TextUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dpanic on 10/7/2016.
 * Project: DPWallz
 */

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorVH> {

    private Context mContext;
    private EventBus eventBus;
    private ArrayList<String> colorList;

    public ColorAdapter(Context context, EventBus eventBus) {
        this.mContext = context;
        this.eventBus = eventBus;
    }

    public void setData(ArrayList<String> colorList) {
        this.colorList = colorList;
    }

    @Override
    public ColorVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ColorVH(inflater.inflate(R.layout.color_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ColorVH holder, int position) {
        holder.ivColorItem.setColorFilter(Color.parseColor(colorList.get(position)), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    class ColorVH extends RecyclerView.ViewHolder {

        @BindView(R.id.color_item)
        ImageView ivColorItem;

        ColorVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.color_item)
        void onClick() {
            String colorSearch = mContext.getResources().getString(R.string.string_color_search_prefix_text) + colorList.get(getAdapterPosition());
            Category category = new Category(colorSearch, TextUtil.getSearchLink(colorSearch), "");
            eventBus.post(new OpenCategoryEvent(category, true));
        }
    }
}
