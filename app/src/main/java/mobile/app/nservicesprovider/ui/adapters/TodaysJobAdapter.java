package mobile.app.nservicesprovider.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.app.nservicesprovider.R;
import mobile.app.nservicesprovider.models.TodaysJobModel;
import mobile.app.nservicesprovider.ui.activity.JobDetailsActivity;
import mobile.app.nservicesprovider.utilities.NServicesSingleton;
import mobile.app.nservicesprovider.utilities.UImsgs;

/**
 * Created by madhu on 21/6/17.
 */

public class TodaysJobAdapter extends RecyclerView.Adapter<TodaysJobAdapter.JobViewHolder> {
    private Context mContext;
    private ArrayList<TodaysJobModel> todaysJobModelList;
    private UImsgs uImsgs;

    public TodaysJobAdapter(Context context, ArrayList<TodaysJobModel> model) {
        this.todaysJobModelList = model;
        this.mContext = context;
        uImsgs = new UImsgs(context);
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_active_row, parent, false);
        return new JobViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JobViewHolder holder, final int position) {
        holder.timeTxt.setText(todaysJobModelList.get(position).getServiceReqDate());
        holder.userNameTxt.setText(todaysJobModelList.get(position).getId());
        holder.addressTxt.setText(todaysJobModelList.get(position).getLocation());
        holder.priceTxt.setText(todaysJobModelList.get(position).getPrice() + ", " + todaysJobModelList.get(position).getService());
        holder.statusTxt.setText(todaysJobModelList.get(position).getDisplayStatus());
        holder.historyRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NServicesSingleton.getInstance().setSelectedJobModel(todaysJobModelList.get(position));
                Intent i = new Intent(mContext,JobDetailsActivity.class);
                i.putExtra("booking_id",todaysJobModelList.get(position).getId());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todaysJobModelList.size();
    }

    public class JobViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTxt, priceTxt, userNameTxt, statusTxt, addressTxt;
        public RelativeLayout historyRow;

        public JobViewHolder(View itemView) {
            super(itemView);
            timeTxt = (TextView) itemView.findViewById(R.id.time_txt);
            userNameTxt = (TextView) itemView.findViewById(R.id.user_name_txt);
            priceTxt = (TextView) itemView.findViewById(R.id.price_txt);
            statusTxt = (TextView) itemView.findViewById(R.id.status_txt);
            addressTxt = (TextView) itemView.findViewById(R.id.address_name_txt);
            historyRow = (RelativeLayout) itemView.findViewById(R.id.history_row);
        }
    }
}
