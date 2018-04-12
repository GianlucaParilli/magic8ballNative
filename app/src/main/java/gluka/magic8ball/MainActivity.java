package gluka.magic8ball;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private MediaPlayer music;
    private TextToSpeech tts;
    private double num;
    private long lastUpdate;
    String saveAnswer="Please Flip Me";
    TextView answer;
    SensorManager manager;
    String[] answerArray = {"Signs point to yes.", "Yes","Reply hazy, try again.","Without a doubt.",
             "My sources say no.","As I see it, yes.","You may rely on it.","Concentrate and ask again.", "Outlook not so good.",
             "It is decidedly so.","Better not tell you now.","Very doubtful.","Yes - definitely.","It is certain.","Cannot predict now.",
             "Most likely.","Ask again later.","My reply is no.","Outlook good.","Don't count on it."
            };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        lastUpdate = System.currentTimeMillis();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(Color.parseColor("#ff669900"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.about1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),AboutActivity.class);
                startActivity(intent);
            }
        });
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        answer =(TextView) findViewById(R.id.answerTV);
        if (savedInstanceState != null) {
            String savedText = savedInstanceState.getString("key");

            answer.setText(savedText);
        }
        if(saveAnswer.equals("Please Flip Me") && savedInstanceState != null){
            saveAnswer = savedInstanceState.getString("key");//savedInstanceState.getString("key");
        }


        music = MediaPlayer.create(this, R.raw.christmasmorning);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
               tts.setLanguage(Locale.US);
            }

        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(),AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long actualTime = System.currentTimeMillis();
        if (actualTime - lastUpdate > 5000) {
            setNum(Double.parseDouble(String.format(Locale.US, "%.1f", sensorEvent.values[2])));
            if (getNum() <= -9.8) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 250 milliseconds
                v.vibrate(200);
                //Log.d("print", "onCreate: " + getNum() + randomAnswer());
                saveAnswer = randomAnswer();
                answer.setText(saveAnswer);
                tts.speak(saveAnswer, TextToSpeech.QUEUE_FLUSH, null, null);
                lastUpdate = actualTime;

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }
    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(this);
        answer.setText(saveAnswer);
        //setNum(getNum());
        music.pause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        music.start();
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

      }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key",saveAnswer);
        saveAnswer = outState.getString("key");

    }

    public String randomAnswer(){
        int max = answerArray.length-1;
        Random rand = new Random();
        int randNum = rand.nextInt(max);
        String ans = answerArray[randNum];

        return ans;
    }

}
