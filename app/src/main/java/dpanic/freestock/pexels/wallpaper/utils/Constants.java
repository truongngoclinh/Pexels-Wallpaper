package dpanic.freestock.pexels.wallpaper.utils;

/**
 * Created by dpanic on 9/30/2016.
 * Project: Pexels
 */

public class Constants {

    public static final String IMAGE_INSTANCE = "image_instance";
    public static final String CATEGORY_INSTANCE = "category_instance";

    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final String MAIN_LINK = "main_link";

    public static final int FRAG_TYPE_RECENT = 0;
    public static final int FRAG_TYPE_MONTH_POPULAR = 1;
    public static final int FRAG_TYPE_ALL_TIME_POPULAR = 2;
    public static final int FRAG_TYPE_SEARCH = 3;

    public static final String RECENT_IMAGE = "recent_image";
    public static final String MONTH_POPULAR_IMAGE = "month_popular_image";
    public static final String ALL_TIME_POPULAR_IMAGE = "all_time_image";
    public static final String SEARCH_IMAGE = "search_image";

    public static final String MAIN_LINK_RECENT = "https://www.pexels.com/";
    public static final String MAIN_LINK_MONTH_POPULAR = "https://www.pexels.com/popular-photos/";
    public static final String MAIN_LINK_ALL_TIME_POPULAR = "https://www.pexels.com/popular-photos/all-time/";

    static final String ROOT_LINK = "https://www.pexels.com";
    static final String CATEGORY_LINK = "https://www.pexels.com/popular-searches/";
    static final String SEARCH_PREFIX_LINK = "https://www.pexels.com/search/";

    public static final String IS_FAVORITE_FRAGMENT = "is_favorite_fragment";
    public static final int AD_PER_IMAGES = 14;

    public static final int DOWNLOAD_FOR_DOWNLOAD = 1;
    public static final int DOWNLOAD_FOR_SET_AS = 2;
    public static final int DOWNLOAD_FOR_SHARE = 3;
    public static final int DOWNLOAD_FOR_PREVIEW = 4;
    public static final String IS_SHOW_ADS = "is_show_ads";
    public static final String IS_COLOR_SEARCH = "is_color_search";

    public static final String FCM_DETAIL_LINK = "image";
    public static final String FCM_MESSAGE = "message";

    public static final String[] VIOLENT_TAGS1 = {"se", "se", "nu", "nu"};
    public static final String[] VIOLENT_TAGS2 = {"x", "xual", "de", "dity"};
    public static final String CATEGORY_EXCLUDE1 = "se";
    public static final String CATEGORY_EXCLUDE2 = "xy";

    public static final long PROGRESS_NORMAL_UPDATE_INTERVAL = 50;
    public static final long PROGRESS_FAST_UPDATE_INTERVAL = 2;
    public static final long PROGRESS_FINISH_DELAY = 900;
    public static final int PROGRESS_UPDATE = 0;
    public static final int PROGRESS_FINISH = 1;
    public static final int PROGRESS_CANCEL = 2;
}
