//A class for adding,Updating and removing travelling expense
//Automatic tracking can be launched from this class.

package com.example.mytax;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;




public class CarMain extends DrawerBarActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private String distance;
    private String startDate;
    private String origin;
    private String destination;
    private String purpose;
    private String amount;
    private Inflater infoCar;
    private FirebaseAuth mAuth;
    public DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatabaseReference mDatabase;
    private Toolbar toolbar;
    private Double k;
    private ImageButton btnImg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.car_rec_list, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);

//        setContentView(R.layout.car_rec_list);
        recyclerView = findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        toolbar=findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        toolbar.getMenu();
        infoCar = new Inflater();
        btnImg = findViewById(R.id.info);
        getSupportActionBar().setTitle("Trips");

        FirebaseUser mUser=mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("mainDb");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "No data Exists", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRecord();
            }
        });
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoCar.info(CarMain.this,R.layout.car_info);
            }
        });
    }
    public void updateData() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View myView = inflater.inflate(R.layout.activity_car_update, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        final EditText mdistance = myView.findViewById(R.id.update_distance);
        final EditText mstartDate = myView.findViewById(R.id.update_start_date);
        final EditText morigin = myView.findViewById(R.id.update_origin);
        final EditText mdestination = myView.findViewById(R.id.update_destination);
        final EditText mpurpose = myView.findViewById(R.id.update_purpose);
        final EditText mamount = myView.findViewById(R.id.update_amount);

           //set the toolbar title
        final  Toolbar toolbar = myView.findViewById(R.id.toolbar_close);
        toolbar.setTitle("Update Your data");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitleMargin(100,0,0,0);
        ((TextView)toolbar.getChildAt(1)).setTextSize(17);

        //disable
        mamount.setKeyListener(null);

        mdistance.setText(distance);
        mdistance.setSelection(distance.length());

        mstartDate.setText(startDate);
        mstartDate.setSelection(startDate.length());


        morigin.setText(origin);
        morigin.setSelection(origin.length());

        mdestination.setText(destination);
        mdestination.setSelection(destination.length());

        mpurpose.setText(purpose);
        mpurpose.setSelection(purpose.length());

        mamount.setText(amount);
        mamount.setSelection(amount.length());

        ImageButton imgBtn =myView.findViewById(R.id.close);
        Button btnDelete = myView.findViewById(R.id.btnDelete);
        Button btnUpdate = myView.findViewById(R.id.btnUpdate);

        mdistance.addTextChangedListener(new TextWatcher() {


            public void onTextChanged(CharSequence c, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
                // Calculate on Text change with appropriate conditions
                if((mdistance.getText().toString()).isEmpty()){

                    mdistance.setError("Empty");
                }
                else if ( Double.parseDouble(mdistance.getText().toString())< 5 || mdistance.getText().toString().isEmpty()
                        ||mstartDate.getText().toString().isEmpty()) {
                    mdistance.setError("Please provide all the inputs or your distance is less than 5 Km");
                    //Toast.makeText(getApplicationContext(), "Please provide all the inputs or your distance is less than 5 Km", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    k = Double.parseDouble(mdistance.getText().toString());
                    Double d = k * 1.85;
                    String mam = String.format("%.2f", d);
                    mamount.setText(" " + mam );

                }
            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser mUser=mAuth.getCurrentUser();
                String uid=mUser.getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("mainDb");

                distance = mdistance.getText().toString().trim();
                startDate = mstartDate.getText().toString().trim();
                origin = morigin.getText().toString().trim();
                destination = mdestination.getText().toString().trim();
                purpose = mpurpose.getText().toString().trim();
                amount = mamount.getText().toString().trim();

                if (startDate.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please provide all the inputs", Toast.LENGTH_SHORT).show();
                    return;
                }

                Car car = new Car(distance, startDate, origin,destination, purpose, amount);
                mDatabase.child(uid).child("cardb").child(car.getStartDate()).setValue(car);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser mUser=mAuth.getCurrentUser();
                String uid=mUser.getUid();
                mDatabase.child(uid).child("cardb").child(startDate).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void submitRecord() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View myView = inflater.inflate(R.layout.activity_car_add, null);
        myDialog.setView(myView);
        final AlertDialog dialog = myDialog.create();

        dialog.setCancelable(false);

        final EditText distance = myView.findViewById(R.id.edit_text_distance);
        final TextView startDate = myView.findViewById(R.id.text_view_startDate);
        final EditText origin = myView.findViewById(R.id.edit_text_origin);
        final EditText destination = myView.findViewById(R.id.edit_text_destination);
        final EditText purpose = myView.findViewById(R.id.edit_text_purpose);
        final EditText amount = myView.findViewById(R.id.edit_text_amount);
        final Button gps = myView.findViewById(R.id.btn_gps);
        amount.setKeyListener(null);
        final Button btnCancel = myView.findViewById(R.id.btnCancel);
        final Button btnAdd = myView.findViewById(R.id.btnSave);


        distance.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence c, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {

            }

            public void afterTextChanged(Editable c) {
                if((distance.getText().toString()).isEmpty()){

                    distance.setError("Empty");
                }
                else if ( Double.parseDouble(distance.getText().toString()) < 5 || distance.getText().toString().isEmpty()
                        ||startDate.getText().toString().isEmpty()) {
                    distance.setError("Please provide all the inputs or your distance is less than 5 Km");

                    return;
                }else{
                    k = Double.parseDouble(distance.getText().toString());
                    Double d = (k * 1.85);
                    String mam = String.format("%.2f", d);
                    amount.setText(" " + mam );
                }
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog2 = new DatePickerDialog(
                        CarMain.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);

                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();


            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + " " + day + " " + year;
                startDate.setText(date);
            }
        };

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarMain.this,AutoCarActivity.class);
                startActivity(intent);
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String mDistance = distance.getText().toString().trim();
                String mStartDate = startDate.getText().toString().trim();
                String mOrgin = origin.getText().toString().trim();
                String mDestination = destination.getText().toString().trim();
                String mPurpose = purpose.getText().toString().trim();
                String mAmount = amount.getText().toString().trim();

                if(mDistance.isEmpty()|| mStartDate.startsWith("S")){
                    Toast.makeText(getApplicationContext(), "Please provide all the inputs", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Date formatter from salalry class
                String sDate = Salary.dateFormatter(mStartDate);

                FirebaseUser mUser=mAuth.getCurrentUser();
                String uid=mUser.getUid();

                Car car = new Car(mDistance, sDate,  mOrgin, mDestination, mPurpose, mAmount);
                mDatabase.child(uid).child("cardb").child(car.getStartDate()).setValue(car);
                Toast.makeText(getApplicationContext(), "Record added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    //Fetch data from database and display it in recycler view
    private void fetch() {
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child("mainDb").child(uid).child("cardb");

        FirebaseRecyclerOptions<Car> options =
                new FirebaseRecyclerOptions.Builder<Car>()
                        .setQuery(query, new SnapshotParser<Car>() {
                            @NonNull
                            @Override
                            public Car parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Car(snapshot.child("distance").getValue().toString(),
                                        snapshot.child("startDate").getValue().toString(),
                                        snapshot.child("origin").getValue().toString(),
                                        snapshot.child("destination").getValue().toString(),
                                        snapshot.child("purpose").getValue().toString(),
                                        snapshot.child("amount").getValue().toString());
                            }
                        })
                        .build();

        //To enable on swipe to delete a record
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

             @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                new AlertDialog.Builder(CarMain.this)

                .setMessage("Are you sure you want to delete this?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        Toast.makeText(CarMain.this, "Deleted ", Toast.LENGTH_SHORT).show();
                        final int position = viewHolder.getAdapterPosition();
                        adapter.getRef(position).removeValue();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        dialog.cancel();
                    };
                }).show();

            }
        };

        ItemTouchHelper it = new ItemTouchHelper(simpleItemTouchCallback);
        it.attachToRecyclerView(recyclerView);


        adapter = new FirebaseRecyclerAdapter<Car, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.car_item, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder viewHolder, final int position, final Car model) {
                viewHolder.setDistance(model.getDistance());
                viewHolder.setStartDate(model.getStartDate());
                viewHolder.setOrigin(model.getOrigin());
                viewHolder.setDestination(model.getDestination());
                viewHolder.setPurpose(model.getPurpose());
                viewHolder.setAmount(model.getAmount());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        distance = model.getDistance();
                        startDate = model.getStartDate();
                        origin=model.getOrigin();
                        destination =model.getDestination();
                        purpose=model.getPurpose();
                        amount=model.getAmount();
                        updateData();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetch();
        adapter.startListening();
    }

    //View Holder class for Our recycler view

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView distance;
        public TextView startDate;
        public TextView origin;
        public TextView destination;
        public TextView purpose;
        public TextView amount;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDistance(String string) {
            distance = mView.findViewById(R.id.text_view_distance);
            distance.setText(string);
        }

        public void setStartDate(String string) {
            startDate = mView.findViewById(R.id.text_view_startDate);
            startDate.setText(string);
        }

        public void setOrigin(String string) {
            origin = mView.findViewById(R.id.text_view_origin);
            origin.setText(string);
        }

        public void setDestination(String string) {
            destination = mView.findViewById(R.id.text_view_destination);
            destination.setText(string);
        }

        public void setPurpose(String string) {
            purpose = mView.findViewById(R.id.text_view_purpose);
            purpose.setText(string);
        }
        public void setAmount (String string) {
            amount = mView.findViewById(R.id.text_view_amount);
            amount.setText(string);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
