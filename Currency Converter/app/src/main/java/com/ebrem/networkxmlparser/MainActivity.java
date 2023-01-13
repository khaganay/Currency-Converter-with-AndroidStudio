package com.ebrem.networkxmlparser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

            EditText txtAmount;
            TextView txtResult;
            Spinner spFrom,spTo;
            Button button;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                txtAmount = (EditText) findViewById(R.id.txtamount);
                txtResult = (TextView) findViewById(R.id.txtresult);
                spFrom = (Spinner) findViewById(R.id.spfrom);
                spTo = (Spinner) findViewById(R.id.spto);
                button = (Button) findViewById(R.id.btn1);

                String[] from = {"USD","TRY","GBP","CAD","EUR","KWD","CHF","AUD"};
                ArrayAdapter ad = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,from);
                spFrom.setAdapter(ad);

                String[] to = {"USD","TRY","GBP","CAD","EUR","KWD","CHF","AUD"};
                ArrayAdapter ad1 = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,to);
                spTo.setAdapter(ad1);


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Double tot;
                        String url = "https://api.exchangerate.host/convert?from="+spFrom.getSelectedItem()+"&to="+spTo.getSelectedItem()+"&format=xml&amount="+txtAmount.getText();
                        new HTTPAsyncTask().execute(url);
                    }
                });
            }

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String...urls){

            try{
                return HttpGet(urls[0]);
            }
            catch (IOException e){
                return "Unable to retrieve web page. URL may be invalid";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            try{
                XMLParser(result);
            }
            catch (XmlPullParserException e){
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


        private String HttpGet(String myUrl) throws IOException {
            InputStream inputStream = null;
            String result = "";

            URL url = new URL(myUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.connect();

            inputStream = conn.getInputStream();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
            return result;
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            return result;
        }
        public void XMLParser (String result) throws XmlPullParserException, IOException {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader( result));
            int eventType = xpp.getEventType();
            String Newresult = "";
            String Tag = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    Tag = xpp.getName();
                }else if(eventType == XmlPullParser.TEXT){
                    if(Tag.equals("result")){
                        Newresult = Newresult + " " + xpp.getText();
                    }
                }
                eventType = xpp.next();
            }
            txtResult.setText(Newresult);
        }
    }
}