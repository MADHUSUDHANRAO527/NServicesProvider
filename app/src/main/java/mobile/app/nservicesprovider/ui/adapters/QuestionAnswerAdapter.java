package mobile.app.nservicesprovider.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.app.nservicesprovider.R;
import mobile.app.nservicesprovider.models.QuestionAnswerModel;
import mobile.app.nservicesprovider.utilities.UImsgs;


/**
 * Created by madhu on 23/6/17.
 */

public class QuestionAnswerAdapter extends RecyclerView.Adapter<QuestionAnswerAdapter.ServicesViewHolder> {
    private Context mContext;
    private ArrayList<QuestionAnswerModel> questionAnswerModelList;
    private UImsgs uImsgs;


    public QuestionAnswerAdapter(Context context, ArrayList<QuestionAnswerModel> questionAnswerModelList) {
        this.questionAnswerModelList = questionAnswerModelList;
        this.mContext = context;
    }

    @Override
    public ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_answer_row, parent, false);
        return new ServicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServicesViewHolder holder, final int position) {
        holder.questionTxt.setText(questionAnswerModelList.get(position).getQuestion());
        holder.answerTxt.setText(questionAnswerModelList.get(position).getAnswer());
    }

    @Override
    public int getItemCount() {
        return questionAnswerModelList.size();
    }

    public class ServicesViewHolder extends RecyclerView.ViewHolder {
        public TextView questionTxt, answerTxt;

        public ServicesViewHolder(View itemView) {
            super(itemView);
            questionTxt = (TextView) itemView.findViewById(R.id.question_txt);
            answerTxt = (TextView) itemView.findViewById(R.id.answer_txt);
        }
    }
}
