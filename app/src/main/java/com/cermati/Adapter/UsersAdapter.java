package com.cermati.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cermati.R;
import com.cermati.model.UsersModel;

import java.util.List;

public class UsersAdapter extends   RecyclerView.Adapter<UsersAdapter.ViewHolder>{
    private List<UsersModel> usersModels;
    private Context context;

    public UsersAdapter(List<UsersModel> usersModels, Context context) {
        this.usersModels = usersModels;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UsersModel model = usersModels.get(position);

       holder.name.setText(model.getLogin());
        Glide.with(context)
                .load( model.getAvatar_url())
                .into(holder.images);
    }

    @Override
    public int getItemCount() {
        return usersModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView images;


        private ViewHolder(View itemView) {
            super(itemView);
           name = itemView.findViewById(R.id.TVname);
            images = itemView.findViewById(R.id.img);

        }
    }
}
