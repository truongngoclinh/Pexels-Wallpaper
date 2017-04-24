package dpanic.freestock.pexels.wallpaper.busevent;

import dpanic.freestock.pexels.wallpaper.data.model.Image;

/**
 * Created by dpanic on 9/30/2016.
 * Project: Pexels
 */

public class OpenImageEvent {
    private Image img;

    public OpenImageEvent(Image img) {
        this.img = img;
    }

    public Image getImage() {
        return img;
    }
}
