package org.xmlgrep;

import java.util.Map;

public abstract class XmlFilter {
    public enum FilterResult {
        YES, NO, PASS
    }
    public abstract FilterResult filter(Map entry);
}

