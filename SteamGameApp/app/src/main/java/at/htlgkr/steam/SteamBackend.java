package at.htlgkr.steam;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SteamBackend {
    private final List<Game> games;
    private final SimpleDateFormat sdf = new SimpleDateFormat(Game.DATE_FORMAT);

    public SteamBackend() {
        games = new ArrayList<>();
    }


    public void loadGames(InputStream inputStream) {
        games.clear();

        try(BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream))){
            String s=bufferedInputStream.readLine();
            while((s=bufferedInputStream.readLine())!=null){
                String[] splits = s.split(";");
                try {
                    games.add(new Game(splits[0],sdf.parse(splits[1]),Double.parseDouble(splits[2])));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void store(OutputStream fileOutputStream) {
        PrintWriter outPrintWriter = new PrintWriter(new OutputStreamWriter(fileOutputStream));
        for (Game game : games) {
            outPrintWriter.println(game.getName() + ";" + sdf.format(game.getReleaseDate()) + ";" + game.getPrice());
        }
        outPrintWriter.close();
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games.clear();
        this.games.addAll(games);
    }

    public void addGame(Game newGame) {
        games.add(newGame);
    }

    public double sumGamePrices() {
        return games.stream().mapToDouble(Game::getPrice).sum();
    }

    public double averageGamePrice() {
        return games.stream().mapToDouble(Game::getPrice).average().orElse(0.0);
    }

    public List<Game> getUniqueGames() {
        return games.stream().distinct().collect(Collectors.toList());
    }

    public List<Game> selectTopNGamesDependingOnPrice(int n) {
        return games.stream().sorted((game, t1) -> {
            return Double.compare(t1.getPrice(), game.getPrice());
        }).limit(n)
                .collect(Collectors.toList());
    }
}
