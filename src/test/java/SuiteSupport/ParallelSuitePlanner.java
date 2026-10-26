package SuiteSupport;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.Test;
import org.testng.xml.*;
import org.testng.xml.internal.Parser;

/** Partitions original XML selections; a passed checkpoint is excluded explicitly, never a failure. */
public final class ParallelSuitePlanner {
    private ParallelSuitePlanner() {}

    public static Map<String,List<String>> selected(Path source) throws Exception {
        Map<String,List<String>> result=new LinkedHashMap<>();
        for(XmlSuite suite:new Parser(source.toString()).parseToList()) collect(suite,result);
        return result;
    }

    private static void collect(XmlSuite suite,Map<String,List<String>> result) throws Exception {
        if(!suite.getParameters().isEmpty()) throw new IllegalArgumentException("Parameterised suites need an explicit plan");
        for(XmlTest test:suite.getTests()) {
            if(!test.getIncludedGroups().isEmpty()||!test.getExcludedGroups().isEmpty()||!test.getLocalParameters().isEmpty())
                throw new IllegalArgumentException("Groups and test parameters need an explicit plan");
            for(XmlClass xml:test.getXmlClasses()) {
                var names=result.computeIfAbsent(xml.getName(),k->new ArrayList<>());
                for(var method:Class.forName(xml.getName()).getMethods()) {
                    Test annotation=method.getAnnotation(Test.class);
                    if(annotation==null||!annotation.enabled())continue;
                    String name=method.getName();
                    boolean included=xml.getIncludedMethods().isEmpty()||xml.getIncludedMethods().stream().anyMatch(x->Pattern.matches(x.getName(),name));
                    boolean excluded=xml.getExcludedMethods().stream().anyMatch(x->Pattern.matches(x,name));
                    if(included&&!excluded) {
                        if(names.contains(name))throw new IllegalArgumentException("Duplicate selection: "+xml.getName()+"#"+name);
                        names.add(name);
                    }
                }
            }
        }
        for(XmlSuite child:suite.getChildSuites())collect(child,result);
    }

    private static XmlSuite suite(String name,int workers) {
        var suite=new XmlSuite();suite.setName(name);
        suite.setParallel(workers>1?XmlSuite.ParallelMode.CLASSES:XmlSuite.ParallelMode.NONE);
        suite.setThreadCount(workers);suite.setDataProviderThreadCount(1);
        suite.setConfigFailurePolicy(XmlSuite.FailurePolicy.CONTINUE);
        suite.setListeners(List.of(BranchIsolationListener.class.getName()));
        var test=new XmlTest(suite);test.setName(name);test.setPreserveOrder(true);test.setParallel(suite.getParallel());
        return suite;
    }

    public static Map<String,Object> plan(Path source,Path output,int workers,Set<String> completed) throws Exception {
        if(workers<1||workers>6)throw new IllegalArgumentException("Workers must be between 1 and 6");
        var original=selected(source);var parallel=suite("Owned fixture classes - parallel",workers);
        var exclusive=suite("Branch defaults and operator journeys - exclusive",1);
        List<String> all=new ArrayList<>(),remaining=new ArrayList<>(),skipped=new ArrayList<>();
        for(var entry:original.entrySet()) {
            var methods=new ArrayList<XmlInclude>();
            for(String method:entry.getValue()) {
                String key=entry.getKey()+"#"+method;all.add(key);
                if(completed.contains(key))skipped.add(key);
                else { remaining.add(key);methods.add(new XmlInclude(method)); }
            }
            if(methods.isEmpty())continue;
            var xml=new XmlClass(entry.getKey());xml.setIncludedMethods(methods);
            var target=BranchIsolationListener.independent(entry.getKey())?parallel:exclusive;
            target.getTests().getFirst().getXmlClasses().add(xml);
        }
        if(!new HashSet<>(all).containsAll(completed))throw new IllegalArgumentException("Checkpoint includes cases outside the source suite");
        Files.createDirectories(output);
        Files.writeString(output.resolve("parallel.xml"),parallel.toXml(),StandardCharsets.UTF_8);
        Files.writeString(output.resolve("exclusive.xml"),exclusive.toXml(),StandardCharsets.UTF_8);
        var manifest=new LinkedHashMap<String,Object>();manifest.put("source",source.toString());manifest.put("workers",workers);
        manifest.put("originalCases",all);manifest.put("completedCases",skipped);manifest.put("remainingCases",remaining);
        manifest.put("parallelClasses",parallel.getTests().getFirst().getXmlClasses().stream().map(XmlClass::getName).toList());
        manifest.put("exclusiveClasses",exclusive.getTests().getFirst().getXmlClasses().stream().map(XmlClass::getName).toList());
        Files.writeString(output.resolve("plan.json"),JSONValue.toJSONString(manifest),StandardCharsets.UTF_8);
        return manifest;
    }

    public static void main(String[] args) throws Exception {
        Set<String> completed=new HashSet<>();
        if(args.length>3)try(var reader=Files.newBufferedReader(Path.of(args[3]))) {
            for(Object row:(List<?>)new JSONParser().parse(reader))completed.add(row.toString());
        }
        var result=plan(Path.of(args[0]),Path.of(args[1]),Integer.parseInt(args[2]),completed);
        System.out.println("Completed="+((List<?>)result.get("completedCases")).size()+" Remaining="+((List<?>)result.get("remainingCases")).size()+" Workers="+args[2]);
    }
}
