package org.xmlgrep;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextOutputFormatter extends OutputFormatter {
    public String []format;

    public String []splitExpr(String format) {
        if (format == null) {
            return null;
        }
        format = format.replace("\\t", "\t").replace("\\n", "\n").replace("\\\\", "\\");
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("#\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(format);
        StringBuffer buf = new StringBuffer();
        while (matcher.find()) {
            buf.setLength(0);
            matcher.appendReplacement(buf, "");
            result.add(buf.toString());
            result.add(matcher.group(1));
        }
        buf.setLength(0);
        matcher.appendTail(buf);
        if (buf.length() > 0) {
            result.add(buf.toString());
        }

        return result.toArray(new String[result.size()]);
    }

    public TextOutputFormatter(String format) {
        this.format = splitExpr(format);
    }

    @Override
    public void output(Map entry, PrintWriter writer) {
        for (int i = 0; i < format.length; i++) {
            if (i % 2 == 0) {
                writer.print(format[i]);
            } else {
                if (entry.containsKey(format[i])) {
                    String value = (String) entry.get(format[i]);
                    writer.print(value.trim());
                }
            }
        }
        writer.println();
    }
}
