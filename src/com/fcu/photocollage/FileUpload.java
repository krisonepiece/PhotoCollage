package com.fcu.photocollage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class FileUpload {
	/**
	 * A generic method to execute any type of Http Request and constructs a
	 * response object
	 * 
	 * @param requestBase
	 *            the request that needs to be exeuted
	 * @return server response as <code>String</code>
	 */
	private static String executeRequest(HttpRequestBase requestBase) {
		String responseString = "";

		InputStream responseStream = null;
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(requestBase);
			if (response != null) {
				HttpEntity responseEntity = response.getEntity();

				if (responseEntity != null) {
					responseStream = responseEntity.getContent();
					if (responseStream != null) {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(responseStream));
						String responseLine = br.readLine();
						String tempResponseString = "";
						while (responseLine != null) {
							tempResponseString = tempResponseString
									+ responseLine
									+ System.getProperty("line.separator");
							responseLine = br.readLine();
						}
						br.close();
						if (tempResponseString.length() > 0) {
							responseString = tempResponseString;
						}
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (responseStream != null) {
				try {
					responseStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		client.getConnectionManager().shutdown();

		return responseString;
	}

	/**
	 * Method that builds the multi-part form data request
	 * 
	 * @param urlString
	 *            the urlString to which the file needs to be uploaded
	 * @param file
	 *            the actual file instance that needs to be uploaded
	 * @param fileName
	 *            name of the file, just to show how to add the usual form
	 *            parameters
	 * @param fileDescription
	 *            some description for the file, just to show how to add the
	 *            usual form parameters
	 * @return server response as <code>String</code>
	 */
	public String executeMultiPartRequest(String urlString, File file,
			String path, String root) {

		HttpPost postRequest = new HttpPost(urlString);
		try {
			MultipartEntity multiPartEntity = new MultipartEntity();

			// The usual form parameters can be added this way
			multiPartEntity.addPart("path", new StringBody(path));
			multiPartEntity.addPart("root", new StringBody(root));

			/*
			 * Need to construct a FileBody with the file that needs to be
			 * attached and specify the mime type of the file. Add the fileBody
			 * to the request as an another part. This part will be considered
			 * as file part and the rest of them as usual form-data parts
			 */
			FileBody fileBody = new FileBody(file, "application/octect-stream");
			multiPartEntity.addPart("attachment", fileBody);

			postRequest.setEntity(multiPartEntity);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}

		return executeRequest(postRequest);
	}

//	public String uploadFile(String upLoadServerUri, File sourceFile, String path, String root) {        
//		int serverResponseCode = 0;
//        String fileName = sourceFile.getPath();
//
//        HttpURLConnection conn = null;
//        DataOutputStream dos = null;  
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1 * 1024 * 1024; 
//         
//        if (!sourceFile.isFile()) {              
//             Log.e("uploadFile", "Source File not exist");              
//             return "Source File not exist";          
//        }
//        else
//        {
//             try { 
//                  
//                   // open a URL connection to the Servlet
//                 FileInputStream fileInputStream = new FileInputStream(sourceFile);
//                 URL url = new URL(upLoadServerUri);
//                  
//                 // Open a HTTP  connection to  the URL
//                 conn = (HttpURLConnection) url.openConnection(); 
//                 conn.setDoInput(true); // Allow Inputs
//                 conn.setDoOutput(true); // Allow Outputs
//                 conn.setUseCaches(false); // Don't use a Cached Copy
//                 conn.setRequestMethod("POST");
//                 conn.setRequestProperty("Connection", "Keep-Alive");
//                 conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//                 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//                 conn.setRequestProperty("attachment", fileName); 
////                 conn.setRequestProperty("path", path); 
////                 conn.setRequestProperty("root", root); 
//                  
//                 dos = new DataOutputStream(conn.getOutputStream());
//        
//                 dos.writeBytes(twoHyphens + boundary + lineEnd); 
//                 dos.writeBytes("Content-Disposition: form-data; name=\"attachment\";filename=\""
//                         + fileName + "\"" + lineEnd);
//                  
//                 dos.writeBytes(lineEnd);
//        
//                 // create a buffer of  maximum size
//                 bytesAvailable = fileInputStream.available(); 
//        
//                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                 buffer = new byte[bufferSize];
//        
//                 // read file and write it into form...
//                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
//                    
//                 while (bytesRead > 0) {
//                      
//                   dos.write(buffer, 0, bufferSize);
//                   bytesAvailable = fileInputStream.available();
//                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
//                    
//                  }
//        
//                 // send multipart form data necesssary after file data...
//                 dos.writeBytes(lineEnd);
//                 
//                 dos.writeBytes(twoHyphens + boundary + lineEnd);
//                 dos.writeBytes("Content-Disposition: form-data; name=\"path\"" + lineEnd);
//                 dos.writeBytes(lineEnd);
//                 dos.writeBytes(path);
//                 dos.writeBytes(lineEnd);
//                 dos.writeBytes(twoHyphens + boundary + lineEnd);
//                 dos.writeBytes("Content-Disposition: form-data; name=\"root\"" + lineEnd);
//                 dos.writeBytes(lineEnd);
//                 dos.writeBytes(root);
//                 dos.writeBytes(lineEnd);
//                 dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//        
//                 // Responses from the server (code and message)
//                 serverResponseCode = conn.getResponseCode();
//                 String serverResponseMessage = conn.getResponseMessage();
//                   
//                 Log.i("uploadFile", "HTTP Response is : "
//                         + serverResponseMessage + ": " + serverResponseCode);
//                  
//                 if(serverResponseCode == 200){
//                	 Log.i("uploadFile", "File Upload Completed.");               
//                 }    
//                  
//                 //close the streams //
//                 fileInputStream.close();
//                 dos.flush();
//                 dos.close();
//                   
//            } catch (MalformedURLException ex) { 
//                ex.printStackTrace(); 
//                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("Upload file to server Exception", "Exception : "
//                                                 + e.getMessage(), e);  
//            }    
//            return "File Upload Completed."; 
//             
//         } // End else block 
//    }
}
