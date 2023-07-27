package com.swaraj.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.swaraj.myapplication.R;
import com.swaraj.myapplication.VideoPlayerActivity;
import com.swaraj.myapplication.data.ProductData;

import java.util.ArrayList;

public class OrderSummeryAdapter extends RecyclerView.Adapter<OrderSummeryAdapter.viewHolder>{

    Context context;
    ArrayList<ProductData> arrayList;

    public OrderSummeryAdapter(Context context, ArrayList<ProductData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_order_summery,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ProductData data = arrayList.get(position);

      holder.tvProductName.setText(""+(position+1)+". "+data.getName());
      holder.tvQuantity.setText(""+data.getQuantity());
      holder.tvPrice.setText(""+data.getrPrice());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{

        TextView tvProductName,tvPrice,tvQuantity;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tvOrderItemName);
            tvPrice = itemView.findViewById(R.id.tvOrderItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvOrderItemQuantity);

        }
    }
}
