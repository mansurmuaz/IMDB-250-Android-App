package com.example.mmuazekici.imdb250.UsersFriends;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mmuazekici.imdb250.R;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.usersViewHolder> {

    private Cursor mCursor;
    private final UsersAdapter.ItemAdapterOnClickHandler mClickHandler;
    private Context mContext;

    public interface ItemAdapterOnClickHandler {
        void onClick(Cursor c, int clickedPosition);
    }

    public UsersAdapter(Context context, UsersAdapter.ItemAdapterOnClickHandler clickHandler) {
        super();
        mContext = context;
        mClickHandler = clickHandler;
    }

    public class usersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView usernameTextView;

        private String username;

        public usersViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.rv_username);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mCursor, adapterPosition);
        }

        public void setUsernameText(String s){
            usernameTextView.setText(s);
        }
    }

    @Override
    public UsersAdapter.usersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.user_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new UsersAdapter.usersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.usersViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        String currentUsername =  mCursor.getString(mCursor.getColumnIndex("username"));
        holder.setUsernameText(currentUsername);
    }

    @Override
    public int getItemCount() {

        if (mCursor == null){
            return 0;
        }
        else{
            return mCursor.getCount();
        }
    }

    public Cursor swapCursor(Cursor c) {

        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}
