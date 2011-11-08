package org.xmlgrep;

import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class TabOutputFormatter extends OutputFormatter {
    @Override
    public void output(Map entry, PrintWriter writer) {
        entry = new TreeMap(entry);
        boolean first = true;
        for (Object key : entry.keySet()) {
            if (!first) {
                writer.print('\t');
            }
            first = false;
            String value = (String) entry.get(key);
            writer.print(value.trim());
        }
        writer.println();
    }
}
