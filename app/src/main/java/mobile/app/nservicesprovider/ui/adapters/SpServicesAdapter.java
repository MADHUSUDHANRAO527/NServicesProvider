package mobile.app.nservicesprovider.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mobile.app.nservicesprovider.R;
import mobile.app.nservicesprovider.models.SpServicesModel;
import mobile.app.nservicesprovider.utilities.PreferenceManager;
import mobile.app.nservicesprovider.utilities.UImsgs;

/**
 * Created by madhu on 22/6/17.
 */

public class SpServicesAdapter extends RecyclerView.Adapter<SpServicesAdapter.ServicesViewHolder> {
    private Context mContext;
    private List<SpServicesModel> servicesModel;
    private UImsgs uImsgs;
    private PreferenceManager preferenceManager;
    private String userLati, userLongi;
    private static final int REQUEST_PERMISSION_SERVICE_SETTING = 103;


    public SpServicesAdapter(Context context, List<SpServicesModel> baseModel) {
        this.servicesModel = baseModel;
        this.mContext = context;
        uImsgs = new UImsgs(context);
        preferenceManager = new PreferenceManager(mContext);
    }

    @Override
    public ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_row, parent, false);
        return new ServicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServicesViewHolder holder, final int position) {
        holder.servicesTitleTxt.setText(servicesModel.get(position).getServiceName());
       /* holder.featureLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferenceManager.getString("user_lat") != null)
                    userLati = preferenceManager.getString("user_lat");
                if (preferenceManager.getString("user_long") != null)
                    userLongi = preferenceManager.getString("user_long");
                if (userLati != null && userLongi != null) {
                    Intent i = new Intent(mContext, ServicesMapActivity.class);
                    i.putExtra("service_id", servicesModel.getFeatureServicesModels().get(position).getId());
                    NServicesSingleton.getInstance().setSelectedServiceName(servicesModel.getFeatureServicesModels().get(position).getName());
                    mContext.startActivity(i);
                } else {
                    UImsgs.showToast(mContext,R.string.enable_perission_manually);
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                    intent.setData(uri);
                  //  mContext.startActivityForResult(intent);
                    ((Activity) mContext).startActivityForResult(intent,REQUEST_PERMISSION_SERVICE_SETTING);

                }

            }
        });*/

    }

    @Override
    public int getItemCount() {
        return servicesModel.size();
    }

    public class ServicesViewHolder extends RecyclerView.ViewHolder {
        public TextView servicesTitleTxt;
        public ImageView servicesIcon;
        public RelativeLayout featureLay;

        public ServicesViewHolder(View itemView) {
            super(itemView);
            servicesTitleTxt = (TextView) itemView.findViewById(R.id.service_name_txt);
       //     servicesIcon = (ImageView) itemView.findViewById(R.id.services_icon);
       //     featureLay = (RelativeLayout) itemView.findViewById(R.id.featured_lay);
        }
    }
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
    }
}
