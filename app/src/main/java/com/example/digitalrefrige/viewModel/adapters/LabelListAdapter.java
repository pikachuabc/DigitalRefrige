package com.example.digitalrefrige.viewModel.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalrefrige.databinding.LabelCardBinding;
import com.example.digitalrefrige.model.dataHolder.Label;

public class LabelListAdapter extends ListAdapter<Label, LabelListAdapter.LabelHolder> {
    public static final DiffUtil.ItemCallback<Label> DIFF_CALLBACK = new DiffUtil.ItemCallback<Label>() {
        @Override
        public boolean areItemsTheSame(@NonNull Label oldItem, @NonNull Label newItem) {
            return oldItem.getLabelId() == newItem.getLabelId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Label oldItem, @NonNull Label newItem) {
            return oldItem.getTitle().equals(newItem.getTitle());
        }
    };

    public LabelListAdapter(){
        super(DIFF_CALLBACK);
    }


    @NonNull
    @Override
    public LabelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LabelCardBinding binding = LabelCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LabelHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelHolder holder, int position) {
        Label label = getItem(position);
        holder.bind(label);
    }

    class LabelHolder extends RecyclerView.ViewHolder {
        private TextView labelName;


        public LabelHolder(@NonNull LabelCardBinding binding) {
            super(binding.getRoot());
            labelName = binding.textViewLabelTitle;

        }

        public void bind(Label label) {
            // bond item content into to this holder
            labelName.setText(label.getTitle());
        }

    }
}
