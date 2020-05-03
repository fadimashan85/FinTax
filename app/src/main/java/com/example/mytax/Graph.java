package com.example.mytax;
import android.app.slice.Slice;
import android.app.slice.SliceItem;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import java.util.ArrayList;
import java.util.List;
public class Graph extends Fragment {
    //animation
    private View view;
    private RelativeLayout myLayout = null;
    private LinearLayout l1,l2;
    private Animation uptodown,downtoup;
    FirebaseDatabase database;
    DatabaseReference salaryRef;
    FirebaseAuth mAuth;
    ArrayList<PieEntry> yvalues;
    Float numVoto;


    //for ()
    ArrayList<String> array;
    String months[] = {"Jan", "Feb", "Mar", "Apr", "May"};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.welcome_activity, container, false);


        myLayout = view.findViewById(R.id.myLayout);
        yvalues=new ArrayList<PieEntry>();

        l1 = (LinearLayout) view.findViewById(R.id.l1);
        l2 = (LinearLayout) view.findViewById(R.id.l2);
        uptodown = AnimationUtils.loadAnimation(getActivity(),R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(getActivity(),R.anim.downtoup);
        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);
        database = FirebaseDatabase.getInstance();
        mAuth   = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();

        salaryRef  = database.getReference("mainDb").child(userId).child("salary");
        Log.e("userId? ", userId);
        salaryRef.addValueEventListener(new ValueEventListener() {
            // dataSnapshot.child()
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //array = new ArrayList<>();
                for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                    //  System.out.println(expenseSnapshot.child("date").getValue());
                    Log.e("Checking ", expenseSnapshot.getValue().toString());

                    numVoto = Float.parseFloat(String.valueOf(expenseSnapshot.child("actualTax").getValue()));
                    yvalues.add(new PieEntry(numVoto,String.valueOf(expenseSnapshot.child("date").getValue())));

                }
                setupChart();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }


    private void setupChart()
    {
        //Populating a list of pie entries
        /*List<PieEntry> pieEntries = new ArrayList<>();
        for(int i = 0; i < array.size(); i++)
        {
            pieEntries.add(new PieEntry(array[i], months[i]));
        }*/
        PieDataSet dataSet = new PieDataSet(yvalues, "");
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setSliceSpace(7f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.WHITE);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.60f);
        dataSet.setValueLinePart2Length(.1f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setSelectionShift(65);
        //Display the chart
        PieChart chart = view.findViewById(R.id.chart);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        chart.setData(data);
        chart.animateY(2800);
        chart.invalidate();
        chart.setTransparentCircleColor(Color.RED);
        chart.setMaxAngle(360);
        chart.setDrawCenterText(true);
        chart.setCenterText("Tax paid");
        chart.setHoleRadius(40);
        chart.setTransparentCircleRadius(0);
        chart.getDescription().setText("");
        chart.getDescription().setTextColor(Color.BLACK);
        chart.getDescription().setTextSize(8f);
        chart.setUsePercentValues(true);
        chart.setEntryLabelColor(Color.GRAY);
        chart.setEntryLabelTextSize(7f);
        //legend display
        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setDrawInside(false);
    }
}