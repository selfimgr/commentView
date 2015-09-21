package com.chaowen.commentlibrary.emoji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.chaowen.commentlibrary.R;

import java.util.ArrayList;
import java.util.List;


public class EmojiViewPagerAdapter extends RecyclingPagerAdapter {

    private LayoutInflater infalter;
    private int mEmojiHeight;
    private List<List<Emojicon>> mPagers = new ArrayList<List<Emojicon>>();
    private OnClickEmojiListener mListener;
    private int mChildCount = 0;

    public EmojiViewPagerAdapter(Context context, List<List<Emojicon>> pager,
                                 int emojiHeight, OnClickEmojiListener listener) {
        infalter = LayoutInflater.from(context);
        mPagers = pager;
        mEmojiHeight = emojiHeight;
        mListener = listener;
    }

    public void setEmojiHeight(int emojiHeight) {
        mEmojiHeight = emojiHeight;
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.view_pager_emoji, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final EmojiAdapter adapter = new EmojiAdapter(container.getContext(),
                mPagers.get(position), mEmojiHeight);
        vh.gv.setAdapter(adapter);
        vh.gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mListener == null)
                    return;
                if (position == adapter.getCount() - 1) {
                    mListener.onDelete();
                } else {
                    mListener.onEmojiClick((Emojicon) adapter.getItem(position));
                }
            }
        });
        adapter.notifyDataSetChanged();
        return convertView;
    }

    @Override
    public int getCount() {
        return mPagers.size();
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    public interface OnClickEmojiListener {
        void onEmojiClick(Emojicon emoji);

        void onDelete();
    }

    static class ViewHolder {
        GridView gv;

        public ViewHolder(View v) {
            gv = (GridView) v.findViewById(R.id.gv_emoji);
        }
    }
}
