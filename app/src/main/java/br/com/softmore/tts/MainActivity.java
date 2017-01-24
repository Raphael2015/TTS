package br.com.softmore.tts;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements android.view.View.OnClickListener, TextToSpeech.OnInitListener {
    //Objeto TTS
    private TextToSpeech tts;
    //Codigo de Verificacao
    private int REQUEST_TTS = 0;
    private int REQ_CODE_SPEECH_INPUT = 1;
    public EditText etTexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etTexto = (EditText) findViewById(R.id.edtTexto);

        Button falar = (Button) findViewById(R.id.btnFalar);
        falar.setOnClickListener(this);

        Button escutar = (Button) findViewById(R.id.btnExcutar);
        escutar.setOnClickListener(this);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(
                TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        startActivityForResult(checkTTSIntent, REQUEST_TTS);
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnFalar){
            String texto = etTexto.getText().toString();
            falar(texto);
        }
        if(v.getId() == R.id.btnExcutar){
            escutar();
        }
    }
    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {

            if(tts.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Erro TTS", Toast.LENGTH_LONG).show();
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
        if (requestCode == REQ_CODE_SPEECH_INPUT)
        {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                etTexto.setText(result.get(0));

            }

        }
    }
    private void falar(String texto) {
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }
    private void escutar() {
        Intent intent = new
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
