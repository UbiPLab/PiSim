package com.example.mygaode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mygaode.Object.Malicious;
import com.example.mygaode.Thread.GetMaliciousDriver;

import java.util.ArrayList;
import java.util.List;

public class MaliciousActivity extends AppCompatActivity {

    private List<Malicious> maliciousList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_malicious);
        GetMaliciousDriver getMaliciousDriver = new GetMaliciousDriver(maliciousList);
        getMaliciousDriver.start();
    }

    public void refresh(View view) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // 获取恶意用户数据
        GetMaliciousDriver getMaliciousDriver = new GetMaliciousDriver(maliciousList);
        getMaliciousDriver.start();
        MaliciousAdapter adapter = new MaliciousAdapter(maliciousList);
        recyclerView.setAdapter(adapter);
    }



    public class MaliciousAdapter extends RecyclerView.Adapter<MaliciousAdapter.ViewHolder> {
        private List<Malicious> mMaliciousList;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView idCar;
            TextView idNumber;

            public ViewHolder(View view) {
                super(view);
                idCar = (TextView) view.findViewById(R.id.malicious_idCar);
                idNumber = (TextView) view.findViewById(R.id.malicious_idNumber);
            }
        }

        public MaliciousAdapter(List<Malicious> maliciousList) {
            mMaliciousList = maliciousList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.malicious, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Malicious maliciousDriver = mMaliciousList.get(position);
            holder.idCar.setText(maliciousDriver.getIdCar());
            holder.idNumber.setText(maliciousDriver.getIdNumber());
        }

        @Override
        public int getItemCount() {
            return mMaliciousList.size();
        }
    }
}
