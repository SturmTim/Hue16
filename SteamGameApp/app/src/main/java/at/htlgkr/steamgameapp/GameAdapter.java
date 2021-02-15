package at.htlgkr.steamgameapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import at.htlgkr.steam.Game;

public class GameAdapter extends BaseAdapter {
    private final int listViewItemLayoutId;
    private final List<Game> games;
    private final LayoutInflater inflater;

    public GameAdapter(Context context, int listViewItemLayoutId, List<Game> games) {
        this.listViewItemLayoutId = listViewItemLayoutId;
        this.games = games;

        inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View givenView, ViewGroup parent) {
        View view = (givenView==null) ? inflater.inflate(this.listViewItemLayoutId,null) : givenView;

        TextView name = view.findViewById(R.id.name);
        name.setText(games.get(position).getName());

        TextView releaseDate = view.findViewById(R.id.releaseDate);
        SimpleDateFormat sdf = new SimpleDateFormat(Game.DATE_FORMAT);
        releaseDate.setText(sdf.format(games.get(position).getReleaseDate()));

        TextView price = view.findViewById(R.id.price);
        price.setText(games.get(position).getPrice() + "");

        return view;
    }
}
