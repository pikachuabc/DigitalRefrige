package com.example.digitalrefrige.viewModel.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalrefrige.databinding.ItemCardBinding;
import com.example.digitalrefrige.model.dataHolder.Item;

import com.example.digitalrefrige.utils.Converters;
import com.example.digitalrefrige.views.itemList.ItemDetailFragment;
import com.example.digitalrefrige.views.itemList.ItemListFragmentDirections;

public class ItemListAdapter extends ListAdapter<Item, ItemListAdapter.ItemHolder> {

    public ItemListAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * callBack configuration to decide if there are changes at current index position in  the
     * recycler list
     * 1) items are not the same as previous one
     * 2) item content are not the same as previous one
     */
    private static final DiffUtil.ItemCallback<Item> DIFF_CALLBACK = new DiffUtil.ItemCallback<Item>() {
        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getCreateDate().equals(newItem.getCreateDate());
        }
    };

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardBinding binding = ItemCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Item item = getItem(position);
        holder.bind(item);
    }


    /**
     * viewHolder for the adapter
     */
    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewDescription;
        private TextView createDate;
        private int itemID;

        public ItemHolder(@NonNull ItemCardBinding binding) {
            super(binding.getRoot());
            textViewName = binding.textViewTitle;
            textViewDescription = binding.textViewDescription;
            createDate = binding.textViewCreateDate;

            binding.getRoot().setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    NavDirections directions = (NavDirections) ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(itemID);
                    Navigation.findNavController(view).navigate(directions);
                }
            });
        }

        public void bind(Item item) {
            // bond item content into to this holder
            textViewName.setText(item.getName());
            textViewDescription.setText(item.getDescription());
            createDate.setText(Converters.dateToString(item.getCreateDate()));
            itemID = item.getId();
        }

    }
}

