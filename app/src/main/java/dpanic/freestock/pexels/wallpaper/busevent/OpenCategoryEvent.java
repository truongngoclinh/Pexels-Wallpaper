package dpanic.freestock.pexels.wallpaper.busevent;

import dpanic.freestock.pexels.wallpaper.data.model.Category;

/**
 * Created by dpanic on 10/12/2016.
 * Project: Pexels
 */

public class OpenCategoryEvent {
    private Category category;
    private boolean isColor;

    public OpenCategoryEvent(Category category, boolean isColor) {
        this.category = category;
        this.isColor = isColor;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isColor() {
        return isColor;
    }
}
