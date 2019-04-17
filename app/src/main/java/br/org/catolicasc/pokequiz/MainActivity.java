package br.org.catolicasc.pokequiz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<Pokemon> pokemonList;
    private ImageView imgPokemon;
    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgPokemon = findViewById(R.id.imgPokemon);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        DownloadDeDados downloadDeDados = new DownloadDeDados();
        downloadDeDados.execute("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json");

    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int resposta = connection.getResponseCode();

                if (resposta != HttpURLConnection.HTTP_OK) { // se resposta não foi OK
                    if (resposta == HttpURLConnection.HTTP_MOVED_TEMP  // se for um redirect
                            || resposta == HttpURLConnection.HTTP_MOVED_PERM
                            || resposta == HttpURLConnection.HTTP_SEE_OTHER) {
                        // pegamos a nova URL e abrimos nova conexão!
                        String novaUrl = connection.getHeaderField("Location");
                        connection = (HttpURLConnection) new URL(novaUrl).openConnection();
                    }
                }
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Erro ao baixar imagem"
                        + e.getMessage());
            }

            return null;
        }
    }

    public void downloadImagem(String imgUrl) {
        ImageDownloader imageDownloader = new ImageDownloader();

        try {
            // baixar a imagem da internet
            Bitmap imagem = imageDownloader.execute(imgUrl).get();
            // atribuir a imagem ao imageView
            imgPokemon.setImageBitmap(imagem);
        } catch (Exception e) {
            Log.e(TAG, "downloadImagem: Impossível baixar imagem"
                    + e.getMessage());
        }
    }

    private void criaTela() {
        List<String> nomesPokemon = new ArrayList<>();

        Random r = new Random();
        // pokemon correto
        Pokemon p = pokemonList.get(r.nextInt(pokemonList.size()));
        int indiceCorreto = r.nextInt(4);

        nomesPokemon.clear();
        for (int i = 0; i < 4; i++) {
            if (i == indiceCorreto) {
                nomesPokemon.add(p.getNome());
            } else {
                String pokemonErrado = null;
                do {
                    pokemonErrado = pokemonList.get(r.nextInt(pokemonList.size())).getNome();
                } while (pokemonErrado == p.getNome());
                nomesPokemon.add(pokemonErrado);
            }
        }

        button0.setText(nomesPokemon.get(0));
        button1.setText(nomesPokemon.get(1));
        button2.setText(nomesPokemon.get(2));
        button3.setText(nomesPokemon.get(3));


        downloadImagem(p.getImgUrl());
    }

    private class DownloadDeDados extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: começa com o parâmetro: " + strings[0]);
            String json = downloadJson(strings[0]);
            if (json == null) {
                Log.e(TAG, "doInBackground: Erro baixando JSON");
            }
            //Log.d(TAG, "doInBackground: " + json);
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parâmetro é: " + s);
            ParseJson parseJson = new ParseJson();
            parseJson.parse(s);

            pokemonList = parseJson.getPokemons();

            criaTela();
        }

        private String downloadJson(String urlString) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int resposta = connection.getResponseCode();
                Log.d(TAG, "downloadJson: O código de resposta foi: " + resposta);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                int charsLidos;
                char[] inputBuffer = new char[500];
                while (true) {
                    charsLidos = reader.read(inputBuffer);
                    if (charsLidos < 0) {
                        break;
                    }
                    if (charsLidos > 0) {
                        builder.append(
                                String.copyValueOf(inputBuffer, 0, charsLidos));
                    }
                }
                reader.close();
                return builder.toString();

            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadJson: URL é inválida " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadJson: Ocorreu um erro de IO ao baixar dados: "
                        + e.getMessage());
            }
            return null;
        }
    }
}
