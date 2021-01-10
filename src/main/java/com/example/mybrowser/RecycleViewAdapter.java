package com.example.mybrowser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {
    private List<Star> collection;
    Context context;

    public RecycleViewAdapter(Context ct, List<Star>bookmarks){
        context = ct;
        collection = bookmarks;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycleview_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Star item = collection.get(position);

        holder.textUrl.setText(item.getUrl());
        holder.textTitle.setText(item.getTitle());
        holder.img.setImageResource(R.drawable.ic_baseline_star_border_24);

        holder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("URL_BM", collection.get(position).getUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textUrl, textTitle;
        ImageView img;
        ConstraintLayout rowLayout;
        ImageButton removeItemBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textUrl = itemView.findViewById(R.id.item_url);
            textTitle = itemView.findViewById(R.id.item_title);
            img = itemView.findViewById(R.id.page_icon_imageView);
            rowLayout = itemView.findViewById(R.id.rowLayout);
            removeItemBtn = itemView.findViewById(R.id.delete_bookmark_btn);
            removeItemBtn.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("TestDeleteItem ", v.toString());
            if(v.equals(removeItemBtn)){
                removeAt(getAdapterPosition());
            }
        }

        private void removeAt(int position) {
            collection.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,collection.size());

            //change sharedPreference data
            Log.d("TestRVAdapter", "BookmarkItem deleted");
            SharedPreferences deletePrefers = context.getSharedPreferences("BOOKMARKS", MODE_PRIVATE);
            SharedPreferences.Editor deleteEditor = deletePrefers.edit();
            Gson gson = new Gson();
            String json = gson.toJson(collection);
            deleteEditor.putString("collections", json);
            deleteEditor.apply();
        }
    }

    public void clearInAdapter() {
        collection.clear();
        notifyItemRangeRemoved(0, collection.size());
        Log.d("TestRVAdapter", "BookmarkData cleared");
    }

}
