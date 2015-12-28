package com.backdoor.moove.core.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backdoor.moove.R;
import com.backdoor.moove.core.interfaces.SimpleListener;
import com.backdoor.moove.core.utils.AssetsUtil;

import java.util.ArrayList;

/**
 * Recycler view adapter for frequently used places.
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private ArrayList<String> array = new ArrayList<>();

    /**
     * Font typeface for text view's.
     */
    private Typeface typeface;

    /**
     * Action listener for adapter.
     */
    private SimpleListener mEventListener;

    /**
     * Adapter constructor.
     * @param context application context.
     * @param array places data provider.
     */
    public PlaceAdapter(final Context context, ArrayList<String> array) {
        this.array = array;
        typeface = AssetsUtil.getLightTypeface(context);
        setHasStableIds(true);
    }

    /**
     * View holder for adapter.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        /**
         * Place title.
         */
        public TextView textView;

        /**
         * View holder constructor.
         * @param v view.
         */
        public ViewHolder(final View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.text1);
            textView.setTypeface(typeface);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            if (mEventListener != null) {
                mEventListener.onItemClicked(getAdapterPosition(), textView);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_taxt_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(array.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return 0;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    /**
     * Get current action listener.
     * @return Action listener.
     */
    public SimpleListener getEventListener() {
        return mEventListener;
    }

    /**
     * Set action listener for adapter.
     * @param eventListener action listener.
     */
    public void setEventListener(final SimpleListener eventListener) {
        mEventListener = eventListener;
    }
}
