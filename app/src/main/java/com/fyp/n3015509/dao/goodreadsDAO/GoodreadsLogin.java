package com.fyp.n3015509.dao.goodreadsDAO;

/**
 * Created by tomha on 23-Mar-17.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.fyp.n3015509.Util.XMLUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.activity.LoginActivity;
import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;

public class GoodreadsLogin extends LoginActivity {

    private static final String BASE_GOODREADS_URL = "https://www.goodreads.com";
    private static final String TOKEN_SERVER_URL = BASE_GOODREADS_URL + "/oauth/request_token";
    private static final String AUTHENTICATE_URL = BASE_GOODREADS_URL + "/oauth/authorize";
    private static final String ACCESS_TOKEN_URL = BASE_GOODREADS_URL + "/oauth/access_token";

    private static final String GOODREADS_KEY = "8vvXeL81U1l6yxnUT1c9Q";
    private static final String GOODREADS_SECRET = "ULtTT6xRNOOCPxX3VS7SLkGhtyLoK9lQ6Rm2fuwRZQ";
    private static final int REQUEST_CODE_CHECK = 1;
    private final XMLUtil xmlUtil = new XMLUtil();

    public int GetGoodreadsAuthentication(Activity ctx) {
        int goodreads_id = 0;
        String goodreads_name = null;
        JSONObject login = null;
        try {
            OAuthHmacSigner signer = new OAuthHmacSigner();
            // Get Temporary Token
            OAuthGetTemporaryToken getTemporaryToken = new OAuthGetTemporaryToken(TOKEN_SERVER_URL);
            signer.clientSharedSecret = GOODREADS_SECRET;
            getTemporaryToken.signer = signer;
            getTemporaryToken.consumerKey = GOODREADS_KEY;
            getTemporaryToken.transport = new NetHttpTransport();
            OAuthCredentialsResponse temporaryTokenResponse = getTemporaryToken.execute();

            // Build Authenticate URL
            OAuthAuthorizeTemporaryTokenUrl accessTempToken = new OAuthAuthorizeTemporaryTokenUrl(AUTHENTICATE_URL);
            accessTempToken.temporaryToken = temporaryTokenResponse.token;
            String authUrl = accessTempToken.build();


            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
            ctx.startActivity(browserIntent);
            Thread.sleep(2000);

            // Get Access Token using Temporary token and Verifier Code
            OAuthGetAccessToken getAccessToken = new OAuthGetAccessToken(ACCESS_TOKEN_URL);
            getAccessToken.signer = signer;

            signer.tokenSharedSecret = temporaryTokenResponse.tokenSecret;
            getAccessToken.temporaryToken = temporaryTokenResponse.token;
            getAccessToken.transport = new NetHttpTransport();
            getAccessToken.consumerKey = GOODREADS_KEY;
            OAuthCredentialsResponse accessTokenResponse = getAccessToken.execute();

            // Build OAuthParameters in order to use them while accessing the resource
            OAuthParameters oauthParameters = new OAuthParameters();
            signer.tokenSharedSecret = accessTokenResponse.tokenSecret;
            oauthParameters.signer = signer;
            oauthParameters.consumerKey = GOODREADS_KEY;
            oauthParameters.token = accessTokenResponse.token;

            // Use OAuthParameters to access the desired Resource URL
            HttpRequestFactory requestFactory = new ApacheHttpTransport().createRequestFactory(oauthParameters);
            GenericUrl genericUrl = new GenericUrl("https://www.goodreads.com/api/auth_user");
            HttpResponse resp = requestFactory.buildGetRequest(genericUrl).execute();

            //parse response to get user Id
            String xml = resp.parseAsString();
            Document doc = xmlUtil.getXMLDocument(xml);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("user");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    goodreads_id = Integer.parseInt(eElement.getAttribute("id"));
                    goodreads_name = eElement.getElementsByTagName("name").item(0).getTextContent();
                }
            }
            SaveSharedPreference.setUserName(ctx, goodreads_name);
            return goodreads_id;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
