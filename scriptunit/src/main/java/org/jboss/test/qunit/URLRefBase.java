package org.jboss.test.qunit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class URLRefBase {

    public URLRefBase() {
        super();
    }

    protected String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        // TODO - detect charset
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
    
        while ((inputLine = reader.readLine()) != null) {
            content.append(inputLine).append('\n');
        }
        reader.close();
        return content.toString();
    }


}