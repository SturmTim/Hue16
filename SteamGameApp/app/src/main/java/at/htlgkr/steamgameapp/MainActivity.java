package at.htlgkr.steamgameapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.steam.Game;
import at.htlgkr.steam.ReportType;
import at.htlgkr.steam.SteamBackend;

public class MainActivity extends AppCompatActivity {
    private static final String GAMES_CSV = "games.csv";

    SteamBackend steamBackend = new SteamBackend();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadGamesIntoListView();
        setUpReportSelection();
        setUpSearchButton();
        setUpAddGameButton();
        setUpSaveButton();
    }

    private void loadGamesIntoListView() {
        try {
            steamBackend.loadGames(getAssets().open(GAMES_CSV));
            ListView games = findViewById(R.id.gamesList);
            games.setAdapter(new GameAdapter(this,R.layout.game_item_layout,steamBackend.getGames()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpReportSelection() {
        List<ReportTypeSpinnerItem> reportItems = new ArrayList<>();
        reportItems.add(new ReportTypeSpinnerItem(ReportType.NONE,SteamGameAppConstants.SELECT_ONE_SPINNER_TEXT));
        reportItems.add(new ReportTypeSpinnerItem(ReportType.SUM_GAME_PRICES,SteamGameAppConstants.SUM_GAME_PRICES_SPINNER_TEXT));
        reportItems.add(new ReportTypeSpinnerItem(ReportType.AVERAGE_GAME_PRICES,SteamGameAppConstants.AVERAGE_GAME_PRICES_SPINNER_TEXT));
        reportItems.add(new ReportTypeSpinnerItem(ReportType.UNIQUE_GAMES,SteamGameAppConstants.UNIQUE_GAMES_SPINNER_TEXT));
        reportItems.add(new ReportTypeSpinnerItem(ReportType.MOST_EXPENSIVE_GAMES,SteamGameAppConstants.MOST_EXPENSIVE_GAMES_SPINNER_TEXT));

        Spinner chooseReport = findViewById(R.id.chooseReport);
        chooseReport.setAdapter(new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,reportItems));
        chooseReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ReportTypeSpinnerItem spinnerItem = (ReportTypeSpinnerItem) adapterView.getItemAtPosition(i);

                AlertDialog.Builder message = new AlertDialog.Builder(MainActivity.this);
                message.setTitle(spinnerItem.getDisplayText());

                switch(spinnerItem.getType()){
                    case SUM_GAME_PRICES:
                        message.setMessage(SteamGameAppConstants.ALL_PRICES_SUM+steamBackend.sumGamePrices());
                        message.show();
                        break;
                    case AVERAGE_GAME_PRICES:
                        message.setMessage(SteamGameAppConstants.ALL_PRICES_AVERAGE+steamBackend.averageGamePrice());
                        message.show();
                        break;
                    case UNIQUE_GAMES:
                        message.setMessage(SteamGameAppConstants.UNIQUE_GAMES_COUNT+steamBackend.getUniqueGames().size());
                        message.show();
                        break;
                    case MOST_EXPENSIVE_GAMES:
                        List<Game> expensiveGames = steamBackend.selectTopNGamesDependingOnPrice(3);
                        message.setMessage(SteamGameAppConstants.MOST_EXPENSIVE_GAMES
                                +"\n"+expensiveGames.get(0)
                                +"\n"+expensiveGames.get(1)
                                +"\n"+expensiveGames.get(2));
                        message.show();
                        break;
                    case NONE:
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void setUpSearchButton() {
        Button search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    EditText searchTerm = new EditText(getApplicationContext());
                    searchTerm.setId(R.id.dialog_search_field);

                    AlertDialog.Builder message = new AlertDialog.Builder(MainActivity.this);
                    message.setTitle(SteamGameAppConstants.ENTER_SEARCH_TERM)
                            .setView(searchTerm)
                            .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            List<Game> filteredGames = steamBackend.getGames().stream()
                                                    .filter(game -> game.getName().toUpperCase().contains(searchTerm.getText().toString().toUpperCase()))
                                                    .collect(Collectors.toList());
                                            ListView gamesList = findViewById(R.id.gamesList);
                                            gamesList.setAdapter(new GameAdapter(getApplicationContext(),R.layout.game_item_layout,filteredGames));
                                        }
                                    }).setNegativeButton("Cancel", null);
                    message.show();
            }
        });
    }

    private void setUpAddGameButton() {
        Button addGame = findViewById(R.id.addGame);
        addGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                EditText name = new EditText(getApplicationContext());
                layout.addView(name);
                EditText date = new EditText(getApplicationContext());
                layout.addView(date);
                EditText price = new EditText(getApplicationContext());
                layout.addView(price);

                AlertDialog.Builder message = new AlertDialog.Builder(MainActivity.this);
                message.setTitle(SteamGameAppConstants.NEW_GAME_DIALOG_TITLE)
                        .setView(layout)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    steamBackend.addGame(new Game(name.getText().toString(),new SimpleDateFormat(Game.DATE_FORMAT).parse(date.getText().toString()),Double.parseDouble(price.getText().toString())));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("Cancel",null);
                message.show();
            }
        });
    }

    private void setUpSaveButton() {
        Button save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(SteamGameAppConstants.SAVE_GAMES_FILENAME);
                try {
                    OutputStream outputStream = openFileOutput(SteamGameAppConstants.SAVE_GAMES_FILENAME, Context.MODE_PRIVATE);
                    steamBackend.store(outputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
