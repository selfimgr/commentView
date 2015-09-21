package com.chaowen.commentlibrary.recoder;

import android.content.Context;
import android.os.Environment;
import com.chaowen.commentlibrary.R;
import java.util.Formatter;
import java.util.Locale;

public class RecordUtils {
    public final static String BASE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/comment/";
    public static final String RECORD_DIR = BASE_DIR + "recorders";

    private static final Object[] sTimeArgs = new Object[5];
    /*
     * Try to use String.format() as little as possible, because it creates a
     * new Formatter every time you call it, which is very inefficient. Reusing
     * an existing Formatter more than tripled the speed of makeTimeString().
     * This Formatter/StringBuilder are also used by makeAlbumSongsLabel()
     */
    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder,
            Locale.getDefault());

    public static String makeTimeString(Context context, long secs) {
        String durationformat = context
                .getString(secs < 3600 ? R.string.durationformatshort
                        : R.string.durationformatlong);

		/*
         * Provide multiple arguments so the format can be changed easily by
		 * modifying the xml.
		 */
        sFormatBuilder.setLength(0);

        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = secs / 3600;
        timeArgs[1] = secs / 60;
        timeArgs[2] = (secs / 60) % 60;
        timeArgs[3] = secs;
        timeArgs[4] = secs % 60;

        return sFormatter.format(durationformat, timeArgs).toString();
    }

    public static String getNewRecordFile() {
        return RECORD_DIR + "/" + System.currentTimeMillis() + ".samr";
    }
}
