package com.swaraj.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swaraj.myapplication.R;
import com.swaraj.myapplication.UpdateProductInterface;
import com.swaraj.myapplication.data.ProductData;

import java.util.ArrayList;

public class ReceivedAdapter extends RecyclerView.Adapter<ReceivedAdapter.viewHolder>{

    Context context;
    ArrayList<ProductData> arrayList;
    UpdateProductInterface productInterface;

    public ReceivedAdapter(Context context) {
        this.context = context;
    }

    public ReceivedAdapter(Context context, ArrayList<ProductData> arrayList, UpdateProductInterface productInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.productInterface = productInterface;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_received_order,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ProductData data = arrayList.get(position);
        holder.tvProductName.setText(data.getName().toUpperCase());
        holder.tvQuantity.setText("Quantity : "+data.getQuantity());

        holder.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productInterface.onClick(arrayList.get(position),position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{

        TextView tvProductName,tvQuantity;
        Button btnOrder;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnOrder = itemView.findViewById(R.id.btnReceivedOrder);
        }
    }
}