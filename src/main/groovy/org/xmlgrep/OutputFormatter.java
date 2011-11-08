package org.xmlgrep;

import java.io.PrintWriter;
import java.util.Map;

public abstract class OutputFormatter {
    public void startOutput(PrintWriter writer) {
    }

    public void endOutput(PrintWriter writer) {
        writer.flush();
    }

    public abstract void output(Map entry, PrintWriter writer);
}

