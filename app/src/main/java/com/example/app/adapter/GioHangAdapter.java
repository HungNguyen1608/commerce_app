package com.example.app.adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.interf.ImageClickListener;
import com.example.app.model.EventBus.TinhTongEvent;
import com.example.app.model.GioHang;
import com.example.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder> {
    Context context;
    List<GioHang> gioHangList;

    public GioHangAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GioHang gioHang = gioHangList.get(position);
        holder.item_giohang_tensp.setText(gioHang.getTensanpham());
        holder.item_giohang_soluong.setText(gioHang.getSoluong()+ " ");
        //Glide.with(context).load(gioHang.getHinhanh()).into(holder.item_giohang_image);
        if(gioHang.getHinhanh().contains("http")){
            Glide.with(context).load(gioHang.getHinhanh()).into(holder.item_giohang_image);
        } else {
            String hinh = Utils.BASE_URL + "images/" +gioHang.getHinhanh();
            Glide.with(context).load(hinh).into(holder.item_giohang_image);
        }
        //String giaString = String.valueOf(gioHang.getGiasp());
        //giaString = giaString.replace(".", "");
        // Chuyển đổi chuỗi giá đã được làm sạch thành số
        long gia = Long.parseLong(String.valueOf(gioHang.getGiasp()));
        holder.item_giohang_gia.setText("Giá: " + gia + "Đ");
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.item_giohang_giasp2.setText(decimalFormat.format(gia*gioHang.getSoluong()));
        holder.item_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Utils.mangmuahang.add(gioHang);
                    EventBus.getDefault().postSticky(new TinhTongEvent());

                }else{
                    for (int i = 0; i < Utils.mangmuahang.size(); i++){
                        Utils.mangmuahang.remove(i);
                        EventBus.getDefault().postSticky(new TinhTongEvent());
                    }
                }
            }
        });
        holder.setListener(new ImageClickListener() {
            @Override
            public void onImageClick(View view, int pos, int giatri) {
                if(giatri == 1) {
                    if (gioHangList.get(pos).getSoluong() > 1) {
                        int soluongmoi = gioHangList.get(pos).getSoluong() - 1;
                        gioHangList.get(pos).setSoluong(soluongmoi);
                        holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluong() + " ");
                        long gia = gioHangList.get(pos).getGiasp() * gioHangList.get(pos).getSoluong();
                        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                        holder.item_giohang_giasp2.setText(decimalFormat.format(gia));
                        EventBus.getDefault().postSticky(new TinhTongEvent());
                    } else if (gioHangList.get(pos).getSoluong() == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("Thông báo");
                        builder.setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?");
                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.manggiohang.remove(pos);
                                notifyDataSetChanged();
                                EventBus.getDefault().postSticky(new TinhTongEvent());
                            }
                        });
                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();

                    }
                }else if(giatri == 2){
                    if (gioHangList.get(pos).getSoluong() < 11) {
                        int soluongmoi = gioHangList.get(pos).getSoluong() + 1;
                        gioHangList.get(pos).setSoluong(soluongmoi);
                    }
                    holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluong() + " ");
                    long gia = gioHangList.get(pos).getGiasp() * gioHangList.get(pos).getSoluong();
                    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                    holder.item_giohang_giasp2.setText(decimalFormat.format(gia));
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }else {
//                        xoa khi ấn icon xoa
                    if (giatri==3){
                        AlertDialog.Builder builder=new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("Thông Báo");
                        builder.setMessage("Bạn muốn xóa sản phẩm khỏi giỏ hàng");
                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.manggiohang.remove(pos);
                                notifyDataSetChanged();
                                EventBus.getDefault().postSticky(new TinhTongEvent());
                            }
                        });
                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                    holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluong() + " ");
                    long gia = gioHangList.get(pos).getGiasp() * gioHangList.get(pos).getSoluong();
                    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                    holder.item_giohang_giasp2.setText(decimalFormat.format(gia));
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_giohang_image,imgCong,imgTru;
        TextView item_giohang_tensp, item_giohang_gia, item_giohang_soluong, item_giohang_giasp2;
        ImageClickListener listener;
        ConstraintLayout btnxoa;
        CheckBox item_checkbox;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            item_giohang_image = itemView.findViewById(R.id.item_giohang_image);
            item_giohang_tensp = itemView.findViewById(R.id.item_giohang_tensp);
            item_giohang_gia = itemView.findViewById(R.id.item_giohang_gia);
            item_giohang_soluong = itemView.findViewById(R.id.item_giohang_soluong);
            item_giohang_giasp2 = itemView.findViewById(R.id.item_giohang_giasp2);
            imgCong = itemView.findViewById(R.id.item_giohang_cong);
            imgTru = itemView.findViewById(R.id.item_giohang_tru);
            item_checkbox = itemView.findViewById(R.id.item_checkbox);
            btnxoa=itemView.findViewById(R.id.xoa);

            //su kien click
            imgCong.setOnClickListener(this);
            imgTru.setOnClickListener(this);
            btnxoa.setOnClickListener(this);
        }

        public void setListener(ImageClickListener listener) {
            this.listener = listener;
        }


        @Override
        public void onClick(View view) {
            if(view == imgTru){
                listener.onImageClick(view, getAdapterPosition(),1);
            }else if(view == imgCong){
                listener.onImageClick(view, getAdapterPosition(),2);
            }
            else if(view == btnxoa){
                listener.onImageClick(view, getAdapterPosition(),3);
            }
        }
//        public void onClick(View view) {
//            if (listener != null) {
//                if (view == imgTru) {
//                    listener.onImageClick(view, getAdapterPosition(), 1);
//                } else if (view == imgCong) {
//                    listener.onImageClick(view, getAdapterPosition(), 2);
//                }
//            }
//        }
    }
}
