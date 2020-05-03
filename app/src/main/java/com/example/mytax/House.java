package com.example.mytax;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.widget.FrameLayout;


public class House extends DrawerBarActivity {
    private ImageButton btnImg;
    int buy;
    int sell;
    int broker;
    int list;
    int profit;
    int spentM;

    EditText buyPrice;
    EditText sellPrice;
    EditText brokerPrice;
    EditText listPrice;
    TextView profitMade;
    TextView moneySpent;
    Toolbar toolbar;
    private Inflater info;

    Button tbdButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_house, contentFrameLayout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        info = new Inflater();
        btnImg = findViewById(R.id.info);
        getSupportActionBar().setTitle("House");
        buyPrice =  findViewById(R.id.bought);
        sellPrice =  findViewById(R.id.sold);
        brokerPrice =  findViewById(R.id.broker);
        listPrice = findViewById(R.id.listing);
        profitMade = findViewById(R.id.profit);
        moneySpent = findViewById(R.id.spentMoney);


        tbdButton = findViewById(R.id.Btn_Calculate);

        tbdButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buy = Integer.parseInt(buyPrice.getText().toString());
                sell = Integer.parseInt(sellPrice.getText().toString());
                broker = Integer.parseInt(brokerPrice.getText().toString());
                list = Integer.parseInt(listPrice.getText().toString());

                profit = (sell - (buy + broker + list));

                spentM = buy+broker+list;


                profitMade.setText(String.valueOf(profit));
                moneySpent.setText(String.valueOf(spentM));

                showToast(String.valueOf("Profit to declare \n" + profit));
            }
        });
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              info.info(House.this,R.layout.house_info);
            }
        });
    }

    private void showToast(String text)
    {
        Toast.makeText(House.this, text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

}