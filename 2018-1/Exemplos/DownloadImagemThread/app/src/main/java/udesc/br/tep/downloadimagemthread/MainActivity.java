package udesc.br.tep.downloadimagemthread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String imagemURL = "http://www.fapesc.sc.gov.br" +
            "/wp-content/uploads/2016/07/udesc-joinville.jpg";
    private ImageView imgDownload = null;
    private ProgressBar pbDownload = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgDownload = findViewById(R.id.imgDownload);
        pbDownload = findViewById(R.id.pbDownload);
        pbDownload.setIndeterminate(true);

        // downloadImagem(imagemURL);
        // downloadImagemContinuo(imagemURL);
        downloadImagemComAsync(imagemURL);
    }

    private void downloadImagem(final String imgURL){
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        // Faz o download - thread separada
                        final Bitmap bitmap = loadImageFromNetwork(imgURL);

                        // Atualiza o componente de imagem - thread separada
                        imgDownload.post(new Runnable() {
                            public void run() {
                                imgDownload.setImageBitmap(bitmap);
                                pbDownload.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
        );

        thread.start();
    }

    private void downloadImagemContinuo(final String imgURL){
        Toast.makeText(this, "HEYYY! Estou começando o processo de download!", Toast.LENGTH_SHORT);

        // Zerar a img
        imgDownload.setImageBitmap(null);

        // Exibe a progressbar
        pbDownload.setVisibility(View.VISIBLE);

        // Dispara a thread
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        // Faz o download - thread separada
                        final Bitmap bitmap = loadImageFromNetwork(imgURL);
                        System.out.println("Terminei o download!");

                        // Atualiza o componente de imagem - thread separada - 10s
                        imgDownload.postDelayed(new Runnable() {
                            public void run() {
                                imgDownload.setImageBitmap(bitmap);
                                pbDownload.setVisibility(View.INVISIBLE);
                            }
                        }, 10000);
                    }
                }
        );

        thread.start();

    }

    private void downloadImagemComAsync(final String imgURL){
        // Cria uma ASyncTask
        DownloadFilesTask down_async = new DownloadFilesTask();

        // Executa uma task
        down_async.execute();
    }

    private Bitmap loadImageFromNetwork(String src) {
        try {
            // Habilita barra
            pbDownload.setVisibility(View.VISIBLE);

            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.mipmap.ic_launcher);
            e.printStackTrace();
            return icon;
        }
    }

    private class DownloadFilesTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Mostra o ProgressBar para fazer a animacao
            pbDownload.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            // Faz o download da imagem
            Bitmap bitmap = loadImageFromNetwork(imagemURL);
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                pbDownload.setVisibility(View.INVISIBLE);
                imgDownload.setImageBitmap(bitmap);
            }
        }
    }
}
