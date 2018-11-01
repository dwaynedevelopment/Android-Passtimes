package com.dwaynedevelopment.passtimes.utils;

import android.support.v7.widget.RecyclerView;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_INSERTED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_MODIFIED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_REMOVED_DATA;

public class AdapterUtils {

    public static <T> void adapterViewStatus(T adapterView, int dataStatus, int insertedIndex) {
        if (adapterView != null) {
            if (adapterView instanceof RecyclerView.Adapter) {
                switch (dataStatus) {
                    case NOTIFY_INSERTED_DATA:
                        ((RecyclerView.Adapter) adapterView).notifyItemInserted(insertedIndex);
                        break;
                    case NOTIFY_MODIFIED_DATA:
                        ((RecyclerView.Adapter) adapterView).notifyItemChanged(insertedIndex);
                        break;
                    case NOTIFY_REMOVED_DATA:
                        ((RecyclerView.Adapter) adapterView).notifyItemRemoved(insertedIndex);
                        break;
                }
                ((RecyclerView.Adapter) adapterView).notifyDataSetChanged();
            }
        }
    }
}
