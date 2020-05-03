
//Fetches Muncipality data from api and compare the percentage from Salary anc check if the
//User is paying the right percentage


package com.example.mytax;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Percentage extends DrawerBarActivity {
    private TextView list;
    private TextView result;
    private EditText grossSalary;
    private EditText netSalary;
    private Button btnCalculate;
    private Spinner spinner;
    private TextView kommuneName;
    private Inflater info;
    private Toolbar toolbar;
    private ImageButton btnImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_percentage, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(4).setChecked(true);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        btnImg = findViewById(R.id.info);
        getSupportActionBar().setTitle("Percentage Check");

        list = (TextView) findViewById(R.id.resultFromDd);
        kommuneName = findViewById(R.id.kommuneValue);
        spinner = (Spinner) findViewById(R.id.spinner);
        result = (TextView) findViewById(R.id.result);
        grossSalary = (EditText) findViewById(R.id.grossSalary);
        netSalary =(EditText)  findViewById(R.id.netSalary);
        btnCalculate = findViewById(R.id.btnCalculate);
        info = new Inflater();

        //Populate the dropdown menu from APi
        Retrofit retro = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Api api = retro.create(Api.class);

        final Call<List<Kommune>> call = api.getKommune();

        call.enqueue(new Callback<List<Kommune>>() {
            @Override
            public void onResponse(Call<List<Kommune>> call, Response<List<Kommune>> response) {
                final List<Kommune> kommunes = response.body();
                ArrayAdapter<Kommune> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, kommunes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        list.setText(kommunes.get(position).getLocalTaxRate());
                        kommuneName.setText(kommunes.get(position).getMunicipality());

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Kommune>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage() + "make sure You are connected to internet", Toast.LENGTH_SHORT).show();

            }
        });

        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.info(Percentage.this,R.layout.percentage_info);
            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(grossSalary.getText().toString().isEmpty() || netSalary.getText().toString().isEmpty()){
                    grossSalary.setError("Enter All the fields");
                    netSalary.setError("Enter All the fields");
                    return;
                }
                else{
                    calculatePercentage();
                }
            }
        });
    }

    public void calculatePercentage() {
        Double grossSal = Double.parseDouble(grossSalary.getText().toString());
        Double netSal = Double.parseDouble(netSalary.getText().toString());
        double percentage = (netSal/grossSal)*100;
        String resul = String.format("%.2f", percentage);
        result.setText(resul);

        if(percentage < Double.parseDouble(list.getText().toString())){

            result.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else{
            result.setTextColor(getResources().getColor(R.color.colorA));
        }

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}

