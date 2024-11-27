package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Activity.JobDetailMain;
import com.example.hotrovieclam.Activity.MessageActivity;
import com.example.hotrovieclam.Model.Experience;
import com.example.hotrovieclam.Model.KiNang;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentProfileCandidateBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile_Candidate_Fragment extends Fragment {
    private FragmentProfileCandidateBinding binding;
    String id_candidate = null;
    String id_Job = null;
    ArrayList<TruongHoc> truonghoc = new ArrayList<>();
    ArrayList<Experience> experiences = new ArrayList<>();
    ArrayList<KiNang> kiNangs = new ArrayList<>();
    ArrayAdapter<TruongHoc> truongHocAdapter;
    ArrayAdapter<Experience> kinhnghiemAdater;
    ArrayAdapter<KiNang> kiNangArrayAdapter;
    String candi;
    private WebView webView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileCandidateBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        webView = binding.webview;
        progressBar = binding.progressBar;
        truongHocAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, truonghoc);
        binding.lisviewHocVan.setAdapter(truongHocAdapter);

        kinhnghiemAdater = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, experiences);
        binding.kinhnghiem.setAdapter(kinhnghiemAdater);

        kiNangArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, kiNangs);
        binding.kinang.setAdapter(kiNangArrayAdapter);
        UserSessionManager userSessionManager = new UserSessionManager();
         uid = userSessionManager.getUserUid();



        Bundle bundle = getArguments();
        if (bundle != null) {
            id_candidate = bundle.getString("id_candidate");
            id_Job = bundle.getString("ID_JOB");
            candi = bundle.getString("candidate");

            Log.d("Firestoraaaaaaaaaaaaaaaaaaaaaae", id_candidate + "yyyyyyyyy " + id_Job);
            if (id_candidate != null && id_Job != null) {

                fetchApplicationDetails(id_Job, candi);

            }


        }
        candi = bundle.getString("candidate");

        Log.d("Firestoraaaaaaaaaaaaaaaaaaaaaae", id_candidate + "yyyyyyyyy " + id_Job);
        if (id_candidate != null && id_Job != null) {

            fetchApplicationDetails(id_Job, candi);

        }


        Log.d("KOK", "yyyyyyyyy " + id_Job);
        LoadDataToFireBase(id_candidate);
        binding.deleteCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("UYT", "onClick: ttttttt");
                updateCandidateStatus(id_Job, id_candidate, 0);

            }
        });
        binding.chapnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameCompany;
                String jobTitle;
                db = FirebaseFirestore.getInstance();

                updateCandidateStatusss(id_Job, id_candidate, 1);
                Map<String, Object> messageData = new HashMap<>();
                messageData.put("conversationTitle", "Cuộc hội thoại giữa User1 và User2");

                List<Map<String, Object>> messagesArray = new ArrayList<>();
                Map<String, Object> message = new HashMap<>();
                message.put("content", "Hello, đây là tin nhắn đầu tiên!");
                messagesArray.add(message);

                messageData.put("messages", messagesArray);

                db.collection("users").document(uid)
                        .collection("conversation")
                        .document(id_candidate)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Nếu cuộc trò chuyện đã tồn tại, lấy messageID và gửi tin nhắn
                                String messageID = documentSnapshot.getString("messageID");
                                getJobInfo();
                                getUserCompanyInfo();
                                sendMessage("Chúng tôi xin chân thành cảm ơn bạn đã dành thời gian tham gia vào quá trình tuyển dụng " + companyName + ". Sau khi xem xét và đánh giá kỹ lưỡng hồ sơ và kết quả phỏng vấn của bạn, chúng tôi rất vui mừng thông báo rằng bạn đã trúng tuyển vào vị trí " + title + " tại công ty chúng tôi.\n Chúng tôi hiện đang lên kế hoạch cho lịch phỏng vấn và sẽ thông báo cho bạn thời gian cụ thể trong thời gian sớm nhất. Mong bạn vui lòng giữ lịch linh hoạt để có thể tham gia phỏng vấn khi chúng tôi xác nhận thời gian.", messageID, id_candidate);

                                Intent intent = new Intent(getContext(), MessageActivity.class);
                                intent.putExtra("messageID", messageID);
                                intent.putExtra("receiverID", id_candidate);
                                startActivity(intent);
                            } else {
                                // Nếu cuộc trò chuyện chưa tồn tại, tạo mới
                                db.collection("Message")
                                        .add(messageData)
                                        .addOnSuccessListener(documentReference -> {
                                            String autoGeneratedId = documentReference.getId();
                                            Log.d("Firestore", "Document được tạo với ID: " + autoGeneratedId);

                                            // Tạo conversation cho cả hai người dùng
                                            Task<Void> task1 = db.collection("users").document(uid)
                                                    .collection("conversation")
                                                    .document(id_candidate)
                                                    .set(Collections.singletonMap("messageID", autoGeneratedId));

                                            Task<Void> task2 = db.collection("users").document(id_candidate)
                                                    .collection("conversation")
                                                    .document(uid)
                                                    .set(Collections.singletonMap("messageID", autoGeneratedId));

                                            // Đảm bảo cả hai tác vụ đều thành công
                                            Tasks.whenAllSuccess(task1, task2)
                                                    .addOnSuccessListener(results -> {
                                                        Log.d("Firestore", "Đã tạo conversation cho cả hai người dùng");

                                                        // Gửi tin nhắn sau khi tạo conversation thành công
                                                        sendMessage("oooooo", autoGeneratedId, id_candidate);

                                                        // Chuyển hướng đến MessageActivity
                                                        Intent intent = new Intent(getContext(), MessageActivity.class);
                                                        intent.putExtra("messageID", autoGeneratedId);
                                                        intent.putExtra("receiverID", id_candidate);
                                                        startActivity(intent);
                                                    })
                                                    .addOnFailureListener(err -> {
                                                        Log.e("Firestore", "Lỗi khi tạo conversation: " + err.getMessage());
                                                    });
                                        })
                                        .addOnFailureListener(err -> {
                                            Log.e("Firestore", "Lỗi khi tạo document: " + err.getMessage());
                                        });
                            }
                        })
                        .addOnFailureListener(err -> {
                            Log.e("Firestore", "Lỗi khi kiểm tra conversation: " + err.getMessage());
                        });

            }

        });
        // Cấu hình WebView
        configureWebView();

        return view;
    }



    private void sendMessage(String content, String messID, String re) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo ID cho tin nhắn mới

        // Tạo Map chứa dữ liệu tin nhắn
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender_id", uid);  // Gửi tin nhắn từ người dùng hiện tại
        messageData.put("content", content);
        messageData.put("sent_at", System.currentTimeMillis());

        db.collection("Message").document(messID)
                .collection("messages")
                .add(messageData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ChatApp", "Tin nhắn đã được gửi.");

                    } else {
                        Log.e("ChatApp", "Lỗi gửi tin nhắn: " + task.getException().getMessage());
                    }
                });


        Map<String, Object> status = new HashMap<>();
        status.put("status", "0");


        DocumentReference docRef = db.collection("users").document(re)
                .collection("conversation").document(uid);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        docRef.update(status)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Cập nhật trường thành công!"))
                                .addOnFailureListener(err -> Log.e("Firestore", "Lỗi khi cập nhật trường: " + err.getMessage()));
                    } else {
                        docRef.set(status)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Tạo mới tài liệu thành công!"))
                                .addOnFailureListener(err -> Log.e("Firestore", "Lỗi khi tạo tài liệu: " + err.getMessage()));
                    }
                })
                .addOnFailureListener(err -> Log.e("Firestore", "Lỗi khi kiểm tra tài liệu: " + err.getMessage()));

    }





    private void updateCandidateStatus(String id_Job, String id_candidate, Integer giaTri) {
        // Hiển thị hộp thoại xác nhận
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận bỏ qua")
                .setMessage("Bạn có chắc chắn muốn bỏ qua ứng viên này không?")
                .setPositiveButton("Bỏ qua", (dialog, which) -> {
                    db = FirebaseFirestore.getInstance();

                    // Truy vấn ứng viên trong collection "application"
                    db.collection("jobs")
                            .document(id_Job)  // ID của công việc
                            .collection("application")
                            .whereEqualTo("candidateId", id_candidate)  // Tìm ứng viên theo candidateId
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();

                                    // Kiểm tra nếu có tài liệu ứng viên cần cập nhật
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        // Cập nhật tất cả tài liệu phù hợp
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            String documentId = document.getId(); // Lấy id của tài liệu

                                            // Cập nhật trường "status" thành 0
                                            db.collection("jobs")
                                                    .document(id_Job)
                                                    .collection("application")
                                                    .document(documentId)
                                                    .update("status", giaTri)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Thành công
                                                        Toast.makeText(getContext(), "Cập nhật trạng thái ứng viên thành công!", Toast.LENGTH_SHORT).show();
                                                        Bundle bundle = new Bundle();
                                                        bundle.putBoolean("update", true);  // Gửi kết quả cập nhật
                                                        getParentFragmentManager().setFragmentResult("updateSuccess", bundle);
                                                        getParentFragmentManager().popBackStack();  // Đóng Fragment
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Thất bại
                                                        Toast.makeText(getContext(), "Lỗi khi cập nhật trạng thái: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }
                                }
                            });


                })
                .setNegativeButton("Hủy", null)
                .show(); //;
    }
    private void updateCandidateStatusss(String id_Job, String id_candidate, Integer giaTri) {
        // Hiển thị hộp thoại xác nhận


                    db = FirebaseFirestore.getInstance();

                    // Truy vấn ứng viên trong collection "application"
                    db.collection("jobs")
                            .document(id_Job)  // ID của công việc
                            .collection("application")
                            .whereEqualTo("candidateId", id_candidate)  // Tìm ứng viên theo candidateId
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();

                                    // Kiểm tra nếu có tài liệu ứng viên cần cập nhật
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        // Cập nhật tất cả tài liệu phù hợp
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            String documentId = document.getId(); // Lấy id của tài liệu

                                            // Cập nhật trường "status" thành 0
                                            db.collection("jobs")
                                                    .document(id_Job)
                                                    .collection("application")
                                                    .document(documentId)
                                                    .update("status", giaTri)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Thành công
                                                       // Toast.makeText(getContext(), "Cập nhật trạng thái ứng viên thành công!", Toast.LENGTH_SHORT).show();
                                                       // Đóng Fragment
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Thất bại
                                                        Toast.makeText(getContext(), "Lỗi khi cập nhật trạng thái: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }
                                }
                            });


                }



    private void fetchApplicationDetails(String a, String b) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Truy vấn Firestore với đường dẫn jobs/{jobId}/application/{applicationId}
        firestore.collection("jobs")
                .document(a)
                .collection("application")
                .document(b)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("Firestoraaaaaaaaaaaaaaaaaaaaaae", candi + "yyyyyyyyy " + id_Job);
                        if (document.exists()) {
                            // Lấy dữ liệu từ document
                            String fileCv = document.getString("cv_file");

                            // Hiển thị dữ liệu (hoặc xử lý tiếp)
                            Log.d("Firestoraaaaaaaaaaaaaaaaaaaaaae", "File CV URL: " + fileCv);

                            if (fileCv == null || fileCv.isEmpty()) {
                                binding.cvFile.setVisibility(View.GONE);

                            } else {
                                binding.cvFile.setVisibility(View.VISIBLE);

                                // Xử lý sự kiện click để mở CV
                                binding.cvFile.setOnClickListener(new View.OnClickListener() {
                                    boolean isWebViewVisible = false;

                                    @Override
                                    public void onClick(View v) {
//
                                        if (!isWebViewVisible) {
                                            // Chuyển sang hiển thị WebView
                                            binding.aa.setVisibility(View.GONE);
                                            webView.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.VISIBLE);
                                            binding.cvFile.setText("CV_App");

                                            String pdfUrl = fileCv;
                                            try {
                                                pdfUrl = URLEncoder.encode(pdfUrl, "UTF-8");
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                            String googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=" + pdfUrl;
                                            webView.loadUrl(googleDocsUrl);
                                            isWebViewVisible = true;
                                        } else {
                                            // Quay lại hiển thị aa
                                            webView.setVisibility(View.GONE);
                                            binding.aa.setVisibility(View.VISIBLE);
                                            binding.cvFile.setText("CV_PDF");

                                            isWebViewVisible = false;
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("Firestoraaaaaaaaaaaaaaaaaaaaaae", "Document không tồn tại cho jobId: ");
                        }
                    } else {
                        Log.e("Firestoraaaaaaaaaaaaaaaaaaaaaae", "Lỗi khi lấy dữ liệu Firestore", task.getException());
                    }
                });
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
                                        if (isAdded()) {
                                            // Hiển thị thông tin profile
                                            binding.born.setText(ngaysinh); // Hiển thị ngày sinh
                                            binding.born.setTextColor(getResources().getColor(R.color.black));// Hiển thị tên giới tính                                        } else {
                                            Log.e("FragmentError", "Fragment is not attached to a context.");
                                        }


                                        // Truy vấn bảng genders để lấy tên giới tính
                                        db.collection("genders").document(String.valueOf(gioitinh))
                                                .get()
                                                .addOnCompleteListener(genderTask -> {
                                                    if (genderTask.isSuccessful()) {
                                                        DocumentSnapshot genderDocument = genderTask.getResult();
                                                        if (genderDocument.exists()) {
                                                            String genderName = genderDocument.getString("name");
                                                            if (isAdded()) {
                                                                binding.gender.setText(genderName);
                                                                binding.gender.setTextColor(getResources().getColor(R.color.black));// Hiển thị tên giới tính                                                            } else {
                                                                Log.e("FragmentError", "Fragment is not attached to a context.");
                                                            }


                                                        } else {
                                                            Log.d("Firestore", "Không tìm thấy giới tính cho ID: " + gioitinh);
                                                        }
                                                    } else {
                                                        Log.e("Firestore", "Lỗi khi truy vấn bảng genders.", genderTask.getException());
                                                    }
                                                });

                                    } else {
                                        if (isAdded()) {
                                            binding.gender.setText("Chưa cập nhật");
                                            binding.gender.setTextColor(requireContext().getResources().getColor(R.color.chuacapnhat));
                                            binding.born.setText("Chưa cập nhật");
                                            binding.born.setTextColor(requireContext().getResources().getColor(R.color.chuacapnhat));
                                        } else {
                                            Log.e("FragmentError", "Fragment is not attached to a context. Cannot update UI.");
                                        }


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
                                            if (isAdded()) {
                                                binding.gioithieu.setText("Chưa cập nhật");
                                                binding.gioithieu.setTextColor(getResources().getColor(R.color.chuacapnhat));
                                            } else {
                                                Log.e("FragmentError", "Fragment is not attached to a context.");
                                            }

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
                                                kiNangs.add(new KiNang(null, null, "Chưa cập nhật kĩ năng", null, null));
                                                kiNangArrayAdapter.notifyDataSetChanged();
                                                //binding.lisviewHocVan.setVisibility(View.VISIBLE); // Hiển thị ListView
                                            } else {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    // Lấy dữ liệu từ document và chuyển vào đối tượng TruongHoc
                                                    String name = document.getString("name");


                                                    KiNang kiNang = new KiNang(null, null, name, null, null);

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
                                            Log.d("ii", "onComplete: lay dc anh vs uid" + id_candidate);
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

    private void configureWebView() {
        // Cấu hình WebView
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        // Set WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                binding.cvFile.setVisibility(View.VISIBLE);
            }
        });
    }
    String companyName = " ";
    String title = " ";
    private void getUserCompanyInfo() {
        db.collection("users")
                .document(uid)
                .collection("role")
                .document("employer") // Lấy dữ liệu từ document "employer"
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                         companyName = documentSnapshot.getString("companyName");

                    } else {
                        Log.d("Firebase", "Không tìm thấy dữ liệu công ty.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Lỗi lấy dữ liệu công ty.", e);
                });
    }

    private void getJobInfo() {
        db.collection("jobs")
                .document(id_Job)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        title = documentSnapshot.getString("title");
                    } else {
                        Log.d("Firebase", "Không tìm thấy dữ liệu công việc.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Lỗi lấy dữ liệu công việc.", e);
                });
    }
}
