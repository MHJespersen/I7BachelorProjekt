package mmm.i7bachelor_smartsale.app.Adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import mmm.i7bachelor_smartsale.app.Models.SalesItem;
import mmm.i7bachelor_smartsale.app.R;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ItemViewHolder> {
    private static final String TAG = "MarketAdapter";

    public interface IItemClickedListener{
        void OnItemClicked(int index);
    }
    private IItemClickedListener listener;
    private FirebaseStorage mStorageRef;

    private List<SalesItem> itemList;

    public MarketAdapter(IItemClickedListener listener)
    {
        this.listener = listener;
        mStorageRef = FirebaseStorage.getInstance();
    }

    public void updateSalesItemList(List<SalesItem> lists){
        itemList = lists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    //Default image inspired by:
    //https://stackoverflow.com/questions/33194477/display-default-image-in-imageview-if-no-image-returned-from-server
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        SalesItem d = itemList.get(position);
        holder.name.setText(itemList.get(position).getTitle());
        holder.description.setText(itemList.get(position).getDescription());

        double price = itemList.get(position).getPrice();
        String sPrice = null;
        // Check price for decimals, if zero, don't show
        if(price % 1 == 0) {
            sPrice = String.format(java.util.Locale.getDefault(),"%.0f kr", price);
        } else {
            sPrice = String.format(java.util.Locale.getDefault(),"%.2f kr", price);
        }
        holder.price.setText(sPrice);

        if(!itemList.get(position).getImage().equals(""))
        {
            StorageReference strRef = mStorageRef.getReference().child(itemList.get(position).getImage());
            strRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageURL = uri.toString();
                    Glide.with(holder.img.getContext()).load(imageURL).into(holder.img);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Glide.with(holder.img.getContext()).load(R.drawable.emptycart).into(holder.img);
                }
            });
        }
        else
        {
            Glide.with(holder.img.getContext()).load(R.drawable.emptycart).into(holder.img);
        }
    }

    @Override
    public int getItemCount()
    {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //viewholder ui widget references
        ImageView img;
        TextView name;
        TextView description;
        TextView price;

        //custom callback interface for user actions
        //IItemClickedListener listener;

        public ItemViewHolder(@NonNull View itemView)
        {
            super(itemView);

            //references from layout
            img = itemView.findViewById(R.id.imgItem);
            name = itemView.findViewById(R.id.txtItem);
            description = itemView.findViewById(R.id.txtDescription);
            price = itemView.findViewById(R.id.txtSalePrice);
            itemView.setOnClickListener(this);
        }

        //react to a click on a listitem
        @Override
        public void onClick(View view)
        {
            listener.OnItemClicked(getAdapterPosition());
        }
    }
}
