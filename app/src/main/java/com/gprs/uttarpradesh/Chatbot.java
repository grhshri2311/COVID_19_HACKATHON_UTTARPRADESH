package com.gprs.uttarpradesh;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.language_translator.v3.util.Language;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class Chatbot extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private ListView listView;
    private CustomChatBotAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private ImageButton btnRecord,voice;
    StreamPlayer streamPlayer = new StreamPlayer();
    private boolean initialRequest;
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String TAG = "MainActivity";
    private static final int RECORD_REQUEST_CODE = 101;
    private boolean listening = false;
    private MicrophoneInputStream capture;
    private Context mContext;
    private MicrophoneHelper microphoneHelper;
    static Timer timer;
    private Assistant watsonAssistant;
    private Response<SessionResponse> watsonAssistantSession;
    private TextToSpeech tts;
    private ArrayList<ArrayList<String>> option;
    private ArrayList<Integer> toggle;
    private ProgressDialog progressDialog;
    ArrayList<String> language;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Boolean sound=false;
    Spinner spinner;
    private LanguageTranslator translationService;
    private String selectedTargetLanguage = Language.ENGLISH;

    private void createServices() {
        translationService = initLanguageTranslatorService();

        if(checkInternetConnection()) {
            watsonAssistant = new Assistant("2019-02-28", new IamAuthenticator("YOUR KEY"));
            watsonAssistant.setServiceUrl("YOUR KEY");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode

        mContext = getApplicationContext();


        inputMessage = findViewById(R.id.inputmessage);
        btnSend = findViewById(R.id.btn_send);
        btnRecord = findViewById(R.id.btn_record);
        listView = findViewById(R.id.list_view);
        spinner = findViewById(R.id.language);
        voice=findViewById(R.id.voice);
        progressDialog=new ProgressDialog(this);
        language=new ArrayList<>();
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Please Wait !"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner

        progressDialog.setCancelable(true);
        messageArrayList = new ArrayList<>();
        option=new ArrayList<>();
        toggle=new ArrayList<>();
        mAdapter = new CustomChatBotAdapter(this,messageArrayList,toggle,option);
        microphoneHelper = new MicrophoneHelper(this);
        tts = new TextToSpeech(this, this);
        timer = new Timer();

        listView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;

        voice.setTag("not");

        language.add("Select Language");
        language.add("English");
        language.add("Hindi");
        language.add("Tamil");
        language.add("Telugu");
        language.add("Malayalam");
        language.add("Gujarati");


        pref = getApplicationContext().getSharedPreferences("language", 0); // 0 - for private mode
        editor = pref.edit();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, R.id.txt_bundle,language);
        spinner.setAdapter(dataAdapter);
        spinner.setGravity(11);
        spinner.setSelection(0, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("English")){
                    selectedTargetLanguage=Language.ENGLISH;
                    editor.putString("lan","E");
                }
                else if(parent.getItemAtPosition(position).equals("Hindi")){
                    selectedTargetLanguage=Language.HINDI;
                    editor.putString("lan","H");
                }
                else if(parent.getItemAtPosition(position).equals("Tamil")){
                    selectedTargetLanguage=Language.TAMIL;
                    editor.putString("lan","T");
                }
                else if(parent.getItemAtPosition(position).equals("Telugu")){
                    selectedTargetLanguage=Language.TELUGU;
                    editor.putString("lan","TE");
                }
                else if(parent.getItemAtPosition(position).equals("Malayalam")){
                    selectedTargetLanguage=Language.MALAYALAM;
                    editor.putString("lan","M");
                }
                else if(parent.getItemAtPosition(position).equals("Gujarati")){
                    selectedTargetLanguage=Language.GUJARATI;
                    editor.putString("lan","G");
                }

                editor.commit();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tts.setSpeechRate(1f);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {
                    start();
                }
            }
        });

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(voice.getTag().equals("clicked")){
                    voice.setTag("not");
                    voice.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_off_black_24dp));
                    sound=false;
                    tts.stop();
                }
                else {
                    voice.setTag("clicked");
                    voice.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_black_24dp));
                    sound=true;
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {
                    if(!inputMessage.getText().toString().isEmpty())
                        sendMessage(inputMessage.getText().toString(),false);
                }
            }
        });




        createServices();
        sendMessage(inputMessage.getText().toString(),true);



        if(pref.getString("lan","").equals("E")){
            selectedTargetLanguage=Language.ENGLISH;

        }
        else if(pref.getString("lan","").equals("T")){
            selectedTargetLanguage=Language.TAMIL;
        }
        else if(pref.getString("lan","").equals("TE")){
            selectedTargetLanguage=Language.TELUGU;
        }
        else if(pref.getString("lan","").equals("M")){
            selectedTargetLanguage=Language.MALAYALAM;
        }
        else if(pref.getString("lan","").equals("G")){
            selectedTargetLanguage=Language.GUJARATI;
        }
        else if(pref.getString("lan","").equals("H")){
            selectedTargetLanguage=Language.HINDI;
        }
        else {
            selectedTargetLanguage=Language.ENGLISH;
        }


        progressDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                speakOut(messageArrayList.get(position).getMessage());
            }
        });
    }



    private class receive extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(final String... params) {
            String firstTranslation;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
            Boolean logged=true;

            pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode

            if(pref.getString("user","").equals(""))
                logged=false;


            if(selectedTargetLanguage.equals(Language.ENGLISH)){
                firstTranslation=params[0];
            }
            else {
                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .addText(params[0])
                        .source(Language.ENGLISH)
                        .target(selectedTargetLanguage)
                        .build();
                TranslationResult result
                        = translationService.translate(translateOptions).execute().getResult();
                firstTranslation = result.getTranslations().get(0).getTranslation();

            }

            String r=params[0];
            Message outMessage;


            if(r.equals("Openning dashboard")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);

                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());



                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result","home");
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }
                        }, 3000);
                    }
                });
            }
            else if(r.equals("Openning Scan")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Chatbot.this,victimalert.class), ActivityOptions.makeSceneTransitionAnimation(Chatbot.this).toBundle());
                            }
                        }, 3000);
                    }
                });
            }
            else if(r.equals("Openning helpline")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Chatbot.this,helpline.class), ActivityOptions.makeSceneTransitionAnimation(Chatbot.this).toBundle());
                            }
                        }, 3000);
                    }
                });
            }
            else if(r.equals("Opening Donate")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Chatbot.this,donate.class), ActivityOptions.makeSceneTransitionAnimation(Chatbot.this).toBundle());
                            }
                        }, 3000);
                    }
                });
            }
            else if(r.equals("Opening Alarm")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                             startActivity(new Intent(Chatbot.this,Alarm.class), ActivityOptions.makeSceneTransitionAnimation(Chatbot.this).toBundle());
                            }
                        }, 3000);
                    }
                });
            }
            else if(r.equals("Opening Medical Shop")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Chatbot.this,Medicalshops.class), ActivityOptions.makeSceneTransitionAnimation(Chatbot.this).toBundle());
                            }
                        }, 3000);
                    }
                });
            }
            else if(r.equals("Opening Updates")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result","updates");
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }
                        }, 3000);
                    }
                });
            }
            else if(r.equals("Openning firstrespond")){
                if(logged) {
                    outMessage = new Message();
                    outMessage.setMessage(firstTranslation);
                    outMessage.setId("2");

                    messageArrayList.add(outMessage);
                    toggle.add(0);
                    option.add(null);

                    speakOut(firstTranslation);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            progressDialog.hide();
                            listView.setSelection(messageArrayList.size());

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Chatbot.this, firstresponder.class), ActivityOptions.makeSceneTransitionAnimation(Chatbot.this).toBundle());
                                }
                            }, 3000);
                        }
                    });
                }

                else {
                    outMessage = new Message();
                    outMessage.setMessage("Please Log In to continue");
                    outMessage.setId("2");

                    messageArrayList.add(outMessage);
                    toggle.add(0);
                    option.add(null);

                    speakOut(firstTranslation);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            progressDialog.hide();
                            listView.setSelection(messageArrayList.size());

                        }
                    });
                }
            }
            else if(r.equals("Openning COVID reports")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result","casesreport");
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }
                        }, 3000);
                    }
                });
            }

            else if(r.equals("Closing Assistant")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 3000);
                    }
                });
            }
            else if(r.equals("Openning Self Assessment")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Chatbot.this,self_asses.class), ActivityOptions.makeSceneTransitionAnimation(Chatbot.this).toBundle());
                            }
                        }, 3000);
                    }
                });
            }

            else if(r.equals("Openning Hospitals near you")){
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);

                speakOut(firstTranslation);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(Chatbot.this,Medicalshops.class);
                                intent.putExtra("text","Hospitals");
                                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(Chatbot.this).toBundle());
                            }
                        }, 3000);
                    }
                });
            }
            else if(params.length==2) {
                if (params[1] != null) {
                    if (params[1].equals("mycity")) {
                        outMessage = new Message();
                        outMessage.setMessage(firstTranslation);
                        outMessage.setId("2");

                        messageArrayList.add(outMessage);
                        toggle.add(0);
                        option.add(null);

                        speakOut(firstTranslation);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                progressDialog.hide();
                                listView.setSelection(messageArrayList.size());

                            }
                        });
                    } else if (params[1].equals("mystate")) {
                        outMessage = new Message();
                        outMessage.setMessage(firstTranslation);
                        outMessage.setId("2");

                        messageArrayList.add(outMessage);
                        toggle.add(0);
                        option.add(null);

                        speakOut(firstTranslation);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                progressDialog.hide();
                                listView.setSelection(messageArrayList.size());
                            }
                        });
                    }
                }
            }
            else {
                Log.println(Log.INFO,"Else translate","Entered");
                outMessage = new Message();
                outMessage.setMessage(firstTranslation);
                outMessage.setId("2");

                messageArrayList.add(outMessage);
                toggle.add(0);
                option.add(null);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                        listView.setSelection(messageArrayList.size());
                    }
                });
                speakOut(firstTranslation);

            }





            return "Did translate";
        }
    }

    private class send extends AsyncTask<String, Void, String> {

        private boolean initialRequest=false;
        private EditText inputMessage;


        @Override
        protected String doInBackground(String... params) {

            String firstTranslation;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    inputMessage=findViewById(R.id.inputmessage);
                    inputMessage.setText("");
                }
            });
            if(params[1].equals("initial"))
                initialRequest=true;
            if(selectedTargetLanguage.equals(Language.ENGLISH)){
                firstTranslation=params[0];
            }
            else if(initialRequest)
                firstTranslation=params[0];
            else {
                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .addText(params[0])
                        .source(selectedTargetLanguage)
                        .target(Language.ENGLISH)
                        .build();
                TranslationResult result
                        = translationService.translate(translateOptions).execute().getResult();
                firstTranslation = result.getTranslations().get(0).getTranslation();

            }

            final String inputmessage=firstTranslation;
            if (!this.initialRequest) {
                Message inputMessage = new Message();
                inputMessage.setMessage(params[0]);
                inputMessage.setId("1");
                messageArrayList.add(inputMessage);
                toggle.add(1);
                option.add(null);
            } else {
                Message inputMessage = new Message();
                inputMessage.setMessage(inputmessage);
                inputMessage.setId("100");
                this.initialRequest = false;


            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });


            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        if (watsonAssistantSession == null) {
                            ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId("YOUR KEY").build());
                            watsonAssistantSession = call.execute();
                        }

                        MessageInput input = new MessageInput.Builder()
                                .text(inputmessage)
                                .build();
                        MessageOptions options = new MessageOptions.Builder()
                                .assistantId("YOUR KEY")
                                .input(input)
                                .sessionId(watsonAssistantSession.getResult().getSessionId())
                                .build();
                        Response<MessageResponse> response = watsonAssistant.message(options).execute();
                        Log.i(TAG, "run: " + response.getResult());
                        if (response != null &&
                                response.getResult().getOutput() != null &&
                                !response.getResult().getOutput().getGeneric().isEmpty()) {

                            List<RuntimeResponseGeneric> responses = response.getResult().getOutput().getGeneric();

                            for (RuntimeResponseGeneric r : responses) {
                                final Message outMessage;
                                switch (r.responseType()) {
                                    case "text":

                                        if(r.text().equals("home")){
                                            new Chatbot.receive().execute("Openning dashboard");
                                        }
                                        else if(r.text().equals("scan")){
                                            new Chatbot.receive().execute("Openning Scan");

                                        }
                                        else if(r.text().equals("helpline")){
                                            new Chatbot.receive().execute("Openning helpline");

                                        }
                                        else if(r.text().equals("firstrespond")){
                                            new Chatbot.receive().execute("Openning firstrespond");

                                        }
                                        else if(r.text().equals("casesnear")){
                                            new Chatbot.receive().execute("Openning COVID reports");

                                        }
                                        else if(r.text().equals("selfassess")){
                                            new Chatbot.receive().execute("Openning Self Assessment");

                                        }
                                        else if(r.text().equals("hospital")){
                                            new Chatbot.receive().execute("Openning Hospitals near you");

                                        }
                                        else if(r.text().equals("mystatus")){
                                            new Chatbot.receive().execute("I didn't understand.You can try rephrasing");

                                        }

                                        else if(r.text().equals("close")){
                                            new Chatbot.receive().execute("Closing Assistant");
                                        }

                                        else if(r.text().equals("mystate")){

                                            new Chatbot.getStateDetail().execute();

                                        }
                                        else if(r.text().equals("mycity")){

                                            new Chatbot.getCityDetail().execute();

                                        }

                                        else if(r.text().equals("donate")){

                                            new Chatbot.receive().execute("Opening Donate");

                                        }
                                        else if(r.text().equals("news")){

                                            new Chatbot.receive().execute("Opening Updates");

                                        }
                                        else if(r.text().equals("medicalshop")){

                                            new Chatbot.receive().execute("Opening Medical Shop");

                                        }
                                        else if(r.text().equals("alarm")){

                                            new Chatbot.receive().execute("Opening Alarm");

                                        }
                                        else {
                                            new Chatbot.receive().execute(r.text());

                                        }
                                        break;

                                    case "option":
                                        outMessage =new Message();
                                        String title = r.title();
                                        String OptionsOutput = "";
                                        for (int i = 0; i < r.options().size(); i++) {
                                            DialogNodeOutputOptionsElement option = r.options().get(i);
                                            OptionsOutput = OptionsOutput + option.getLabel() +"\n";

                                        }
                                        outMessage.setMessage(title + "\n" + OptionsOutput);
                                        outMessage.setId("2");

                                        messageArrayList.add(outMessage);
                                        toggle.add(0);
                                        option.add(null);

                                        break;

                                    case "image":
                                        outMessage = new Message(r);
                                        messageArrayList.add(outMessage);
                                        toggle.add(0);
                                        option.add(null);
                                        // speak the description
                                        break;
                                    default:
                                        Log.e("Error", "Unhandled message type");
                                }
                            }

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    progressDialog.hide();

                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();



            return "Did translate";
        }
    }


    private LanguageTranslator initLanguageTranslatorService() {
        Authenticator authenticator
                = new IamAuthenticator("YOUR KEY");
        LanguageTranslator service = new LanguageTranslator("2018-05-01", authenticator);
        service.setServiceUrl("YOUR KEY");
        return service;
    }



    // Sending a message to Watson Assistant Service
    private void sendMessage(String mes,boolean init) {

        progressDialog.show();
        if(init)
            new Chatbot.send().execute(mes.trim(),"initial");
        else {
            new Chatbot.send().execute(mes.trim(),"notinitial");
        }

    }



    /**
     * Check Internet Connection
     *
     * @return
     */
    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected) {
            return true;
        } else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void start() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedTargetLanguage);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak the word");
        try {
            startActivityForResult(intent, RECORD_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(Chatbot.this,
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RECORD_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(!result.get(0).isEmpty())
                        sendMessage(result.get(0),false);
                }

                break;

            }
            default:start();
        }
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Init failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakOut(String speech) {


        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {


            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
            }

            @Override
            public void onError(String s) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");

        String text = speech;
        if(sound) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "Dummy String");
            }
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    String getState(double lat,double lon){


        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(Chatbot.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5



            String state=addresses.get(0).getAdminArea();

            return  state;


        }
        catch(IndexOutOfBoundsException e){

        }

        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return  null;
    }

    String getCity(double lat,double lon){


        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(Chatbot.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5



            String city = addresses.get(0).getLocality();


            return  city;


        }
        catch(IndexOutOfBoundsException e){

        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return  null;
    }


    class getStateDetail extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {


        }

        protected String doInBackground(Void... urls) {

            // Do some validation here
            // NO NEW CASES DAILY https://api.covid19india.org/data.json
            try {
                URL url = new URL("https://api.covidindiatracker.com/state_data.json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    if (urlConnection!=null)
                        urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);

            }
            return null;
        }

        protected void onPostExecute(String response) {



            try {
                progressDialog.show();
                final JSONArray jsonArray = (JSONArray) new JSONTokener(response).nextValue();


                pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode
                editor = pref.edit();
                String state = pref.getString("state",null);
                String message = "";
                if (state == null) {
                    new Chatbot.receive().execute("Some error occured,Try again later.");
                } else {
                    try {
                        for (int a = 0; a < jsonArray.length(); a++) {

                            JSONObject object = jsonArray.getJSONObject(a);
                            if (object.optString("state").equals(state)) {
                                message = "In your state ( " + state + "):\nActive : " + object.optString("active") + "\nConfirm : " + object.optString("confirmed") + "(Today :  " + object.optString("cChanges") + ")\nDeath : " + object.optString("deaths") + "(Today : " + object.optString("dChanges") + " )\nRecovered : " + object.optString("recovered") + " (Today : " + object.optString("rChanges") + " )";

                                new Chatbot.receive().execute(message, "mystate");


                                break;
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



            } catch (Exception e) {
                e.printStackTrace();


            }


        }
    }

    class getCityDetail extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {


        }

        protected String doInBackground(Void... urls) {

            // Do some validation here
            // NO NEW CASES DAILY https://api.covid19india.org/data.json
            try {
                URL url = new URL("https://api.covid19india.org/state_district_wise.json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    if (urlConnection!=null)
                        urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);

            }
            return null;
        }

        protected void onPostExecute(final String response) {



            try {

                progressDialog.show();
                pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode
                editor = pref.edit();
                String state = pref.getString("state",null);
                String city = pref.getString("city",null);
                String message = "";
                if (state == null || city == null) {
                    new Chatbot.receive().execute("Some error occured,Try again later.");
                } else {
                    try {

                        JSONObject object1 = (JSONObject) new JSONTokener(response).nextValue();
                        object1 = object1.getJSONObject(state).getJSONObject("districtData");

                        JSONObject obj = object1.getJSONObject(city);

                        message = "In your City( " + city + "):\nActive : " + obj.optString("active") + "\nConfirm : " + obj.optString("confirmed") + "\nDeath : " + obj.optString("deceased") + "\nRecovered : " + obj.optString("recovered");

                        new Chatbot.receive().execute(message, "mycity");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();


            }


        }
    }


}
