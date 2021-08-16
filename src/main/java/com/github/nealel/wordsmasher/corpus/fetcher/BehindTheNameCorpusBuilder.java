package com.github.nealel.wordsmasher.corpus.fetcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;

import static com.github.nealel.wordsmasher.corpus.fetcher.BtnApiService.getUsage;
import static com.github.nealel.wordsmasher.corpus.fetcher.BtnFileLoader.loadRaw;
import static com.github.nealel.wordsmasher.corpus.fetcher.BtnFileWriter.responseToFile;
import static java.lang.Thread.sleep;

/**
 * Stand-alone command-line runner class that fetches name data from the behindTheName.com API
 *  Used to populate the initial corpus files as a one-off job
 *
 * Raw name lists can be downloaded from behindTheName as a single export, but that doesn't contain
 * any metadata about where the name comes from (it's "usage"). This class iterates through each name in that list
 * and calls the behindthename api to retrieve usage information
 *
 * Because of rate limits, this takes about 2 days to run a full batch because of API rate limits
 */
@Slf4j
public class BehindTheNameCorpusBuilder {

    private static final int SECS_BETWEEN_REQUESTS = 3;

    /**
     * @param args
     *    0 KEY:  an BehindTheName API key, obtainable by registering at their website
     *    1 FROM LETTER (optional): a letter or string to start fetching from
     *            this can be used to split the 2-day job into manageable chunks
     *            inclusive
     *            defaults to 'a'
     *    2 TO LETTER (optional): as from, a letter or string to stop fetching
     *          exclusive
     *          defaults to zzz
     */
    public static void main(String[] args) throws Exception {
        String key = args[0];
        String from = args.length > 1 ? args[1] : "a";
        String to = args.length > 2 ? args[2] : "zzz";

        List<String> rawNames = loadRaw(from, to);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        for (String name : rawNames) {
            Optional<JSONObject> response = getUsage(key, httpClient, name);
            if (response.isPresent()) {
                responseToFile(name, response.get());
            }
            sleep(SECS_BETWEEN_REQUESTS * 1000);
        }
    }

}
