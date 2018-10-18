package com.dwaynedevelopment.passtimes.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_FAVORITE_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.SnackbarUtils.invokeSnackBar;

public class FavoriteViewAdapter extends RecyclerView.Adapter<FavoriteViewAdapter.ViewHolder> {

    private final ArrayList<Sport> selected = new ArrayList<>();

    private ArrayList<Sport> favoriteSports;
    private AppCompatActivity context;


    public FavoriteViewAdapter(AppCompatActivity context, ArrayList<Sport> favoriteSports) {
        this.favoriteSports = favoriteSports;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_favorites, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Sport favoriteSport = favoriteSports.get(position);
        Glide.with(context).load(favoriteSport.getIdle()).into(holder.ivIcon);
        holder.tvCategory.setText(favoriteSport.getCategory());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    Glide.with(context).load(favoriteSport.getIdle()).into(holder.ivIcon);
                    v.setSelected(false);
                    holder.tvCategory.setTextColor(context.getResources().getColor(R.color.colorDarkPrimary));
                    selected.remove(favoriteSport);

                } else {
                    Glide.with(context).load(favoriteSport.getActive()).into(holder.ivIcon);
                    v.setSelected(true);
                    holder.tvCategory.setTextColor(context.getResources().getColor(R.color.colorSecondaryAccent));
                    selected.add(favoriteSport);
                }

                Intent selectIntent = new Intent(ACTION_FAVORITE_SELECTED);
                selectIntent.putParcelableArrayListExtra("SELECTED_SPORTS", selected);
                context.sendBroadcast(selectIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteSports.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivIcon;
        private TextView tvCategory;
        private RelativeLayout button;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ic_fav);
            tvCategory = itemView.findViewById(R.id.tv_favorite);
            button = itemView.findViewById(R.id.rl_favorite);

        }
    }
}