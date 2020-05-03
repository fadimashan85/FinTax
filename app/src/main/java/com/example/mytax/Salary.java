/*Adding Salary details for the specified companies including getting data from Api for
    appropriate tax percentage depending on the Muncipality that person is and add, update and delete records.
    this will help to inform the user where he is paying the percentage specified or not
 */

package com.example.mytax;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Salary extends DrawerBarActivity implements AdapterView.OnItemSelectedListener{
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private String companyName;
    private String salary;
    private Toolbar toolbar;
    private String expectedTax;
    private String actualTax;
    private String date;
    private Inflater info;
    private ImageButton btnImg;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    public DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout =  findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.rec_list, contentFrameLayout);
        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Salary details");
        btnImg = findViewById(R.id.info);
        recyclerView = findViewById(R.id.list);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("mainDb");
        mDatabase.keepSynced(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid=mUser.getUid();
        info=new Inflater();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("mainDb");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "No data Exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               info.info(Salary.this,R.layout.salary_info);
            }
        });



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRecord();
            }
        });
    }

    public void updateData() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View myView = inflater.inflate(R.layout.activity_update, null);
        myDialog.setView(myView);
        final AlertDialog dialog = myDialog.create();

        final EditText mCompanyName = myView.findViewById(R.id.company_name);
        final EditText mSalary = myView.findViewById(R.id.salary);
        final EditText mExpectedTax = myView.findViewById(R.id.expected_tax);
        final EditText mActualTax = myView.findViewById(R.id.actual_tax);
        final EditText mDate = myView.findViewById(R.id.date);

        //set the toolbar title
        final  Toolbar toolbar = myView.findViewById(R.id.toolbar_close);
        toolbar.setTitle("Update Your data");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitleMargin(100,0,0,0);
        ((TextView)toolbar.getChildAt(1)).setTextSize(17);

        mCompanyName.setText(companyName);
        mCompanyName.setSelection(companyName.length());

        mSalary.setText(salary);
        mSalary.setSelection(salary.length());

        mExpectedTax.setText(expectedTax);
        mExpectedTax.setSelection(expectedTax.length());

        mActualTax.setText(actualTax);
        mActualTax.setSelection(actualTax.length());

        mDate.setText(date);
        mDate.setSelection(date.length());


        Button btnDelete = myView.findViewById(R.id.btnDelete);
        Button btnUpdate = myView.findViewById(R.id.btnUpdate);
        ImageButton imgBtn = myView.findViewById(R.id.close);

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
                companyName = mCompanyName.getText().toString().trim();
                salary = mSalary.getText().toString().trim();
                expectedTax = mExpectedTax.getText().toString().trim();
                actualTax = mActualTax.getText().toString().trim();
                date = mDate.getText().toString().trim();


                if (companyName.trim().isEmpty() || actualTax.trim().isEmpty() || expectedTax.trim().isEmpty() || salary.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please provide all the inputs", Toast.LENGTH_SHORT).show();
                    return;
                }
                Company company = new Company(companyName, salary, expectedTax, actualTax,date);
                mDatabase.child(uid).child("salary").child(company.getDate()).setValue(company);

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser mUser=mAuth.getCurrentUser();
                String uid=mUser.getUid();
                mDatabase.child(uid).child("salary").child(date).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void submitRecord() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View myView = inflater.inflate(R.layout.activity_add, null);
        myDialog.setView(myView);
        final AlertDialog dialog = myDialog.create();

        dialog.setCancelable(false);

        final TextView list = myView.findViewById(R.id.kommune_percent);
        final Spinner spinner = myView.findViewById(R.id.spinner);

        final EditText companyName = myView.findViewById(R.id.company_name);
        final EditText salary = myView.findViewById(R.id.salary);
        final EditText actualTax = myView.findViewById(R.id.actual_tax);


        Button btnCancel = myView.findViewById(R.id.btnCancel);
        Button btnAdd = myView.findViewById(R.id.btnSave);
        final TextView mDisplayDate = myView.findViewById(R.id.date);

        final TypedArray percent = getResources().obtainTypedArray(R.array.percentage);
        Spinner coloredSpinner = findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                Salary.this,
                R.array.commune,
                R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                list.setText(percent.getString(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Salary.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + " " + day + " " + year;
                mDisplayDate.setText(date);
            }
        };

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String mDate = mDisplayDate.getText().toString().trim();
                String mCompanyName = companyName.getText().toString().trim();
                String mSalary = salary.getText().toString().trim();
                String mActualTax = actualTax.getText().toString().trim();

                if (mCompanyName.trim().isEmpty() || mActualTax.trim().isEmpty() || mSalary.trim().isEmpty()||mDate.trim().isEmpty()||list.getText().toString().equals("0.0")) {
                    Toast.makeText(getApplicationContext(), "Please provide all the inputs", Toast.LENGTH_SHORT).show();
                    return;
                }


               String formattedDate = dateFormatter(mDate);


                double num1 = Double.parseDouble(list.getText().toString());
                double num2 = Double.parseDouble(salary.getText().toString());
                double num3 = num1 * num2 / 100;
                String mExpectedTax = String.format("%.2f", num3);
                FirebaseUser mUser=mAuth.getCurrentUser();
                String uid=mUser.getUid();

                Company company = new Company(mCompanyName, mSalary, mExpectedTax, mActualTax, formattedDate);
                mDatabase.child(uid).child("salary").child(company.getDate()).setValue(company);
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



    public static String dateFormatter(String mDate){
        SimpleDateFormat parser = new SimpleDateFormat("M d yyyy");
        Date date = null;
        try {
            date = parser.parse(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        return  formatter.format(date);
    }


    private void fetch() {
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid=mUser.getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child("mainDb").child(uid).child("salary");

        FirebaseRecyclerOptions<Company> options =
                new FirebaseRecyclerOptions.Builder<Company>()
                        .setQuery(query, new SnapshotParser<Company>() {
                            @NonNull
                            @Override
                            public Company parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Company(snapshot.child("companyName").getValue().toString(),
                                        snapshot.child("salary").getValue().toString(),
                                        snapshot.child("expectedTax").getValue().toString(),
                                        snapshot.child("actualTax").getValue().toString(),
                                        snapshot.child("date").getValue().toString());
                            }
                        })
                        .build();


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                new AlertDialog.Builder(Salary.this)

                .setMessage("Are you sure you want to delete this?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        Toast.makeText(Salary.this, "Deleted ", Toast.LENGTH_SHORT).show();
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

        adapter = new FirebaseRecyclerAdapter<Company, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(Salary.this)
                        .inflate(R.layout.to_be_inflated, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder viewHolder, final int position, final Company model) {
                viewHolder.setCompanyName( model.getCompanyName());
                viewHolder.setSalary     ( model.getSalary()+" kr");
                viewHolder.setActualTax  (model.getActualTax() + " kr");
                viewHolder.setExpectedTax(model.getExpectedTax()+" kr");
                viewHolder.setDate( model.getDate());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        companyName = model.getCompanyName();
                        salary = model.getSalary();
                        expectedTax = model.getExpectedTax();
                        actualTax = model.getActualTax();
                        date=model.getDate();
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this,adapterView.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView companyName;
        public TextView salary;
        public TextView expectedTax;
        public TextView actualTax;
        public TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setSalary(String string) {
            salary = mView.findViewById(R.id.salary);
            salary.setText(string);
        }

        public void setExpectedTax(String string) {
            expectedTax = mView.findViewById(R.id.expected_tax);
            expectedTax.setText(string);
        }

        public void setActualTax(String string) {
            actualTax = mView.findViewById(R.id.actual_tax);
            actualTax.setText(string);
        }

        public void setCompanyName(String string) {
            companyName = mView.findViewById(R.id.company_name);
            companyName.setText(string);
        }

        public void setDate(String string) {
            date = mView.findViewById(R.id.date);
            date.setText(string);
        }

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

}


