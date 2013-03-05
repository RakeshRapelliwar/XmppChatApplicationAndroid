package com.snapwork.xmppchatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Proxy;
import android.util.Log;

@SuppressWarnings("unused")
public class ProxyUrlUtil {

	private String toString(BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append('\n');
		}
		return sb.toString();
	}
	
	public String escape(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		//	Log.e("UrlUtil" , "Cannot find UTF-8 encoding - this is not very likely!");
			return s;
		}

	}

	private static final int REDIRECT_RESPONSE_CODE = 302;
	
	public URL getRedirectedUrl(URL url,Context c) throws IOException {
		//HttpURLConnection conn = (HttpURLConnection) url.openConnection();

	    HttpGet httpRequest = null;
		try {
    	   	URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
	            httpRequest = new HttpGet(uri);
	            httpRequest.addHeader("Accept-Encoding", "gzip");
	        //    Log.d("Fb",uri.toString());
	            
	    } catch (URISyntaxException e) {
	    	//	Log.e("URLUTIL", "Error in url " );
	            e.printStackTrace();
	    }        
	
	    try{
    	
	    	ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    	
	    	HttpParams httpParameters = new BasicHttpParams();
	 	     // Set the timeout in milliseconds until a connection is established.
	 	    int timeoutConnection = 2000;
	 	    if (connMgr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE)
	 	    	timeoutConnection = 3000;
				
	 	     HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	 	    
 	    
	 	     // Set the default socket timeout (SO_TIMEOUT) 
	 	     // in milliseconds which is the timeout for waiting for data.
	 	     //int timeoutSocket = 2000;
	 	     //HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	 	     DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);        	
	
	 	     android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	
			 	if (connMgr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE)
			 	{
				    if((mobile.isAvailable()) &&(Proxy.getDefaultHost()!=null))
					{
						//Log.d("InXMLN","inside set Proxy");
						HttpHost proxy = new HttpHost(Proxy.getDefaultHost() ,  Proxy.getDefaultPort() , "http");
						httpclient.getParams().setParameter (ConnRoutePNames.DEFAULT_PROXY, proxy);
					}
			 	}
	
			 	//httpParameters.setRedirecting(httpParameters, false);
			 	httpclient.setRedirectHandler(new RedirectHandler() {
			        public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
			            return null;
			        }

			        public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
			            return false;
			        }
			    });

		    HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
		    int status = response.getStatusLine().getStatusCode();
	//	Log.d("Fb","status: "+ Integer.toString(status));    
		
		
		/*conn.setRequestMethod("GET");
		conn.setInstanceFollowRedirects(false);
		int rc = conn.getResponseCode();*/
		if (status != REDIRECT_RESPONSE_CODE) {
			//throw new IOException("code " + rc + " '" + conn.getResponseMessage() + "'");
			return null;
		}
		//String location = conn.getHeaderField("Location");
		String location = response.getFirstHeader("Location").getValue();
		if (location == null) {
			throw new IOException("No 'Location' header found");
		}
		
		return new URL(location);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    
	    return null;
	}

	public String getProxyXML(URL url, Context c) throws IOException {
	//	Log.d("InXMLN", "Start");
		BufferedReader reader = null;
		HttpGet httpRequest = null;
		String retStr=null;
        try {
        	   	URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
                httpRequest = new HttpGet(uri);
                httpRequest.addHeader("Accept-Encoding", "gzip");
                //Log.d("ProxyXML",uri.toString());
        } catch (URISyntaxException e) {
        	//	Log.e("URLUTIL", "Error in url " );
                e.printStackTrace();
        }        

        try{
        	
        	ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        	
        	HttpParams httpParameters = new BasicHttpParams();
     	     // Set the timeout in milliseconds until a connection is established.
     	    int timeoutConnection = 2000;
     	    if (connMgr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE)
     	    	timeoutConnection = 3000;
 			
     	     HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
     	     // Set the default socket timeout (SO_TIMEOUT) 
     	     // in milliseconds which is the timeout for waiting for data.
     	     //int timeoutSocket = 2000;
     	     //HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
     	     DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);        	

     	     android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
 
 		 	if (connMgr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE)
 		 	{
 			    if((mobile.isAvailable()) &&(Proxy.getDefaultHost()!=null))
 				{
 					//Log.d("InXMLN","inside set Proxy");
 					HttpHost proxy = new HttpHost(Proxy.getDefaultHost() ,  Proxy.getDefaultPort() , "http");
 					httpclient.getParams().setParameter (ConnRoutePNames.DEFAULT_PROXY, proxy);
 				}
 		 	}

 		 	CookieStore cookieStore = new BasicCookieStore();
 		 	Cookie cookie = new BasicClientCookie("Path= ","; HttpOnly");
 		 	cookieStore.addCookie(cookie);
 		 	HttpContext localContext = new BasicHttpContext();
 		 	localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
 		 	
 		 	HttpResponse response = (HttpResponse) httpclient.execute(httpRequest,localContext);
		    int status = response.getStatusLine().getStatusCode();
		    if (status == HttpStatus.SC_OK) {
		    	//Log.d("InXMLN", "Response ok");
		    	Header contentEncoding = response.getFirstHeader("Content-Encoding");
		    	if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
		    		//Log.d("InXMLN", "in GZip");
		    		
		    		InputStream instream = response.getEntity().getContent();		    		
		    	    instream = new GZIPInputStream(instream);
		    	    reader = new BufferedReader(new InputStreamReader(instream));
		    	    retStr = toString(reader);
		    	}
		    	else{
		    		//Log.d("InXMLN", "in Normal");
				    HttpEntity entity = response.getEntity();
				    BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity); 
				    //InputStream instream = bufHttpEntity.getContent();			    
				    reader = new BufferedReader(new InputStreamReader(bufHttpEntity.getContent()));
				    retStr = toString(reader);
		    	}
		    }
		    else
		    { //	Log.e("getXMLN", "Http response notok " + Integer.toString(status));
		    	
		    }
		    
			//retStr = instream.toString();
        }catch (MalformedURLException e) {
			//Log.w("URLUTIL", "Wrong url: ");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//Log.d("InXMLN", "End");		
		return retStr;
		
	}	
	
	
	public Bitmap getImage(URL url,Context c) throws IOException {
		
		HttpGet httpRequest = null;
		Bitmap bmp = null;
		//Log.d("IngetImage", "Start");
		//Log.d("IngetImage", url.toString());
		InputStream instream = null;
        try {
        	   	URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
                httpRequest = new HttpGet(uri);
                httpRequest.addHeader("Accept-Encoding", "gzip");
        } catch (URISyntaxException e) {
        	//	Log.e("IngetImage", "Error in url " );
                e.printStackTrace();
        }        

        try{
        	
        	  HttpParams httpParameters = new BasicHttpParams();
     	     // Set the timeout in milliseconds until a connection is established.
     	     int timeoutConnection = 3000;
     	     //HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
     	     // Set the default socket timeout (SO_TIMEOUT) 
     	     // in milliseconds which is the timeout for waiting for data.
     	     int timeoutSocket = 3000;
     	     HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);     	    
     	     HttpProtocolParams.setUseExpectContinue(httpParameters, true);
     	     DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);        	
     	     //HttpProtocolParams.setUseExpectContinue(httpclient.getParams(), true);
     	    
		    //HttpClient httpclient = new DefaultHttpClient();	
		    					 	
		    ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		    
 		 	//android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
 
 		 	android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
 
 		 	if (connMgr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE)
 		 	{
 			    if((mobile.isAvailable()) &&(Proxy.getDefaultHost()!=null))
 				{
 					//Log.d("IngetImage","inside set Proxy");
 					HttpHost proxy = new HttpHost(Proxy.getDefaultHost() ,  Proxy.getDefaultPort() , "http");
 					httpclient.getParams().setParameter (ConnRoutePNames.DEFAULT_PROXY, proxy);
 				}
 		 	}

		    
		    HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
		    int status = response.getStatusLine().getStatusCode();
		    if (status == HttpStatus.SC_OK) {
		    	//Log.d("IngetImage", "Response ok");
		    	Header contentEncoding = response.getFirstHeader("Content-Encoding");
		    	if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
		    		//Log.d("IngetImage", "in GZip");
		    		
		    		instream = response.getEntity().getContent();		    		
		    	    instream = new GZIPInputStream(instream);
		    	    //reader = new BufferedReader(new InputStreamReader(instream));

		    	}
		    	else{
		    		//Log.d("IngetImage", "in Normal");
				    HttpEntity entity = response.getEntity();
				    BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity); 
				    instream = bufHttpEntity.getContent();			    
				    //reader = new BufferedReader(new InputStreamReader(bufHttpEntity.getContent()));

		    	}
		    	
		    	bmp = BitmapFactory.decodeStream(instream);
		    }
		    else{
		    	//Log.e("IngetImage",url.toString());
		    //	Log.e("IngetImage", "Http response notok " + Integer.toString(status));
		    }
		    
			//retStr = instream.toString();
        }catch (MalformedURLException e) {
			//Log.w("IngetImage", "Wrong url: ");
		}catch (Exception e) {
			//Log.e("GetImage Err",url.toString());
			e.printStackTrace();
			return getImageRegular(url,c);
		}

		
		
		return bmp;
	}
	

	public Bitmap getImageRegular(URL url,Context c) throws IOException {
		
		HttpGet httpRequest = null;
		Bitmap bmp = null;
		//Log.d("IngetImage", "Start");
		InputStream instream = null;
        try {
        	   	URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
                httpRequest = new HttpGet(uri);
                //httpRequest.addHeader("Accept-Encoding", "gzip");
        } catch (URISyntaxException e) {
        		//Log.e("IngetImage", "Error in url " );
                e.printStackTrace();
        }        

        try{
        	
        	  HttpParams httpParameters = new BasicHttpParams();
     	     // Set the timeout in milliseconds until a connection is established.
     	     int timeoutConnection = 3000;
     	     HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
     	     // Set the default socket timeout (SO_TIMEOUT) 
     	     // in milliseconds which is the timeout for waiting for data.
     	     int timeoutSocket = 3000;
     	     HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);     	    
     	     //HttpProtocolParams.setUseExpectContinue(httpParameters, true);
     	     DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);        	
     	     //HttpProtocolParams.setUseExpectContinue(httpclient.getParams(), true);
     	    					 	
		    ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);		    
 		 	android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
 
 		 	if (connMgr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE)
 		 	{
 			    if((mobile.isAvailable()) &&(Proxy.getDefaultHost()!=null))
 				{
 					//Log.d("IngetImage","inside set Proxy");
 					HttpHost proxy = new HttpHost(Proxy.getDefaultHost() ,  Proxy.getDefaultPort() , "http");
 					httpclient.getParams().setParameter (ConnRoutePNames.DEFAULT_PROXY, proxy);
 				}
 		 	}

		    
		    HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
		    int status = response.getStatusLine().getStatusCode();
		    if (status == HttpStatus.SC_OK) {
		    	//Log.d("IngetImage", "Response ok");
		    	Header contentEncoding = response.getFirstHeader("Content-Encoding");
		    	if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
		    		//Log.d("IngetImage", "in GZip");
		    		
		    		instream = response.getEntity().getContent();		    		
		    	    instream = new GZIPInputStream(instream);
		    	    //reader = new BufferedReader(new InputStreamReader(instream));

		    	}
		    	else{
		    		//Log.d("IngetImage", "in Normal");
				    HttpEntity entity = response.getEntity();
				    BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity); 
				    instream = bufHttpEntity.getContent();			    
				    //reader = new BufferedReader(new InputStreamReader(bufHttpEntity.getContent()));

		    	}
		    	
		    	bmp = BitmapFactory.decodeStream(instream);
		    }
		    else
		    { //	Log.e("IngetImage", "Http response notok " + Integer.toString(status));
		    }
			//retStr = instream.toString();
        }catch (MalformedURLException e) {
			//Log.w("IngetImage", "Wrong url: ");
		}catch (Exception e) {
			//Log.e("GetImage Err",url.toString());
			e.printStackTrace();
		}
		
		return bmp;
	}

	
	
}
