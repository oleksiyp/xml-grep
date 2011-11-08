package org.xmlgrep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class XmlFilterBuilder {
    private List<XmlFilter> chain = new ArrayList();

    public XmlFilterBuilder matchesPattern(final String field, final String regular) {
        final Pattern pattern = Pattern.compile(regular);
        chain.add(new XmlFilter() {
            @Override
            public FilterResult filter(Map entry) {
                String value = (String) entry.get(field);
                if (value == null) {
                    return FilterResult.PASS;
                }
                return pattern.matcher(value).find() ? FilterResult.YES : FilterResult.NO;
            }
        });
        return this;
    }

    public XmlFilterBuilder doesNotMatchPattern(final String field, final String regular) {
        final Pattern pattern = Pattern.compile(regular);
        chain.add(new XmlFilter() {
            @Override
            public FilterResult filter(Map entry) {
                String value = (String) entry.get(field);
                if (value == null) {
                    return FilterResult.PASS;
                }
                return !pattern.matcher(value).find() ? FilterResult.YES : FilterResult.NO;
            }
        });
        return this;
    }

    public XmlFilterBuilder containsText(final String field, final String text) {
        chain.add(new XmlFilter() {
            @Override
            public FilterResult filter(Map entry) {
                String value = (String) entry.get(field);
                if (value == null) {
                    return FilterResult.PASS;
                }
                return value.contains(text) ? FilterResult.YES : FilterResult.NO;
            }
        });
        return this;
    }

    public XmlFilterBuilder doesNotContainText(final String field, final String text) {
        chain.add(new XmlFilter() {
            @Override
            public FilterResult filter(Map entry) {
                String value = (String) entry.get(field);
                if (value == null) {
                    return FilterResult.PASS;
                }
                return value.contains(text) ? FilterResult.YES : FilterResult.NO;
            }
        });
        return this;
    }

    public XmlFilterBuilder matchesText(final String field, final String text) {
        chain.add(new XmlFilter() {
            @Override
            public FilterResult filter(Map entry) {
                String value = (String) entry.get(field);
                if (value == null) {
                    return FilterResult.PASS;
                }
                return value.equals(text) ? FilterResult.YES : FilterResult.NO;
            }
        });
        return this;
    }

    public XmlFilterBuilder doesNotMatchText(final String field, final String text) {
        chain.add(new XmlFilter() {
            @Override
            public FilterResult filter(Map entry) {
                String value = (String) entry.get(field);
                if (value == null) {
                    return FilterResult.PASS;
                }
                return !value.equals(text) ? FilterResult.YES : FilterResult.NO;
            }
        });
        return this;
    }

    public XmlFilter toOrFilter() {
        return new XmlFilter() {
            @Override
            public FilterResult filter(Map entry) {
                if (chain.isEmpty()) {
                    return FilterResult.YES;
                }
                for (XmlFilter filter : chain) {
                    FilterResult result = filter.filter(entry);
                    if (result == FilterResult.YES) {
                        return result;
                    }
                }
                return FilterResult.NO;
            }
        };
    }

    public void addFilter(XmlFilter filter) {
        chain.add(filter);
    }

    public XmlFilter toAndFilter() {
        return new XmlFilter() {
            @Override
            public FilterResult filter(Map entry) {
                if (chain.isEmpty()) {
                    return FilterResult.YES;
                }
                for (XmlFilter filter : chain) {
                    FilterResult result = filter.filter(entry);
                    if (result == FilterResult.NO) {
                        return result;
                    }
                }
                return FilterResult.YES;
            }
        };
    }
}
