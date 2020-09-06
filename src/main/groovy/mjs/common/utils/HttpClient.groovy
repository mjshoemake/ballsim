package mjs.common.utils

import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.HttpResponse


class HttpClient {
    String url = ""

    static String requestGet(String url) {
        org.apache.http.client.HttpClient client = new DefaultHttpClient()
        HttpGet request = new HttpGet(url)
        HttpResponse response = client.execute(request)

        // Get the response
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(
                        response.getEntity().getContent()))

        StringBuilder builder = new StringBuilder()
        String line = ""
        while ((line = rd.readLine()) != null) {
            builder << line
        }
        println builder.toString()
        builder.toString()
    }
}
