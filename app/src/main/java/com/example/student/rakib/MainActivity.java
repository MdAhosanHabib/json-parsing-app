package com.example.student.rakib;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    ArrayList<HashMap<String,String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();
        lv = findViewById(R.id.list);

        new GetContacts().execute();

    }

    private  class GetContacts extends AsyncTask<Void, Void,Void>{



        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json data is downloading",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            httpHandler sh = new httpHandler();

            String url = "http://192.168.1.107/ss/api.php";
            String jsonStr = null;
            try{
                jsonStr = sh.makeServiceCall(url);
            }catch (IOException e){
                e.printStackTrace();
            }

            Log.e(TAG, "Response from url: "+ jsonStr);
            if(jsonStr != null){
                try{
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray students = jsonObj.getJSONArray("student_info");

                    for(int i = 0; i < students.length(); i++){
                        JSONObject c = students.getJSONObject(i);
                        String id = c.getString("s_id");
                        String name = c.getString("s_name");
                        String section = c.getString("s_section");
                        String mobile = c.getString("s_mobile");


                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("id",id);
                        contact.put("name",name);
                        contact.put("section",section);
                        contact.put("mobile",mobile);

                        contactList.add(contact);
                    }
                }catch (final JSONException e){

                    Log.e(TAG, "Json parsing error: "+e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"could't get json from srever. Chech LogCat for possible erros",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

                ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                        R.layout.list_item,new String[]{"id","name","section","mobile"},new int[]{R.id.id,R.id.name,R.id.section,R.id.mobile});
                lv.setAdapter(adapter);

        }
    }
}
