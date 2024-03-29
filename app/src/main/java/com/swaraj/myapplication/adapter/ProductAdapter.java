package com.swaraj.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.swaraj.myapplication.R;
import com.swaraj.myapplication.VideoPlayerActivity;
import com.swaraj.myapplication.data.ProductData;

import java.util.ArrayList;
import java.util.Random;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.viewHolder>{

    Context context;
    ArrayList<ProductData> arrayList;

    public ProductAdapter(Context context, ArrayList<ProductData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_product,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ProductData data = arrayList.get(position);

        Glide.with(context)
                .load(data.getImagePath()) // image url
                .placeholder(R.drawable.noimage) // any placeholder to load at start
                .error(R.drawable.noimage)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop()
                .into(holder.productImage);
        holder.tvProductName.setText(data.getName().toUpperCase());
        holder.tvWPrice.setText("Wholesale Price : "+data.getwPrice()+"£");
        holder.tvRPrice.setText("Retail Price : "+data.getrPrice()+"£");
        holder.tvQuantity.setText("Quantity : "+data.getQuantity());
        if(data.getQuantity() <= 5){
            holder.redFlag.setVisibility(View.VISIBLE);
        }else{
            holder.redFlag.setVisibility(View.GONE);
        }
        if(data.getVideoPath().equalsIgnoreCase("")){
            holder.ivVideo.setVisibility(View.GONE);
        }else{
            holder.ivVideo.setVisibility(View.VISIBLE);
        }
        holder.ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("videoLink",data.getVideoPath());
                context.startActivity(intent);
            }
        });
        //holder.linearLayout.setBackgroundResource(R.color.purple_100);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{

        ImageView productImage, redFlag, ivVideo;
        TextView tvProductName, tvWPrice, tvRPrice,tvQuantity;
        CardView linearLayout;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.ivProductImage);
            redFlag = itemView.findViewById(R.id.ivRedFlag);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvWPrice = itemView.findViewById(R.id.tvWholesalePrice);
            tvRPrice = itemView.findViewById(R.id.tvRetailPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            linearLayout = itemView.findViewById(R.id.llListItem);
            ivVideo = itemView.findViewById(R.id.ivDisplayVideo);

        }
    }
}