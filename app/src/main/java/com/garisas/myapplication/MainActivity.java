package com.garisas.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton _addButton, _refreshButton;
    private RecyclerView _recyclerView;
    private TextView _txtMahasiswaCount, _txtSearch;
    private List<MahasiswaModel> mahasiswaModelList;
    private MahasiswaAdapter ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _recyclerView = findViewById(R.id.recyclerView1);
        _txtMahasiswaCount = findViewById(R.id.txtMahasiswaCount);

        initAddButton();
        loadRecylerView();
        initRefreshButton();
        initSearch();
    }

    private void initSearch() {
        _txtSearch = findViewById(R.id.txtSearch);
        _txtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String filterText = _txtSearch.getText().toString();
                if(!filterText.isEmpty()){
                    filter(filterText);
                }
                else{
                    loadRecylerView();

                }
                return false;
            }
        });
    }

    private void filter(String text){
        List<MahasiswaModel> filteredList = new ArrayList<>();

        for (MahasiswaModel item: mahasiswaModelList){
            if(item.getNama().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        if(filteredList.isEmpty()){
            Toast.makeText(MainActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
        }
        else {
            ma.filter(filteredList);
        }
    }

    private void initRefreshButton() {
        _refreshButton = findViewById(R.id.refreshButton);
        _refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRecylerView();
            }
        });
    }

    private void initAddButton() {
        _addButton = findViewById(R.id.addButton);

        _addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMahasiswaActivity.class);
                startActivity(intent);
                loadRecylerView();
            }
        });
    }

    private void loadRecylerView() {
        AsyncHttpClient ahc = new AsyncHttpClient();
        String url = "https://stmikpontianak.net/011100862/tampilMahasiswa.php";

        ahc.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson g = new Gson();
                mahasiswaModelList = g.fromJson(new String(responseBody), new TypeToken<List<MahasiswaModel>>(){}.getType());

                RecyclerView.LayoutManager lm = new LinearLayoutManager(MainActivity.this);
                _recyclerView.setLayoutManager(lm);

                ma = new MahasiswaAdapter(mahasiswaModelList);
                _recyclerView.setAdapter(ma);

                String mahasiswaCount = "Total mahasiswa : " +ma.getItemCount();
                _txtMahasiswaCount.setText(mahasiswaCount);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}