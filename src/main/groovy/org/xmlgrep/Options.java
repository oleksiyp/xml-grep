package org.xmlgrep;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.List;

public class Options {
    @Option(name="-p", usage="Match pattern", aliases = "--pattern", metaVar="field=value", multiValued=true)
    List<String> includeFieldPatternPair = new ArrayList<String>();

    @Option(name="-ep", usage="Does not match pattern", aliases = "--exclude-pattern", metaVar="field=value", multiValued=true)
    List<String> excludeFieldPatternPair = new ArrayList<String>();

    @Option(name="-c", usage="Matches text", aliases = "--contain", metaVar="field=value", multiValued=true)
    List<String> includeFieldContainsTextPair = new ArrayList<String>();

    @Option(name="-ec", usage="Does not match text", aliases = "--exclude-contain", metaVar="field=value", multiValued=true)
    List<String> excludeFieldContainsTextPair = new ArrayList<String>();

    @Option(name="-m", usage="Contains text", aliases = "--match", metaVar="field=value", multiValued=true)
    List<String> includeFieldMatchTextPair = new ArrayList<String>();

    @Option(name="-em", usage="Does not contains text", aliases = "--exclude-match", metaVar="field=value", multiValued=true)
    List<String> excludeFieldMatchTextPair = new ArrayList<String>();

    @Option(name="-ot", usage="Type of output: text, xml, json", aliases = "--output-type")
    String outputType = "text";

    @Option(name="-of",
        usage="Output format - any expression like: \"#{value1} #{subelement.value2}\"",
        aliases="--output-format")
    String outputFormat;

    @Argument(index=0,
        usage="File or URL to parse",
        metaVar="file/url",
        required=true)
    String object;

    @Argument(index=1,
        usage="Period('.') separated path to the element where xml extraction starts e.g. 'ROOT Element'.'subelement'.'subsubelement'",
        metaVar="entryPath",
        required=true)
    String entryPrefix;

    @Argument(index=2,
        usage="Pairs field=value to match field(which is indeed path inside entry) and value - value to match",
        metaVar="matchesField",
        multiValued=true)
    List<String> containsTextPairFilter = new ArrayList<String>();


    public Options() {
    }

    public Options parse(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            usage(parser, e);
        }
        return this;
    }

    private void usage(CmdLineParser parser, CmdLineException e) {
        System.err.println(e.getMessage());
        System.err.println("java -jar xmlgrep.jar [options...] file/url entryPath [matchesField...]");
        parser.printUsage(System.err);
        System.exit(1);
    }
}
