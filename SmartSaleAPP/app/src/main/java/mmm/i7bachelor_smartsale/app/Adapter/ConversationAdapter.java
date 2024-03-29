package mmm.i7bachelor_smartsale.app.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Collections;
import java.util.List;

import mmm.i7bachelor_smartsale.app.Models.PrivateMessage;
import mmm.i7bachelor_smartsale.app.R;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    public interface IMessageClickedListener {
        void onMessageClicked(int index);
    }

    private Context con;
    private IMessageClickedListener listener;
    private FirebaseStorage mStorageRef;
    private List<PrivateMessage> messagelist;
    private String UserEmail;
    int ScreenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    FirebaseAuth auth;

    public ConversationAdapter(IMessageClickedListener listener) {
        mStorageRef = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        UserEmail = auth.getCurrentUser().getEmail();
        this.listener = listener;
        this.con = (Context)listener;
    }

    public void updateMessageList(List<PrivateMessage> list){
        Collections.sort(list);
        messagelist = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
        ConversationViewHolder vh = new ConversationViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position)
    {
        holder.Message.setText(messagelist.get(position).getMessageBody());
        holder.Datetime.setText(messagelist.get(position).getMessageDate());
        if(messagelist.get(position).getSender().equals(UserEmail))
        {
            holder.ProfilePic.setX(ScreenWidth*(float)0.3);
            holder.Message.setX(ScreenWidth*(float)0.03);
            holder.Message.setBackgroundColor(ContextCompat.getColor(con, R.color.messageColorSend));
        }
        else
        {
            holder.ProfilePic.setX(ScreenWidth*(float)-0.3);
            holder.Message.setX(ScreenWidth*(float)-0.03);
            holder.Message.setBackgroundColor(ContextCompat.getColor(con, R.color.messageColorReceived));
        }

        Glide.with(holder.ProfilePic.getContext())
                .load(R.drawable.com_facebook_profile_picture_blank_portrait) //Set blank til default og hent det rigtige profile pic fra FB / Github?
                .into(holder.ProfilePic);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount()
    {
        if(messagelist != null)
            return messagelist.size();
        return 0;
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //viewholder ui widget references
        ImageView ProfilePic;
        TextView Message;
        TextView Datetime;

        //custom callback interface for user actions
        public ConversationViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ProfilePic = itemView.findViewById(R.id.profilePic);
            Message = itemView.findViewById(R.id.txtMessage);
            Datetime = itemView.findViewById(R.id.txtDatetime);
            itemView.setOnClickListener(this);
        }

        //react to a click on a listitem
        @Override
        public void onClick(View view)
        {
            listener.onMessageClicked(getAdapterPosition());
        }
    }
}
