package com.example.mmuazekici.imdb250;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mmuazekici.imdb250.Database.DatabaseConract;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.moviesViewHolder> {


    private Cursor mCursor;

    private Context mContext;

    public MoviesAdapter(Context context) {
        super();
        mContext = context;
    }

    public class moviesViewHolder extends RecyclerView.ViewHolder{

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

        String currentMovieName =  mCursor.getString(mCursor.getColumnIndex(DatabaseConract.MoviesTable.COLUMN_MOVIE_NAME));
        holder.setMovieNameText(currentMovieName);

        String currentDuration =  mCursor.getString(mCursor.getColumnIndex(DatabaseConract.MoviesTable.COLUMN_DURATION));
        holder.setdurationText(currentDuration);

        String currentYear =  mCursor.getString(mCursor.getColumnIndex(DatabaseConract.MoviesTable.COLUMN_DATE));
        holder.setYearText(currentYear);

        String currentImdbScore =  mCursor.getString(mCursor.getColumnIndex(DatabaseConract.MoviesTable.COLUMN_IMDB_SCORE));
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
