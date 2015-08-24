package com.fcu.photocollage.movie;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class RunPhp {

	public String stringQuery(String url){
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost method = new HttpPost(url);
            HttpResponse response = httpclient.execute(method);
            HttpEntity entity = response.getEntity();
            if(entity != null){
                return EntityUtils.toString(entity, "utf-8");
            }
            else{
                return "No string.";
            }
         }
         catch(Exception e){
             return "Network problem";
         }
    }

}
