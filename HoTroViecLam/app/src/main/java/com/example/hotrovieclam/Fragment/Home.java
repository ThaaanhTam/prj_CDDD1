package com.example.hotrovieclam.Fragment;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.example.hotrovieclam.R;

import java.text.Normalizer;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotrovieclam.Activity.JobDetailMain;
import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;
import com.example.hotrovieclam.Connect.API;
import com.example.hotrovieclam.Connect.Website;
import com.example.hotrovieclam.Model.Job;

import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Home extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Job> listJob;
    private MyRecyclerViewAdapter adapter;


    private ArrayList<String> arrayList;
    private TextView textViewSpinner;
    private Dialog dialog;
    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        funCustomSpinner();
        // funCustomSalaryDialog();
        listJob = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(getActivity(), listJob);
        binding.sourceSpinner.setSelection(3);
        binding.line1.setVisibility(View.GONE);
        binding.jobList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.jobList.setAdapter(adapter);
        fetchJobsFromFirestore();
        Website websiteLoader = new Website();
        websiteLoader.loadWebsitesConcurrentlySequentially(adapter, listJob);
        API apiLoader = new API();
        apiLoader.loadAPIsConcurrently(adapter, listJob);



        binding.sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ii", "onItemSelected: " + position);
                ArrayList<Job> filteredJobs = new ArrayList<>();

                switch (position) {
                    case 0:
                        for (Job job : listJob) {
                            if (job.getSourceId() == 1) {
                                filteredJobs.add(job);
                            }
                        }
                        break;
                    case 1:
                        for (Job job : listJob) {
                            if (job.getSourceId() == 2) {
                                filteredJobs.add(job);
                            }
                        }
                        break;
                    case 2:
                        for (Job job : listJob) {
                            if (job.getSourceId() == 3) {
                                filteredJobs.add(job);
                            }
                        }
                        break;
                    default:
                        filteredJobs.addAll(listJob);
                        break;

                }

                adapter = new MyRecyclerViewAdapter(getActivity(), filteredJobs);
                binding.jobList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                setRecycleClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        binding.btnTim.setOnClickListener(v -> {

            binding.line1.setVisibility(View.VISIBLE);
            String searchText = binding.searchBar.getText().toString();
            Log.d("SearchInput", "Search text: " + searchText);
            adapter = new MyRecyclerViewAdapter(getActivity(), performSearch(searchText));
            binding.jobList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            setRecycleClick();

        });
        setRecycleClick();
        adapter.updateList(listJob);

    }
    private void setRecycleClick(){
        adapter.setRecycleClick(new MyRecyclerViewAdapter.OnItemClick() {
            @Override
            public void DetailClick(String SourceID, String jobID,Job job) {
                //     Log.d("Click", "DetailClick: " + "jodID" + jobID);
                //Detail_Job detailJob = new Detail_Job();
                Intent intent = new Intent(getContext(), JobDetailMain.class);

                intent.putExtra("jobID", jobID);
                intent.putExtra("sourceId", job.getSourceId());

                Log.d(TAG, "DetailClick: "+job);
                intent.putExtra("KEY_NAME", job);
                startActivity(intent);
            }
        });
    }

    private ArrayList<Job> performSearch(String query) {
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            return new ArrayList<>(listJob); // Return a new list with all jobs
        }

        ArrayList<Job> filteredList = new ArrayList<>();
        for (Job job : listJob) {
            if (job.getTitle().toUpperCase().contains(query.toUpperCase())) {
                filteredList.add(job);
            }
        }

        return filteredList;
    }

    private void fetchJobsFromFirestore() {
        db.collection("jobs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Job job = document.toObject(Job.class);
                    job.setSourceId(3);
                    //        Log.d("oo", job.getId());
                    listJob.add(job);
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.w("HomeFragment", "Error getting documents.", task.getException());
            }
        });
    }

    public void funCustomSpinner() {
        // Thêm giá trị vào arrayList
        String[] provincesArray = getResources().getStringArray(R.array.provinces);
        for (String province : provincesArray) {
            arrayList.add(province);
        }

        // Tách biệt biến cho location
        TextView textViewLocation = binding.location;
        textViewLocation.setOnClickListener(view -> {
            dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.layout_search_location);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.show();

            EditText editText = dialog.findViewById(R.id.editText_of_searchableSpinner);
            ListView listView = dialog.findViewById(R.id.listView_of_searchLocation);
            Button searchButton = dialog.findViewById(R.id.timpn); // Lấy nút Tìm

            // Khởi tạo ArrayAdapter với danh sách hiện có
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, arrayList);
            listView.setAdapter(arrayAdapter);

            // Thiết lập sự kiện click cho nút Tìm
            searchButton.setOnClickListener(v -> {
                String searchText = editText.getText().toString().trim();
                String searchTextNoDiacritics = removeDiacritics(searchText); // Chuyển searchText sang không dấu
                textViewLocation.setText(searchText); // Hiển thị nội dung của EditText lên TextView location
                arrayAdapter.getFilter().filter(searchText);
                Toast.makeText(getActivity(), "Selected: " + searchText, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                // Lọc danh sách công việc dựa trên searchText
                ArrayList<Job> filteredJobs = filter(searchTextNoDiacritics);
                updateRecyclerView(filteredJobs); // Cập nhật RecyclerView
            });

            // Tạo TextWatcher để tự động lọc khi gõ vào EditText
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    arrayAdapter.getFilter().filter(charSequence); // Lọc danh sách theo từng ký tự gõ
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });


            // Xử lý sự kiện khi chọn một mục trong ListView
            listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                String selectedItem = arrayAdapter.getItem(i);
                textViewLocation.setText(selectedItem); // Cập nhật TextView location bằng mục đã chọn
                Toast.makeText(getActivity(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                ArrayList<Job> filteredJobs = filter(selectedItem);
                updateRecyclerView(filteredJobs); // Đóng dialog
            });


        });

        ArrayList<String> aw = new ArrayList<>();
        String[] a = getResources().getStringArray(R.array.salary);
        for (String province : a) {
            aw.add(province);
        }
        TextView textViewSalary = binding.luong;
        textViewSalary.setOnClickListener(view -> {
            dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.layout_search_salary); // Layout cho khoảng lương
            ListView aa = dialog.findViewById(R.id.listView_of_searchableSalary);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.show();

            EditText minSalaryEditText = dialog.findViewById(R.id.salaryMin);
            EditText maxSalaryEditText = dialog.findViewById(R.id.salaryMax);
            Button submitButton = dialog.findViewById(R.id.timLuong);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, aw);
            aa.setAdapter(arrayAdapter);


            // Thiết lập sự kiện click cho nút Submit
            submitButton.setOnClickListener(v -> {
                String minSalary = minSalaryEditText.getText().toString().trim();
                String maxSalary = maxSalaryEditText.getText().toString().trim();

                if (!minSalary.isEmpty() && !maxSalary.isEmpty()) {
                    textViewSalary.setText(minSalary + " - " + maxSalary); // Hiển thị khoảng lương đã chọn
                    Toast.makeText(getActivity(), "Đã chọn: " + minSalary + " - " + maxSalary, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    ArrayList<Job> filteredJobs = filterBySalaryRange(minSalary, maxSalary);

                    updateRecyclerView(filteredJobs);
                } else {
                    Toast.makeText(getActivity(), "Vui lòng nhập cả khoảng lương tối thiểu và tối đa.", Toast.LENGTH_SHORT).show();
                }
            });

            aa.setOnItemClickListener((adapterView, view1, i, l) -> {
                String selectedItem = arrayAdapter.getItem(i);
                textViewSalary.setText(selectedItem); // Cập nhật TextView location bằng mục đã chọn
                Toast.makeText(getActivity(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                if (selectedItem.equals("Dưới 10 triệu")) {
                    ArrayList<Job> filteredJobs = filterBySalaryRange("0", "10");
                    updateRecyclerView(filteredJobs);
                    dialog.dismiss();
                } else if (selectedItem.equals("Trên 50 triệu")) {
                    ArrayList<Job> filteredJobs = new ArrayList<>();
                    for (Job jobL : listJob) {
                        if (jobL.getSalaryMin() > 50) {
                            filteredJobs.add(jobL);
                        }
                    }
                    updateRecyclerView(filteredJobs);
                    dialog.dismiss();
                } else if (selectedItem.equals("Thỏa thuận")) {
                    ArrayList<Job> filteredJobs = new ArrayList<>();
                    for (Job jobL : listJob) {
                        if ((jobL.getSalaryMin() == jobL.getSalaryMax()) && jobL.getAgreement() != null) {
                            filteredJobs.add(jobL);
                        }
                    }
                    updateRecyclerView(filteredJobs);
                    dialog.dismiss();
                } else {
                    String jobSalary = selectedItem;
                    int[] salaryRange = parseSalaryRange(jobSalary);
                    ArrayList<Job> filteredJobs = filterBySalaryRange(salaryRange[0] + "", salaryRange[1] + "");
                    updateRecyclerView(filteredJobs);
                    dialog.dismiss();// Đóng dialog
                }

            });



        });

        ArrayList<String> major = new ArrayList<>();
        String[] inforMajor = getResources().getStringArray(R.array.job_industries);
        for (String province : provincesArray) {
            major.add(province);
        }

        TextView textViewMajor = binding.major;
        textViewMajor.setOnClickListener(view -> {
            dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.layout_search_major);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.show();

            EditText editText = dialog.findViewById(R.id.editText_of_searchMajor);
            ListView listView = dialog.findViewById(R.id.listView_of_searchMajor);
            Button searchButton = dialog.findViewById(R.id.btnMajor); // Lấy nút Tìm
            // Khởi tạo ArrayAdapter với danh sách hiện có
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, inforMajor);
            listView.setAdapter(arrayAdapter);
            searchButton.setOnClickListener(v -> {
                String searchText = editText.getText().toString().trim();
                String searchTextNoDiacritics = removeDiacritics(searchText); // Chuyển searchText sang không dấu
                textViewMajor.setText(searchText); // Hiển thị nội dung của EditText lên TextView location
                arrayAdapter.getFilter().filter(searchText);
                Toast.makeText(getActivity(), "Selected: " + searchText, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                // Lọc danh sách công việc dựa trên searchText
                ArrayList<Job> filteredJobs = filterMajor(searchTextNoDiacritics);
                updateRecyclerView(filteredJobs); // Cập nhật RecyclerView
            });
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    arrayAdapter.getFilter().filter(charSequence); // Lọc danh sách theo từng ký tự gõ
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });


            // Xử lý sự kiện khi chọn một mục trong ListView
            listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                String selectedItem = arrayAdapter.getItem(i);
                textViewMajor.setText(selectedItem); // Cập nhật TextView location bằng mục đã chọn
                Toast.makeText(getActivity(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                ArrayList<Job> filteredJobs = filterMajor(selectedItem);
                updateRecyclerView(filteredJobs); // Đóng dialog
            });
        });
    }

    private int[] parseSalaryRange(String salaryText) {
        try {
            String[] salaryParts = salaryText.split("-");
            if (salaryParts.length == 2) {
                int salaryMin = Integer.parseInt(salaryParts[0].replaceAll("[^\\d]", "").trim());
                int salaryMax = Integer.parseInt(salaryParts[1].replaceAll("[^\\d]", "").trim());
                return new int[]{salaryMin, salaryMax};
            }

        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing salary range: " + e.getMessage());
        }
        return null;
    }

    private ArrayList<Job> filterBySalaryRange(String minSalary, String maxSalary) {
        ArrayList<Job> filteredList = new ArrayList<>();

        try {
            float min = Float.parseFloat(minSalary);
            float max = Float.parseFloat(maxSalary);

            for (Job job : listJob) {
                if (job.getSalaryMax() != -1.0f || job.getSalaryMin() != 1.0f) {
                    if (job.getSalaryMin() >= min && job.getSalaryMax() <= max && job.getSalaryMin() != job.getSalaryMax()) {
                        filteredList.add(job);
                    }
                }
            }

        } catch (NumberFormatException e) {
            Log.e("SalaryFilter", "Nhập lương không hợp lệ", e);
        }

        return filteredList;
    }


    // Hàm loại bỏ dấu
    public static String removeDiacritics(String input) {
        return input == null ? null :
                Normalizer.normalize(input, Normalizer.Form.NFD)
                        .replaceAll("\\p{M}", "");
    }

    private ArrayList<Job> filter(String location) {
        ArrayList<Job> filteredList = new ArrayList<>();

        ArrayList<Job> jobListCopy = new ArrayList<>(listJob);

        for (Job job : jobListCopy) {
            if (job.getLocation() != null) {
                String locationNoDiacritics = removeDiacritics(job.getLocation());
                if (locationNoDiacritics != null && locationNoDiacritics.toUpperCase().contains(removeDiacritics(location.toUpperCase()))) {
                    filteredList.add(job);
                    Log.d("locaaa", "Công việc phù hợp: " + job.getLocation());
                }
            }
        }
        return filteredList;
    }


    private ArrayList<Job> filterMajor(String major) {
        ArrayList<Job> filteredList = new ArrayList<>();


        for (Job job : listJob) {
            if (job.getMajor() != null) {
                String majorNoDiacritics = removeDiacritics(job.getMajor());

                if (majorNoDiacritics != null && majorNoDiacritics.toUpperCase().contains(removeDiacritics(major.toUpperCase()))) {
                    filteredList.add(job);
                    // Ghi log công việc phù hợp
                    Log.d("locaaa", "Công việc phù hợp: " + job.getMajor());
                }
            }
        }
        // Trả về danh sách các công việc đã lọc
        return filteredList;
    }


    // Hàm cập nhật RecyclerView
    private void updateRecyclerView(ArrayList<Job> filteredJobs) {
         adapter = new MyRecyclerViewAdapter(getActivity(), filteredJobs);
        // for (Job a : filteredJobs) {
//            Log.d("luong hien thi", "Thoa thuan: " + a.getAgreement() + "\n max: " + a.getSalaryMax() + "\n min: " + a.getSalaryMin());
//            Log.d("nganh",a.getMajor());
        // }
        binding.jobList.setAdapter(adapter);
        adapter.notifyDataSetChanged(); // Cập nhật adapter với danh sách mới
        setRecycleClick();
    }
}

