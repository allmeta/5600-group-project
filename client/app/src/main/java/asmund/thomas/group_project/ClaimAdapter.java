package asmund.thomas.group_project;

import android.view.View;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.ViewHolder>{
    private List<Claim> claimList;
    private View.OnClickListener onItemClickListener;

    public ClaimAdapter(List<Claim> claimList, View.OnClickListener onClickListener) {
        this.claimList = claimList;
        this.onItemClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClaimAdapter.ViewHolder viewHolder, int position) {
        Claim claim = claimList.get(position);
        TextView idTextView = viewHolder.idTextView;
        idTextView.setText(claim.getId());
        TextView nameTextView = viewHolder.nameTextView;
        nameTextView.setText(claim.getDes());
    }

    @Override
    public int getItemCount() {
        return claimList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView idTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
            nameTextView = itemView.findViewById(R.id.item_name);

            idTextView = itemView.findViewById(R.id.item_id);
        }
    }

}
