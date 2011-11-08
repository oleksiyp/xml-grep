package org.xmlgrep;

import org.xml.sax.helpers.DefaultHandler
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.Attributes
import org.xmlgrep.XmlFilter.FilterResult

public class XMLGrep {
    private String []entryPath;
    private XmlFilter filter = new NoFilter();
    private OutputFormatter formatter = new TabOutputFormatter();
    private boolean logElementsPath = false;

    public XMLGrep(String ...entryPath) {
        this.entryPath = entryPath;
    }
    public XMLGrep setFilter(XmlFilter filter) {
        this.filter = filter;
        return this;
    }
    public XmlFilter getFilter() {
        return this.filter;
    }

    public void parse(URL url, PrintWriter output) {
        InputStream input = url.openStream();
        try {
            parse(input, output);
        } finally {
            input.close();
        }
    }

    public void parse(File file, PrintWriter output) {
        parse(file.toURL(), output);
    }

    public XMLGrep setLogElementsPath(boolean value) {
        this.logElementsPath = value;
        return this;
    }
    public boolean getLogElementsPath() {
        return this.logElementsPath;
    }

    public void parse(InputStream input, PrintWriter output) {
        Map entry = new HashMap();
        Stack<Level> builder = new Stack<Level>();
        def processLevel = {Level level ->
            String path = level.getRecordPath();
            if (path != null) {
                if (path.isEmpty()) {
                    if (filter.filter(entry) != FilterResult.NO) {
                        formatter.output(entry, output);
                        output.flush();
                    }
                    entry.clear();
                } else {
                    entry.put(path, level.text.toString());
                }
            }
        };
        def handler = [
            startDocument : {
                formatter.startOutput(output);
                builder.push(new Level(new String[0]));
            },
            endDocument : {
                formatter.endOutput(output);
            },
            startElement : {
                String uri, String localName, String qName, Attributes attributes ->
                Level level = new Level(builder.peek(), qName);
                builder.push(level);
                if (logElementsPath) {
                    System.err.println(Arrays.toString(level.path));
                }

                String []subpath = level.cut(entryPath);
                if (subpath != null) {
                    StringBuilder path = new StringBuilder();
                    boolean first = true;
                    for (String name : subpath) {
                        if (!first) {
                            path.append('.');
                        }
                        first = false;
                        path.append(name);
                    }
                    level.setRecordPath(path.toString());
                }

                for (int i = 0; i < attributes.getLength(); i++)
                {
                    String name = attributes.getQName(i);
                    String value = attributes.getValue(i);

                    Level attrLevel = new Level(level, name);
                    attrLevel.text.append(value);
                    processLevel(attrLevel);
                }
            },
            endElement : { String uri, String localName, String qName ->
                processLevel(builder.pop());
            },
            characters : { char[] ch, int start, int length ->
                builder.peek().text.append(new String(ch, start, length));
            }
        ];
        SAXParserFactory.newInstance().newSAXParser().parse(input, handler as DefaultHandler);
    }


    public static void main(String[] args) {
        Options options = new Options().parse(args);
        XmlFilterBuilder builder = new XmlFilterBuilder();
        def buildFilter = {List<String> clauses, filterClause ->
            XmlFilterBuilder andBuilder = new XmlFilterBuilder();
            for (String clause : clauses) {
                for (String pair : clause.split(" *, *")) {
                    String []arr = pair.split("="); String field = arr[0]; String value = arr[1];
                    filterClause(field, value);
                }
            }
            builder.addFilter(andBuilder.toAndFilter());
        }

        buildFilter(options.includeFieldContainsTextPair, {field,value->builder.containsText(field, value)});
        buildFilter(options.excludeFieldContainsTextPair, {field,value->builder.doesNotContainText(field, value)});
        buildFilter(options.includeFieldMatchTextPair, {field,value->builder.matchesText(field, value)});
        buildFilter(options.excludeFieldMatchTextPair, {field,value->builder.doesNotMatchText(field, value)});
        buildFilter(options.excludeFieldPatternPair, {field,value->builder.matchesPattern(field, value)});
        buildFilter(options.containsTextPairFilter, {field,value->builder.doesNotMatchPattern(field, value)});
        buildFilter(options.containsTextPairFilter, {field,value->builder.containsText(field, value)});

        XMLGrep grep = new XMLGrep(options.entryPrefix.split("\\."));
        grep.setFilter(builder.toOrFilter());
        if (options.outputType.equals("text") && options.outputFormat != null) {
            grep.setOutputFormatter(new TextOutputFormatter(options.outputFormat));
        }

        File file = new File(options.object)
        if (file.exists()) {
            grep.parse(file, new PrintWriter(System.out));
        } else {
            grep.parse(new URL(options.object), new PrintWriter(System.out));
        }
    }

    public void setOutputFormatter(OutputFormatter formatter) {
        this.formatter = formatter;
    }

}


