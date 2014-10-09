package geocoding.reynolds.ee461l.utexas.edu.geocoding;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by stephen on 10/9/14.
 */
public class SearchAddress extends AsyncTask<String, Integer, JSONObject> {

    public JSONObject doInBackground(String... urls){

        try {
            URI geocodeRequest = new URI(urls[0]);

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(geocodeRequest.toURL().openStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            JSONObject locationData = new JSONObject(responseStrBuilder.toString());

            return locationData;
        }
        catch(URISyntaxException e){

        }
        catch(IOException e){

        }
        catch(JSONException e){

        }

        return null;
    }

}
