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

/**
 * Partitions original XML selections; a passed checkpoint is excluded explicitly, never a failure.
 */
public final class ParallelSuitePlanner {
    private ParallelSuitePlanner() {}

    public static Map<String, List<String>> selected(Path sourceSuite) throws Exception {
        Map<String, List<String>> selectedMethodsByClass = new LinkedHashMap<>();
        for (XmlSuite suite : new Parser(sourceSuite.toString()).parseToList()) {
            collectSelectedMethods(suite, selectedMethodsByClass);
        }
        return selectedMethodsByClass;
    }

    private static void collectSelectedMethods(
            XmlSuite suite, Map<String, List<String>> selectedMethodsByClass) throws Exception {
        if (!suite.getParameters().isEmpty()) {
            throw new IllegalArgumentException("Parameterised suites need an explicit plan");
        }
        for (XmlTest test : suite.getTests()) {
            if (!test.getIncludedGroups().isEmpty()
                    || !test.getExcludedGroups().isEmpty()
                    || !test.getLocalParameters().isEmpty()) {
                throw new IllegalArgumentException(
                        "Groups and test parameters need an explicit plan");
            }
            for (XmlClass xmlClass : test.getXmlClasses()) {
                var selectedMethodNames =
                        selectedMethodsByClass.computeIfAbsent(
                                xmlClass.getName(), className -> new ArrayList<>());
                for (var method : Class.forName(xmlClass.getName()).getMethods()) {
                    Test testAnnotation = method.getAnnotation(Test.class);
                    if (testAnnotation == null || !testAnnotation.enabled()) {
                        continue;
                    }
                    String methodName = method.getName();
                    boolean isIncluded =
                            xmlClass.getIncludedMethods().isEmpty()
                                    || xmlClass.getIncludedMethods().stream()
                                            .anyMatch(
                                                    selection ->
                                                            Pattern.matches(
                                                                    selection.getName(),
                                                                    methodName));
                    boolean isExcluded =
                            xmlClass.getExcludedMethods().stream()
                                    .anyMatch(selection -> Pattern.matches(selection, methodName));
                    if (isIncluded && !isExcluded) {
                        if (selectedMethodNames.contains(methodName)) {
                            throw new IllegalArgumentException(
                                    "Duplicate selection: "
                                            + xmlClass.getName()
                                            + "#"
                                            + methodName);
                        }
                        selectedMethodNames.add(methodName);
                    }
                }
            }
        }
        for (XmlSuite child : suite.getChildSuites()) {
            collectSelectedMethods(child, selectedMethodsByClass);
        }
    }

    private static XmlSuite suite(String name, int workers) {
        var suite = new XmlSuite();
        suite.setName(name);
        suite.setParallel(workers > 1 ? XmlSuite.ParallelMode.CLASSES : XmlSuite.ParallelMode.NONE);
        suite.setThreadCount(workers);
        suite.setDataProviderThreadCount(1);
        suite.setConfigFailurePolicy(XmlSuite.FailurePolicy.CONTINUE);
        suite.setListeners(List.of(BranchIsolationListener.class.getName()));
        var test = new XmlTest(suite);
        test.setName(name);
        test.setPreserveOrder(true);
        test.setParallel(suite.getParallel());
        return suite;
    }

    public static Map<String, Object> plan(
            Path sourceSuite, Path outputDirectory, int workers, Set<String> completedCases)
            throws Exception {
        if (workers < 1 || workers > 6) {
            throw new IllegalArgumentException("Workers must be between 1 and 6");
        }
        var originalSelections = selected(sourceSuite);
        var parallelSuite = suite("Owned fixture classes - parallel", workers);
        var exclusiveSuite = suite("Branch defaults and operator journeys - exclusive", 1);
        List<String> originalCases = new ArrayList<>(),
                remainingCases = new ArrayList<>(),
                completedSelections = new ArrayList<>();
        for (var entry : originalSelections.entrySet()) {
            var includedMethods = new ArrayList<XmlInclude>();
            for (String method : entry.getValue()) {
                String caseId = entry.getKey() + "#" + method;
                originalCases.add(caseId);
                if (completedCases.contains(caseId)) completedSelections.add(caseId);
                else {
                    remainingCases.add(caseId);
                    includedMethods.add(new XmlInclude(method));
                }
            }
            if (includedMethods.isEmpty()) {
                continue;
            }
            var xmlClass = new XmlClass(entry.getKey());
            xmlClass.setIncludedMethods(includedMethods);
            var targetSuite =
                    BranchIsolationListener.independent(entry.getKey())
                            ? parallelSuite
                            : exclusiveSuite;
            targetSuite.getTests().getFirst().getXmlClasses().add(xmlClass);
        }
        if (!new HashSet<>(originalCases).containsAll(completedCases)) {
            throw new IllegalArgumentException(
                    "Checkpoint includes cases outside the source suite");
        }
        Files.createDirectories(outputDirectory);
        Files.writeString(
                outputDirectory.resolve("parallel.xml"),
                parallelSuite.toXml(),
                StandardCharsets.UTF_8);
        Files.writeString(
                outputDirectory.resolve("exclusive.xml"),
                exclusiveSuite.toXml(),
                StandardCharsets.UTF_8);
        var manifest = new LinkedHashMap<String, Object>();
        manifest.put("source", sourceSuite.toString());
        manifest.put("workers", workers);
        manifest.put("originalCases", originalCases);
        manifest.put("completedCases", completedSelections);
        manifest.put("remainingCases", remainingCases);
        manifest.put(
                "parallelClasses",
                parallelSuite.getTests().getFirst().getXmlClasses().stream()
                        .map(XmlClass::getName)
                        .toList());
        manifest.put(
                "exclusiveClasses",
                exclusiveSuite.getTests().getFirst().getXmlClasses().stream()
                        .map(XmlClass::getName)
                        .toList());
        Files.writeString(
                outputDirectory.resolve("plan.json"),
                JSONValue.toJSONString(manifest),
                StandardCharsets.UTF_8);
        return manifest;
    }

    public static void main(String[] args) throws Exception {
        Set<String> completedCases = new HashSet<>();
        if (args.length > 3)
            try (var reader = Files.newBufferedReader(Path.of(args[3]))) {
                for (Object row : (List<?>) new JSONParser().parse(reader))
                    completedCases.add(row.toString());
            }
        var planManifest =
                plan(Path.of(args[0]), Path.of(args[1]), Integer.parseInt(args[2]), completedCases);
        System.out.println(
                "Completed="
                        + ((List<?>) planManifest.get("completedCases")).size()
                        + " Remaining="
                        + ((List<?>) planManifest.get("remainingCases")).size()
                        + " Workers="
                        + args[2]);
    }
}
