package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Experience;
import com.example.hotrovieclam.Model.KiNang;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentProfileCandidateBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Profile_Candidate_Fragment extends Fragment {
    private FragmentProfileCandidateBinding binding;
    String id_candidate = null;
    String id_Job=null;
    ArrayList<TruongHoc> truonghoc = new ArrayList<>();
    ArrayList<Experience> experiences = new ArrayList<>();
    ArrayList<KiNang>kiNangs= new ArrayList<>();
    ArrayAdapter<TruongHoc> truongHocAdapter;
    ArrayAdapter<Experience> kinhnghiemAdater;
    ArrayAdapter<KiNang>kiNangArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileCandidateBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
//

        truongHocAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, truonghoc);
        binding.lisviewHocVan.setAdapter(truongHocAdapter);

        kinhnghiemAdater = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, experiences);
        binding.kinhnghiem.setAdapter(kinhnghiemAdater);

        kiNangArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,kiNangs);
        binding.kinang.setAdapter(kiNangArrayAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            id_candidate = bundle.getString("id_candidate");
            id_Job = bundle.getString("ID_JOB");
        }
        Log.d("KOK", "yyyyyyyyy "+id_Job);
        LoadDataToFireBase(id_candidate);
        binding.deleteCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCandidate(id_Job, id_candidate);
            }
        });

        binding.btnChapNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    private void deleteCandidate(String idJob, String candidateId) {
        // Hiển thị hộp thoại xác nhận
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa ứng viên này không?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn "Xóa", tiến hành xóa ứng viên và tài liệu người dùng
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // Bước 1: Truy vấn và lấy tài liệu ứng viên trong collection "application"
                        db.collection("jobs")
                                .document(idJob)  // ID của công việc
                                .collection("application")
                                .whereEqualTo("candidateId", candidateId)  // Tìm ứng viên theo candidateId
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot querySnapshot = task.getResult();

                                            // Kiểm tra nếu có tài liệu ứng viên cần xóa
                                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                // Lấy id của tài liệu trong collection "application"
                                                for (QueryDocumentSnapshot document : querySnapshot) {
                                                    String documentId = document.getId(); // Lấy id của tài liệu trong application

                                                    // Xóa tài liệu ứng viên dựa trên documentId
                                                    db.collection("jobs")
                                                            .document(idJob)
                                                            .collection("application")
                                                            .document(documentId)
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    // Xóa thành công tài liệu ứng viên
                                                                    Toast.makeText(getContext(), "Xóa ứng viên thành công!", Toast.LENGTH_SHORT).show();
                                                                    Bundle bundle = new Bundle();
                                                                    bundle.putBoolean("delete", true);  // Đảm bảo tên khóa là "delete"
                                                                    getParentFragmentManager().setFragmentResult("deleteSuccess", bundle);
                                                                    getParentFragmentManager().popBackStack();  // Đóng Fragment

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getContext(), "Lỗi khi xóa ứng viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            } else {
                                                // Nếu không tìm thấy tài liệu ứng viên
                                                Toast.makeText(getContext(), "Không tìm thấy ứng viên với id: " + candidateId, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // Trường hợp lỗi khi truy vấn
                                            Toast.makeText(getContext(), "Lỗi khi truy vấn Firestore: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Hủy", null) // Nếu chọn "Hủy", không làm gì cả
                .show();
    }


    public void LoadDataToFireBase(String id_candidate) {
        //Log.d("EEE", "LoadDataToFireBase: "+id_candidate);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(id_candidate);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String phoneNumber = document.getString("phoneNumber");

                        // Hiển thị thông tin người dùng
                        binding.name.setText(name);
                        binding.email.setText(email);
                        binding.phoneNumber.setText(phoneNumber);
                        //Log.d("PPPP", "onComplete: "+email+name);
                        DocumentReference profileRef = db.collection("users").document(id_candidate)
                                .collection("role").document("candidate")
                                .collection("profile").document(id_candidate);
                        profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot profileDocument = task.getResult();
                                    if (profileDocument.exists()) {
                                        String ngaysinh = profileDocument.getString("birthday"); // Lấy ngày sinh
                                        String diachi = profileDocument.getString("address"); // Lấy địa chỉ
                                        int gioitinh = profileDocument.getLong("gioitinh").intValue(); // Lấy giới tính dưới dạng số

                                        // Hiển thị thông tin profile
                                        binding.born.setText(ngaysinh); // Hiển thị ngày sinh
                                        binding.born.setTextColor(getResources().getColor(R.color.black));// Hiển thị tên giới tính
// Hiển thị địa chỉ

                                        // Truy vấn bảng genders để lấy tên giới tính
                                        db.collection("genders").document(String.valueOf(gioitinh))
                                                .get()
                                                .addOnCompleteListener(genderTask -> {
                                                    if (genderTask.isSuccessful()) {
                                                        DocumentSnapshot genderDocument = genderTask.getResult();
                                                        if (genderDocument.exists()) {
                                                            String genderName = genderDocument.getString("name");
                                                            binding.gender.setText(genderName);
                                                            binding.gender.setTextColor(getResources().getColor(R.color.black));// Hiển thị tên giới tính
                                                        } else {
                                                            Log.d("Firestore", "Không tìm thấy giới tính cho ID: " + gioitinh);
                                                        }
                                                    } else {
                                                        Log.e("Firestore", "Lỗi khi truy vấn bảng genders.", genderTask.getException());
                                                    }
                                                });

                                    } else {
                                        binding.gender.setText("Chưa cập nhật");
                                        binding.gender.setTextColor(getResources().getColor(R.color.chuacapnhat));
                                        binding.born.setText("Chưa cập nhật");
                                        binding.born.setTextColor(getResources().getColor(R.color.chuacapnhat));

                                        Log.d("Firestore", "Không tìm thấy dữ liệu profile.");
                                    }
                                    DocumentReference doc = db.collection("users").document(id_candidate)
                                            .collection("role").document("candidate")
                                            .collection("introduction").document("introductdata");

                                    doc.addSnapshotListener((documentSnapshot, e) -> {
                                        if (e != null) {
                                            // Xử lý lỗi nếu có
                                            Log.e("Firestore", "Error listening to document", e);
                                            return;
                                        }

                                        if (documentSnapshot != null && documentSnapshot.exists()) {
                                            // Kiểm tra xem trường "introduction" đã có data hay chưa
                                            String introduction = documentSnapshot.getString("introduction");
                                            if (introduction != null && !introduction.isEmpty()) {
                                                binding.gioithieu.setText(introduction);
                                                // Nếu có data thì ẩn nút thêm thông tin giới thiệu bản thân và hiện nút và ngược lại
                                                Log.d("Firestore", "Đã có data: " + introduction);
                                            } else {
                                                binding.gioithieu.setText("Chưa cập nhật");
                                                binding.gioithieu.setTextColor(getResources().getColor(R.color.chuacapnhat));
                                            }
                                        } else {
                                            Log.d("Firestore", "Không có document với UID này trong bảng Introduces");
                                            binding.gioithieu.setText("Chưa cập nhật");
                                            binding.gioithieu.setTextColor(getResources().getColor(R.color.chuacapnhat));
                                        }
                                    });
                                } else {
                                    Log.d("Firestore", "Lỗi khi truy vấn dữ liệu profile.", task.getException());
                                }
                            }
                        });



                        CollectionReference docSchool = db.collection("users")
                                .document(id_candidate)
                                .collection("role")
                                .document("candidate")
                                .collection("school");
                        docSchool.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            truonghoc.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                                            if (task.getResult().isEmpty()) {
                                                // Hiển thị thông báo nếu không có dữ liệu
                                                truonghoc.add(new TruongHoc(null, null, "Chưa cập nhật trường học", "", "", "", null, null));
                                                truongHocAdapter.notifyDataSetChanged();
                                                //binding.lisviewHocVan.setVisibility(View.VISIBLE); // Hiển thị ListView
                                            } else {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    // Lấy dữ liệu từ document và chuyển vào đối tượng TruongHoc
                                                    String nameSchool = document.getString("nameSchool");
                                                    String nganhHoc = document.getString("nganhHoc");
                                                    String timeStart = document.getString("timeStart");
                                                    String timeEnd = document.getString("timeEnd");

                                                    TruongHoc truongHoc = new TruongHoc(null, null, nameSchool, nganhHoc, timeStart, timeEnd, null, null);
                                                    truonghoc.add(truongHoc); // Thêm vào danh sách
                                                }
                                            }

                                            truongHocAdapter.notifyDataSetChanged(); // Cập nhật adapter sau khi thêm dữ liệu mới
                                        } else {
                                            Log.d("Firestore", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });


                        CollectionReference getKinhNghiem = db.collection("users")
                                .document(id_candidate)
                                .collection("role")
                                .document("candidate")
                                .collection("experience");
                        getKinhNghiem.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            experiences.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                                            if (task.getResult().isEmpty()) {
                                                // Hiển thị thông báo nếu không có dữ liệu
                                                experiences.add(new Experience(null, "", "", "", "Chưa cập nhật kinh nghiệm", "", null));
                                                kinhnghiemAdater.notifyDataSetChanged();
                                                //binding.lisviewHocVan.setVisibility(View.VISIBLE); // Hiển thị ListView
                                            } else {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    // Lấy dữ liệu từ document và chuyển vào đối tượng TruongHoc
                                                    String name_organization = document.getString("name_organization");
                                                    String position = document.getString("position");
                                                    String timeStart = document.getString("time_start");
                                                    String timeEnd = document.getString("time_end");

                                                    Experience experie = new Experience(null, timeEnd, timeStart, position, name_organization, null, null);

                                                    experiences.add(experie); // Thêm vào danh sách
                                                }
                                            }

                                            kinhnghiemAdater.notifyDataSetChanged(); // Cập nhật adapter sau khi thêm dữ liệu mới
                                        } else {
                                            Log.d("Firestore", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        CollectionReference getSkill = db.collection("users")
                                .document(id_candidate)
                                .collection("role")
                                .document("candidate")
                                .collection("skills");
                        getSkill.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            kiNangs.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                                            if (task.getResult().isEmpty()) {
                                                // Hiển thị thông báo nếu không có dữ liệu
                                                kiNangs.add(new KiNang(null, null, "Chưa cập nhật kĩ năng", null));
                                                kiNangArrayAdapter.notifyDataSetChanged();
                                                //binding.lisviewHocVan.setVisibility(View.VISIBLE); // Hiển thị ListView
                                            } else {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    // Lấy dữ liệu từ document và chuyển vào đối tượng TruongHoc
                                                    String name = document.getString("name");


                                                    KiNang kiNang = new KiNang( null, null,name,null);

                                                    kiNangs.add(kiNang); // Thêm vào danh sách
                                                }
                                            }

                                            kiNangArrayAdapter.notifyDataSetChanged(); // Cập nhật adapter sau khi thêm dữ liệu mới
                                        } else {
                                            Log.d("Firestore", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        db.collection("users").document(id_candidate).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String avatarUrl = documentSnapshot.getString("avatar");
                                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                            // Dùng Glide để tải ảnh từ URL và hiển thị vào ImageView
                                            Glide.with(getContext())
                                                    .load(avatarUrl)
                                                    .centerCrop()
                                                    .into(binding.imageProfile); // imageView là ImageView của bạn
                                            Log.d("ii", "onComplete: lay dc anh vs uid"+id_candidate );
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Xử lý lỗi nếu có
                                    Log.e("FirestoreError", "Lỗi khi lấy dữ liệu", e);
                                });

                    } else {
                        Log.d("Firestore", "Không tìm thấy dữ liệu người dùng.");
                    }
                } else {
                    Log.d("Firestore", "Lỗi khi truy vấn dữ liệu.", task.getException());
                }
            }
        });


    }
}