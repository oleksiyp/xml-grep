package org.xmlgrep;

import java.util.Map;

public class NoFilter extends XmlFilter {
    public FilterResult filter(Map entry) {
        return FilterResult.YES;
    }
}

