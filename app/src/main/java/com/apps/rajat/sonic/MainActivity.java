package com.apps.rajat.sonic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.AlphabeticIndex;
import android.icu.text.DecimalFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity  {

    //Variables Here
    private ArrayList<SongInfo> _songs = new ArrayList<SongInfo>();;
    RecyclerView recyclerView;
    SeekBar seekBar;
    SongAdapter songAdapter;
    MediaPlayer mediaPlayer;
    Button btnloop, btnPLay, btnPlayIcon;
    TextView tvtime, tvtotal , textsb , tvsong;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    private Handler myHandler = new Handler();
    int color = 0;
    private MediaSessionCompat mediaSessionCompat;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnloop = (Button)findViewById(R.id.loopb);
        btnPLay = (Button)findViewById(R.id.playText);
        btnPlayIcon = (Button)findViewById(R.id.playicon);
        tvtime = (TextView) findViewById(R.id.tvtime);
        tvtotal = (TextView) findViewById(R.id.tvtotal);
        tvsong = (TextView) findViewById(R.id.tvsong);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        songAdapter = new SongAdapter(this,_songs);
        mediaSessionCompat = new MediaSessionCompat(this,"tag");
        recyclerView.setAdapter(songAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        mediaPlayer = new MediaPlayer();
        btnloop.setText("Loop - ON");
        swipeRefreshLayout.setColorSchemeColors(R.color.googleblue, R.color.googlegreen, R.color.googlered, R.color.googleyellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (color > 4) {
                            color = 0;
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2000);

                Toast.makeText(MainActivity.this, "This doesn't do anything for now.", Toast.LENGTH_SHORT).show();

            }
        });

        //Loop btn OnClick
        btnloop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnloop.getText().toString().equals("Loop - ON")) {

                    mediaPlayer.setLooping(false);
                    btnloop.setBackgroundResource(R.mipmap.loopofff);
                    btnloop.setText("Loop - OFF");
                }else{
                mediaPlayer.setLooping(true);
                btnloop.setBackgroundResource(R.mipmap.looponn);
                btnloop.setText("Loop - ON");}
            }
        });

        //Play Btn On Click
        btnPlayIcon .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyy();
                if (btnPLay.getText().toString().equals("Pause")){
                    YoYo.with(Techniques.Tada)
                            .duration(1000)
                            .repeatMode(0)
                            .playOn(btnPlayIcon);

                    mediaPlayer.pause();
                    btnPlayIcon.setBackgroundResource(R.mipmap.playbtnn);
                    btnPLay.setText("Play");
                }else{
                    YoYo.with(Techniques.RubberBand)
                            .duration(1000)
                            .repeatMode(0)
                            .playOn(btnPlayIcon);
                    mediaPlayer.start();
                    btnPlayIcon.setBackgroundResource(R.mipmap.stopbtnn);
                    btnPLay.setText("Pause");
                }
            }
        });

        //Seekbar Stuff
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {

                int seektime = progress;

                int split[] = splitTime(seektime);

                tvtime.setText(split[0] + ":" + split[1] + " ");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


                //Toast.makeText(MainActivity.this, "Someone is touching the seekbar.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //Toast.makeText(MainActivity.this, "Not Anymore", Toast.LENGTH_SHORT).show();
                seekonclick();

            }
        });

        //Main on Item Click here
        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Button b, View view, final SongInfo obj, int position) {
                if (mediaPlayer.equals(obj)){
                    Toast.makeText(MainActivity.this, "Works", Toast.LENGTH_SHORT).show();
                }
                if(b.getText().equals("Stop")){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    btnPLay.setText("Play");
                    btnPlayIcon.setBackgroundResource(R.mipmap.playbtnn);

                    b.setText("Play");
                  //  mediaPlayer.release();
                  //    `  mediaPlayer = null;


                }else {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(obj.getSongUrl());
                                mediaPlayer.prepareAsync();

                                if (btnloop.getText().toString().equals("Loop - ON")) {

                                    mediaPlayer.setLooping(false);
                                }else{
                                    mediaPlayer.setLooping(true);}
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                        seekBar.setProgress(0);
                                        seekBar.setMax(mediaPlayer.getDuration());
                                        Log.d("Prog", "run: " + mediaPlayer.getDuration());
                                    }
                                });

                             //   b.setText("Stop");
                                btnPLay.setText("Pause");

                                btnPlayIcon.setBackgroundResource(R.mipmap.stopbtnn);


                            }catch (Exception e){}
                        }

                    };
                    myHandler.postDelayed(runnable,100);

                }
            }
        });

        checkUserPermission();

        Thread t = new runThread();
        t.start();
       // Collections.sort(new Collections());

    }

    //Music Notification
    public void notifyy(){
        final Intent activityIntent = new Intent(this, MainActivity.class);
        final PendingIntent contentIntent = (PendingIntent) PendingIntent.getActivity(this , 0 , activityIntent, 0);
        Bitmap largeicon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);

        NotificationManager nm2 = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notifbuilder2;
        String CHANNEL_ID_2 = "2";
         Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.mipmap.playbtnn)
                .setContentTitle("Now Playing")
                .setContentText("content")
                .setLargeIcon(largeicon)
                .addAction(R.mipmap.loopofff, "Dislike", null)
                .addAction(R.mipmap.playbtnn, "Previous", null)
                .addAction(R.mipmap.icon, "Play", null)
                .addAction(R.mipmap.stopbtnn, "Next", null)
                .addAction(R.mipmap.looponn, "Like", null)

                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1,2,3).setMediaSession(mediaSessionCompat.getSessionToken()))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
        .build();
      //  nm2.notify(2,notifbuilder2.build());

    }

    //Seeking media player
    public void seekonclick(){

        int pro =  seekBar.getProgress();
        mediaPlayer.seekTo(pro);
        if (btnloop.getText().toString().equals("Loop - ON")) {

            mediaPlayer.setLooping(false);
        }else{
            mediaPlayer.setLooping(true);}

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Menu Stuff
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ver:

                return true;
            case R.id.about:
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    //Cool Setup Alerter
    public void Alerter(){
        Alerter.create(this)
                .setTitle("Great! Setup Complete.")
                .setText("Now you can just swipe the screen to get your songs!")
                .setBackgroundColorRes(R.color.googlegreen)
                .setDuration(4000)
                .setIcon(R.drawable.ic_done_black_24dp)
                .enableSwipeToDismiss()
                .setProgressColorRes(R.color.googleblue)
                .enableProgress(true)
                .show();
    }

    //Important Framework of Music Player
    public class runThread extends Thread {


        @Override
        public void run() {
            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("Runwa", "run: " + 1);
                if (mediaPlayer != null) {

                    seekBar.post(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            if (seekBar.getProgress() == seekBar.getMax()){
                                btnPLay.setText("Play");
                            }
                            /* if (Build.VERSION.SDK_INT>=23){
                                tvtime.setText(""+mediaPlayer.getTimestamp().toString());
                             }*/
                            int currenttime =mediaPlayer.getCurrentPosition();

                            int split[] = splitTime(currenttime);

                            tvtime.setText(split[0] + ":" + split[1] + " ");
                            int totalt = mediaPlayer.getDuration();
                            int splitt[] = splitTime(totalt);

                            tvtotal.setText(" / "+splitt[0]+":"+splitt[1]);
                        }
                    });


                    Log.d("Runwa", "run: " + mediaPlayer.getCurrentPosition());
                }else if (mediaPlayer == null){
                    Toast.makeText(MainActivity.this, "Alright", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    //Splting Time Maths
    public static int[] splitTime(int ms){
        int ss = ms/1000;
        int mm = ss/60;
        ss = ss - (mm*60);
        return new int[]{mm,ss};
    }

    //Permission
    private void checkUserPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
                return;
            }
        }
        loadSongs();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    progressDialog = ProgressDialog.show(this,"Please Wait", "Loading Songs....", true);
                    loadSongs();
                    CountDownTimer timer = new CountDownTimer(5000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            progressDialog.dismiss();
                            Alerter();
                           // Toast.makeText(MainActivity.this, "Swipe to get the Song List!", Toast.LENGTH_SHORT).show();

                        }
                    }.start();

                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//                    NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//                    NotificationCompat.Builder notifbuilder;
//                    notifbuilder = new NotificationCompat.Builder(this)
//                            .setSmallIcon(R.mipmap.playbtnn)
//                            .setContentTitle("Permission Required!")
//                            .setContentText("Permission of Reading Storage is required.");
//                    nm.notify(1,notifbuilder.build());
                    Alerter.create(this)
                            .setTitle("Permisson Required")
                            .setText("Permission of Reading Storage is required.")
                            .setBackgroundColorRes(R.color.googlered)
                            .setDuration(5000)
                            .enableSwipeToDismiss()
                            .setProgressColorRes(R.color.googlegreen)
                            .enableProgress(true)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    checkUserPermission();
                                }
                            })
                            .show();
                    checkUserPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    //Loading Songs
    private void loadSongs(){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));


                    SongInfo s = new SongInfo(name,artist,url);
                    _songs.add(s);

                }while (cursor.moveToNext());
            }

            cursor.close();
            songAdapter = new SongAdapter(MainActivity.this,_songs);

        }
    }



}
