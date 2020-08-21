package com.github.nealel.wordsmasher.corpus.fetcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class BtnApiService {
    public static final String BASE_URL = "https://www.behindthename.com/api/lookup.json?exact=true&";

    public static Optional<JSONObject> getUsage(String key, CloseableHttpClient httpClient, String name) throws IOException {
        log.info("Making request for {}", name);
        String url = BASE_URL + "name=" + name + "&key=" + key;
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        if (okResponse(response)) {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            log.info("Response was {}", jsonResponse);
            JSONObject jsonObject = (JSONObject) new JSONArray(jsonResponse).get(0);
            return Optional.of(jsonObject);
        }
        return Optional.empty();
    }

    public static boolean okResponse(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() != 200) {
            log.error("Response was {}", response.getStatusLine().toString());
            return false;
        }
        return true;
    }
}
