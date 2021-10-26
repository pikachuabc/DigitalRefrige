package com.example.digitalrefrige.viewModel.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.ItemCardBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.utils.Converters;
import com.example.digitalrefrige.views.itemList.ItemListFragmentDirections;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends ListAdapter<Item, ItemListAdapter.ItemHolder> {

    /**
     * callBack configuration to decide if there are changes at current index position in  the
     * recycler list
     * 1) items are not the same as previous one
     * 2) item content are not the same as previous one
     */
    private static final DiffUtil.ItemCallback<Item> DIFF_CALLBACK = new DiffUtil.ItemCallback<Item>() {
        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getItemId() == newItem.getItemId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getExpireDate().equals(newItem.getExpireDate()) &&
                    oldItem.getImgUrl().equals(newItem.getImgUrl());
        }
    };

    public ItemListAdapter() {
        super(DIFF_CALLBACK);
    }

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
//        private TextView textViewDescription;
        private TextView expireDate;
        private ImageView itemImage;
        private long itemID;

        public ItemHolder(@NonNull ItemCardBinding binding) {
            super(binding.getRoot());
            textViewName = binding.textViewTitle;
//            textViewDescription = binding.textViewDescription;
            expireDate = binding.textViewExpireDate;
            itemImage = binding.imageViewFoodPhoto;

            binding.getRoot().setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    NavDirections directions = (NavDirections) ItemListFragmentDirections.actionItemListFragmentToItemDetailActivity(itemID);
                    Navigation.findNavController(view).navigate(directions);
                }
            });
        }

        public void bind(Item item) {
            // bond item content into to this holder
            textViewName.setText(item.getName());
//            textViewDescription.setText(item.getDescription());
            expireDate.setText(Converters.dateToString(item.getExpireDate()));
            itemID = item.getItemId();
            String uri = item.getImgUrl();
            if (uri != null && !uri.equals("")) {
                Picasso.get()
                        .load(Uri.parse(uri))
                        .fit()
                        .centerCrop()
                        .into(itemImage);
            }else{
                Picasso.get().load(R.drawable.img_4857).into(itemImage);
            }
        }

    }

    @Override
    public void submitList(@Nullable List<Item> list) {
        super.submitList(list);
    }
}

