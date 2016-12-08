package sirs.grupo7.securepayment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

import sirs.grupo7.securepayment.connections.UDP;
import sirs.grupo7.securepayment.readwrite.ReadWriteInfo;

public class MakingTransactionActivity extends AppCompatActivity {

    private String moneyToTransfer;
    private String destIBAN;
    private String nowWhat;
    private String cod;


    private class MakingTransactionTask extends AsyncTask<Void, Void, Void> {

        TextView textView;
        String res = "";

        @Override
        protected void onPreExecute() {
            textView = (TextView) findViewById(R.id.text_done);
        }

        @Override
        protected Void doInBackground(Void... params) {

            UDP udp = new UDP(read(ReadWriteInfo.IP), getApplicationContext());

            String MYIBAN = read(ReadWriteInfo.IBAN);

            switch (nowWhat) {
                case "cr":{
                    /*try {
                        res = udp.giveResponsetoChallenge(input1, input2, input3, tid);
                    } catch (IOException e) {
                        res = "ERROR";
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("RES = " + res);*/
                    return null;

                }
                default: {
                    try {
                        System.out.println("\nT\n");

                        res = udp.makeTransaction(MYIBAN, destIBAN, moneyToTransfer.substring(0, moneyToTransfer.length() - 2));
                        System.out.println(";;;;;;;;;;;;;;; " + res);
                    } catch (IOException e) {
                        res = "ERROR";
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("RES = " + res);
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (res.equals("C")) {
                // Transfer was completed with success

                textView.setText(moneyToTransfer + " " + getResources().getString(R.string.transferSuccess) + "\n" + destIBAN);
            } else if (res.startsWith("P")) {
                // Challengeresponse
                System.out.println("_______________________CR____________________");
                // TODO Challenge Response
                String[] cods = res.substring(2, 18).split("-");
                String tid = res.substring(20);
                System.out.println(cods[1]);
                System.out.println(cods[2]);
                System.out.println(cods[3]);
                System.out.println(tid);
                System.out.println("_____________________________________________");

                String toAsk1 = cods[0] + " " + cods[1] + " " + cods[2];
                String toAsk2 = cods[4] + " " + cods[5] + " " + cods[6];
                String toAsk3 = cods[7] + " " + cods[8] + " " + cods[9];

                Intent intent = new Intent(MakingTransactionActivity.this, Start.class);
                intent.putExtra("toAsk1", toAsk1);
                intent.putExtra("toAsk2", toAsk2);
                intent.putExtra("toAsk3", toAsk3);
                intent.putExtra("tid", tid);
                intent.putExtra("cod", cod);

                startActivity(intent);

            } else if (res.equals("F")) {
                // Insuficient founds
                textView.setText("Insuficient Founds");
            }  else if (res.equals("I")) {
                // Invalid IBAN
                textView.setText("Invalid IBAN\n" + destIBAN);
            } else {
                textView.setText(getResources().getString(R.string.errorMakingTransaction));
            }
        }

        public String read(String filename) {
            try {
                String message;
                FileInputStream fileInputStream = openFileInput(filename);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer stringBuffer = new StringBuffer();
                while ((message = bufferedReader.readLine()) != null) {
                    stringBuffer.append(message);
                }
                return stringBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "ERROR";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_transaction);
        final Activity activity = this;

        Button buttonDone = (Button) findViewById(R.id.button_done);
        cod = (String) getIntent().getExtras().get("cod");
        //System.out.println(";;;;;;;;;;;;;;; ");

        nowWhat = (String) getIntent().getExtras().get("nowWhat");

        switch (nowWhat) {
            case "cr":{
                String input1 = (String) getIntent().getExtras().get("cod1");
                String input2 = (String) getIntent().getExtras().get("cod2");
                String input3 = (String) getIntent().getExtras().get("cod3");
                String tid = (String) getIntent().getExtras().get("tid");

                new MakingTransactionTask().execute();

                buttonDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra("cod", cod);
                        startActivity(intent);
                    }
                });

                break;
            }
            default: {
                //myIBAN = (String) getIntent().getExtras().get("myIBAN");
                destIBAN = (String) getIntent().getExtras().get("destIBAN");
                moneyToTransfer = (String) getIntent().getExtras().get("moneyToTransfer");

                cod = (String) getIntent().getExtras().get("cod");

                new MakingTransactionTask().execute();

                buttonDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra("cod", cod);
                        startActivity(intent);
                    }
                });
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_making_transaction, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Nothing
    }
}
