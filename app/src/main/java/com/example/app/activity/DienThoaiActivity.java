package com.example.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.adapter.DienThoaiAdapter;
import com.example.app.model.SanPhamMoi;
import com.example.app.model.SanPhamMoiModel;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DienThoaiActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int page = 1;
    int loai,iduser,idshop;
    DienThoaiAdapter adapterDT;
    List<SanPhamMoi> sanPhamMoiList;
    LinearLayoutManager linearLayoutManager;
    Handler handle = new Handler();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dien_thoai);
        loai = getIntent().getIntExtra("loai",1);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        AnhXa();
        ActionToolBar();
        getData(page);
        addEventLoad();
    }
    private void addEventLoad(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isLoading == false){
                    if(linearLayoutManager.findLastCompletelyVisibleItemPosition() == sanPhamMoiList.size()-1){
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }
    private void loadMore() {
        handle.post(new Runnable() {
            @Override
            public void run() {
                sanPhamMoiList.add(null);
                adapterDT.notifyItemInserted(sanPhamMoiList.size() - 1);
            }
        });
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                sanPhamMoiList.remove(sanPhamMoiList.size() - 1);
                adapterDT.notifyItemRemoved(sanPhamMoiList.size());
                page = page + 1;
                getData(page);
                // Đặt isLoading thành false trong onSuccess hoặc onFailure của getData
            }
        }, 500);
    }

    private void getData(int page) {
        iduser = Utils.user_current.getId();
        compositeDisposable.add(apiBanHang.getlaytheoloai(page, loai, iduser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                if (adapterDT == null) {
                                    sanPhamMoiList = sanPhamMoiModel.getResult();
                                    adapterDT = new DienThoaiAdapter(getApplicationContext(), sanPhamMoiList);
                                    recyclerView.setAdapter(adapterDT);
                                } else {
                                    int vitri = sanPhamMoiList.size() - 1;
                                    int soluongadd = sanPhamMoiModel.getResult().size();
                                    for (int i = 0; i < soluongadd; i++) {
                                        sanPhamMoiList.add(sanPhamMoiModel.getResult().get(i));
                                    }
                                    adapterDT.notifyItemRangeInserted(vitri + 1, soluongadd); // Cập nhật vị trí bắt đầu
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Hết dữ liệu", Toast.LENGTH_LONG).show();
                                // isLoading = true;
                            }
                            isLoading = false; // Đặt isLoading thành false ở đây
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối server", Toast.LENGTH_LONG).show();
                            isLoading = false; // Đặt isLoading thành false ở đây nếu gặp lỗi
                        }
                ));
    }

    //    private void loadMore(){
//        handle.post(new Runnable() {
//            @Override
//            public void run() {
//                sanPhamMoiList.add(null);
//                adapterDT.notifyItemInserted(sanPhamMoiList.size()-1);
//            }
//        });
//        handle.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                sanPhamMoiList.remove(sanPhamMoiList.size()-1);
//                adapterDT.notifyItemRemoved(sanPhamMoiList.size());
//                page = page +1;
//                getData(page);
//                adapterDT.notifyDataSetChanged();
//                isLoading = false;
//            }
//        },1000);
//    }
//    private void getData(int page){
//        iduser=Utils.user_current.getId();
//        compositeDisposable.add(apiBanHang.getlaytheoloai(page,loai,iduser)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        sanPhamMoiModel -> {
//                            if(sanPhamMoiModel.isSuccess()){
//                                if(adapterDT == null) {
//                                    sanPhamMoiList = sanPhamMoiModel.getResult();
//                                    adapterDT = new DienThoaiAdapter(getApplicationContext(), sanPhamMoiList);
//                                    recyclerView.setAdapter(adapterDT);
//                                }else{
//                                    int vitri = sanPhamMoiList.size() -1;
//                                    int soluongadd = sanPhamMoiModel.getResult().size();
//                                    for(int i=0; i<soluongadd; i++){
//                                        sanPhamMoiList.add(sanPhamMoiModel.getResult().get(i));
//                                    }
//                                    adapterDT.notifyItemRangeInserted(vitri,soluongadd);
//                                 }
//                            }else{
//                                Toast.makeText(getApplicationContext(),"Het du lieu",Toast.LENGTH_LONG).show();
//                               // isLoading = true;
//                            }
//                        },
//                        throwable -> {
//                            Toast.makeText(getApplicationContext(),"Khong ket noi server",Toast.LENGTH_LONG).show();
//                        }
//
//                ));
//    }
    private void ActionToolBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void AnhXa()
    {
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleview_dt);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        sanPhamMoiList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}