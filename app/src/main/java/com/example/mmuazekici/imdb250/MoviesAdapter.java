package com.example.mmuazekici.imdb250;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.moviesViewHolder> {


    private Cursor mCursor;
    private final ItemAdapterOnClickHandler mClickHandler;
    private Context mContext;

    public interface ItemAdapterOnClickHandler {
        void onClick(Cursor c, int clickedPosition);
    }

    public MoviesAdapter(Context context, ItemAdapterOnClickHandler clickHandler) {
        super();
        mContext = context;
        mClickHandler = clickHandler;
    }

    public class moviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView movieNameTextView;
        private final TextView durationTextView;
        private final TextView yearTextView;
        private final TextView imdbScoreTextView;

        private String movieName;
        private String duration;
        private String year;
        private String imdbScore;

        public moviesViewHolder(View itemView) {
            super(itemView);
            movieNameTextView = itemView.findViewById(R.id.rv_movieName);
            durationTextView = itemView.findViewById(R.id.rv_duration);
            yearTextView = itemView.findViewById(R.id.rv_year);
            imdbScoreTextView = itemView.findViewById(R.id.rv_imdbScore);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mCursor, adapterPosition);
        }

        public void setMovieNameText(String s){
            movieNameTextView.setText(s);
        }
        public void setdurationText(String s){
            durationTextView.setText(s);
        }
        public void setYearText(String s){
            yearTextView.setText(s);
        }
        public void setImdbScoreText(String s){
            imdbScoreTextView.setText(s);
        }
    }

    @Override
    public moviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new moviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.moviesViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        String currentMovieName =  mCursor.getString(mCursor.getColumnIndex("movieName"));
        holder.setMovieNameText(currentMovieName);

        String currentDuration =  mCursor.getString(mCursor.getColumnIndex("duration"));
        holder.setdurationText(currentDuration);

        String currentYear =  mCursor.getString(mCursor.getColumnIndex("date"));
        holder.setYearText(currentYear);

        String currentImdbScore =  mCursor.getString(mCursor.getColumnIndex("imdbScore"));
        holder.setImdbScoreText(currentImdbScore);
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
