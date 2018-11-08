package com.dwaynedevelopment.passtimes.base.account.edit.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Sport;

import java.util.ArrayList;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_SELECT_SELECTED;

public class SelectedViewAdapter extends RecyclerView.Adapter<SelectedViewAdapter.ViewHolder> {

    private final ArrayList<Sport> favoriteSports;
    private final AppCompatActivity context;

    private Sport oldFavoriteSport = null;
    private View oldSelectedView;
    private ImageView oldImageViewSelected;
    private TextView oldTextViewSelected;

    public SelectedViewAdapter(AppCompatActivity context, ArrayList<Sport> favoriteSports) {
        this.favoriteSports = favoriteSports;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_selected, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Sport favoriteSport = favoriteSports.get(position);
        Glide.with(context).load(favoriteSport.getIdle()).into(holder.ivIcon);
        holder.tvCategory.setText(favoriteSport.getCategory());

        holder.button.setOnClickListener(v -> {

            if (oldSelectedView != null) {
                Glide.with(context).load(oldFavoriteSport.getIdle()).into(oldImageViewSelected);
                oldSelectedView.setSelected(false);
                oldTextViewSelected.setTextColor(context.getResources().getColor(R.color.colorDarkPrimary));
            }

            Glide.with(context).load(favoriteSport.getActive()).into(holder.ivIcon);
            v.setSelected(true);
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.colorSecondaryAccent));

            oldSelectedView = v;
            oldImageViewSelected = holder.ivIcon;
            oldTextViewSelected = holder.tvCategory;
            oldFavoriteSport = favoriteSport;

            Intent selectIntent = new Intent(ACTION_SELECT_SELECTED);
            selectIntent.putExtra("SELECTED_SELECT", favoriteSport);
            context.sendBroadcast(selectIntent);

        });
    }

    @Override
    public int getItemCount() {
        return favoriteSports.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivIcon;
        private final TextView tvCategory;
        private final RelativeLayout button;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ic_fav);
            tvCategory = itemView.findViewById(R.id.tv_favorite);
            button = itemView.findViewById(R.id.rl_favorite);

        }
    }
}