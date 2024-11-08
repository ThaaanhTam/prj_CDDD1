package com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.hotrovieclam.Interface.APIAdress.AddressAPI;
import com.example.hotrovieclam.Interface.APIAdress.ApiResponse;
import com.example.hotrovieclam.Model.Address;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentDialogAddressBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Dialog_address extends DialogFragment {
    private FragmentDialogAddressBinding binding;
    private static ThongTinCaNhanFragment thongTinCaNhanFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDialogAddressBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://esgoo.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Khởi tạo AddressAPI
        AddressAPI addressAPI = retrofit.create(AddressAPI.class);
        addressAPI.getAddressList(1, "0").enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Address> addressList = response.body().getData();

                    // Tạo danh sách tên tỉnh thành và thêm "Chọn thành phố" vào đầu
                    String[] tinhThanhNames = new String[addressList.size() + 1];
                    tinhThanhNames[0] = "Chọn Thành Phố";
                    for (int i = 0; i < addressList.size(); i++) {
                        tinhThanhNames[i + 1] = addressList.get(i).getFullName(); // Dùng full_name
                    }

                    // Tạo adapter và gắn vào Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Dialog_address.this.getContext(),
                            android.R.layout.simple_spinner_item, tinhThanhNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.nameTinhThanh.setAdapter(adapter);

                    // Thêm sự kiện khi người dùng chọn tỉnh thành
                    binding.nameTinhThanh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, android.view.View selectedItemView, int position, long id) {
                            if (position > 0) { // Nếu chọn tỉnh thành (vị trí 0 là "Chọn Thành Phố")
                                String idTinhThanh = addressList.get(position - 1).getId();
                                loadQuanHuyen(idTinhThanh); // Lấy quận huyện cho tỉnh thành đã chọn
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // Do nothing
                        }
                    });
                } else {
                    Log.e("Error", "Không lấy được dữ liệu");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Error", "Lỗi kết nối: " + t.getMessage());
            }
        });
        binding.xacnhan.setVisibility(View.GONE);
        binding.xacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = binding.diachicuthe.getText().toString().trim();
                if (a.isEmpty()){
                    Toast.makeText(getContext(), "Vui lòng nhập địa chỉ cụ thể", Toast.LENGTH_SHORT).show();
                    binding.diachicuthe.requestFocus();
                    return;
                }
                String b = binding.nameTinhThanh.getSelectedItem().toString().trim();
                String c = binding.quanHuyen.getSelectedItem().toString();
                String d = binding.phuongxa.getSelectedItem().toString();
                String diachhi = a + ", " + d + ", " + c + ", " + b;

                if (thongTinCaNhanFragment == null) {
                    thongTinCaNhanFragment = new ThongTinCaNhanFragment();
                    Bundle args = new Bundle();
                    args.putString("diachi", diachhi);
                    thongTinCaNhanFragment.setArguments(args);
                } else {
                    Bundle args = new Bundle();
                    args.putString("diachi", diachhi);
                    thongTinCaNhanFragment.setArguments(args);
                }

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, thongTinCaNhanFragment)
                        .addToBackStack(null)
                        .commit();

                Log.d("sss", diachhi);

                // Đóng dialog hiện tại
                dismiss();
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Thay đổi kích thước của dialog
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Đặt chiều rộng và chiều cao cho dialog
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // Bạn cũng có thể sử dụng chiều cao cố định hoặc tỷ lệ, ví dụ:
                // window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);
            }
        }
    }

    private void loadQuanHuyen(String idTinhThanh) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://esgoo.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AddressAPI addressAPI = retrofit.create(AddressAPI.class);
        addressAPI.getAddressList(2, idTinhThanh).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Address> quanHuyenList = response.body().getData();

                    // Tạo danh sách tên quận huyện để gắn vào Spinner
                    String[] quanHuyenNames = new String[quanHuyenList.size() + 1];
                    quanHuyenNames[0] = "Chọn Quận Huyện";
                    for (int i = 0; i < quanHuyenList.size(); i++) {
                        quanHuyenNames[i + 1] = quanHuyenList.get(i).getFullName();
                    }

                    // Tạo adapter và gắn vào Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Dialog_address.this.getContext(),
                            android.R.layout.simple_spinner_item, quanHuyenNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.quanHuyen.setAdapter(adapter);

                    // Thêm sự kiện khi người dùng chọn quận huyện
                    binding.quanHuyen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, android.view.View selectedItemView, int position, long id) {
                            if (position > 0) { // Nếu chọn quận huyện (vị trí 0 là "Chọn Quận Huyện")
                                String idQuanHuyen = quanHuyenList.get(position - 1).getId();
                                loadPhuongXa(idQuanHuyen); // Lấy phường xã cho quận huyện đã chọn
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // Do nothing
                        }
                    });

                    // Tự động gọi API lấy phường xã cho quận huyện đầu tiên
                    if (quanHuyenList.size() > 0) {
                        String idQuanHuyen = quanHuyenList.get(0).getId();
                        loadPhuongXa(idQuanHuyen); // Lấy phường xã cho quận huyện đầu tiên
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Error", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void loadPhuongXa(String idQuanHuyen) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://esgoo.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AddressAPI addressAPI = retrofit.create(AddressAPI.class);
        addressAPI.getAddressList(3, idQuanHuyen).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Address> phuongXaList = response.body().getData();

                    // Tạo danh sách tên phường xã để gắn vào Spinner
                    String[] phuongXaNames = new String[phuongXaList.size() + 1];
                    phuongXaNames[0] = "Chọn Phường Xã";
                    for (int i = 0; i < phuongXaList.size(); i++) {
                        phuongXaNames[i + 1] = phuongXaList.get(i).getFullName();
                    }

                    // Tạo adapter và gắn vào Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Dialog_address.this.getContext(),
                            android.R.layout.simple_spinner_item, phuongXaNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.phuongxa.setAdapter(adapter);
// Thêm sự kiện khi người dùng chọn phường xã
                    binding.phuongxa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            if (position > 0) { // Nếu người dùng đã chọn phường xã (vị trí 0 là "Chọn Phường Xã")
                                binding.xacnhan.setVisibility(View.VISIBLE);
                            } else {
                                binding.xacnhan.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // Do nothing
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Error", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

}

