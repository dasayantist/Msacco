package com.dasayantist.msacco.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dasayantist.msacco.Premium;
import com.dasayantist.msacco.R;
import com.dasayantist.msacco.model.Loan;

import java.util.ArrayList;



public class LoanListAdapter extends BaseAdapter {


    Context pContext;

//    final List<String> productsArrayList = new ArrayList<String>();
    ArrayList<Loan> temporaryArray;
    ArrayList<Loan> permanentArray;

    public LoanListAdapter(Context context, ArrayList<Loan> data) {
        this.pContext = context;
        this.temporaryArray = data;
        this.permanentArray = new ArrayList<>();
        this.permanentArray.addAll(data);
    }

    public void refresh(ArrayList<Loan> data){
        permanentArray.clear();
        permanentArray.addAll(data);
    }
    @Override
    public int getCount() {
        return temporaryArray.size();// # of items in your arraylist
    }

    @Override
    public Object getItem(int position) {
        return temporaryArray.get(position);// get the actual movie
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LoanListAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) pContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.loanlist, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.loan_type = convertView.findViewById(R.id.loan_type);
            viewHolder.loan_id = convertView.findViewById(R.id.loan_id);
            viewHolder.loan_principal = convertView.findViewById(R.id.loan_principal);
            viewHolder.loan_interest = convertView.findViewById(R.id.loan_interest);
            viewHolder.loan_time = convertView.findViewById(R.id.loan_time);
            viewHolder.loan_penalty = convertView.findViewById(R.id.loan_penalty);
            viewHolder.loan_total = convertView.findViewById(R.id.loan_total);
            viewHolder.imgCall = convertView.findViewById(R.id.imgCall);
            viewHolder.imgSMS = convertView.findViewById(R.id.imgSMS);
           // viewHolder.imgLocation = (ImageView) convertView.findViewById(R.id.imgLocation);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Loan loan = temporaryArray.get(position);
        viewHolder.loan_type.setText(loan.getLoan_type());
        viewHolder.loan_id.setText(loan.getLoan_id());
        viewHolder.loan_principal.setText(loan.getPrincipal());
        viewHolder.loan_interest.setText(loan.getInterest());
        viewHolder.loan_time.setText(loan.getS_time());
        viewHolder.loan_penalty.setText(loan.getPenalty());
        viewHolder.loan_total.setText(loan.getLoan_amount());

        viewHolder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loan_bal = loan.getPrincipal();
                String loan_id = loan.getLoan_id();
                String penalty = loan.getPenalty();
                String interest = loan.getInterest();
                String total = loan.getLoan_amount();
                Intent intent= new Intent(pContext, Premium.class);
               // intent.putextra("your_extra","your_class_value");
                intent.putExtra("loan_bal", loan_bal);
                intent.putExtra("loan_id", loan_id);
                intent.putExtra("penalty", penalty);
                intent.putExtra("interest", interest);
                intent.putExtra("total", total);
                pContext.startActivity(intent);
//                Intent n = new Intent(v.getContext(), Premium.class);
//                //startActivityForResult(n);
//                startActivity(n);
//                AlertDialog.Builder dialog=new AlertDialog.Builder(pContext);
//                dialog.setMessage("Do you really want to call?");
//                dialog.setTitle("Call?");
//                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String phone = loan.getPhone();
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
//                        if (ActivityCompat.checkSelfPermission(pContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            return;
//                        }
//                        pContext.startActivity(intent);
//                    }
//                });
//                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();

            }
        });

        viewHolder.imgSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = loan.getPrincipal();
                //pContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));
            }
        });
        return convertView;
    }

    public void filter(String text){
        text=text.toLowerCase();
        temporaryArray.clear();

        if(text.trim().length()==0) {
            temporaryArray.addAll(permanentArray);
        }
        else {
            Log.d("SEARCH", "perma_array: "+permanentArray.size());
            for (Loan p:permanentArray) {//p.getName().toLowerCase().contains(text)||
                if (p.getLoan_type().toLowerCase().contains(text) ||
                        p.getLoan_id().toLowerCase().contains(text)) {
                    temporaryArray.add(p);
                }
            }
            Log.d("SEARCH","COUNT  "+temporaryArray.size());
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView loan_type;
        TextView loan_id;
        TextView loan_principal;
        TextView loan_interest;
        TextView loan_time;
        TextView loan_penalty;
        TextView loan_total;
        Button imgCall;
        Button imgSMS;

    }
}