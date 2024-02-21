import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main
{
    public static void main(String[] args)
    {
        File sourcesDirectory           = new File("src/");
        File backupDirectory            = new File("backup/");
        File workingDirectory           = new File("").getAbsoluteFile();
        File sourcesFile                        = new File(workingDirectory, "sources").getAbsoluteFile();
        File compilationDirectory   = new File("bin/");
        File lastHashingFile                = new File("lastCompilation.md5");

        var compilationNeeded = true;
        String currentHash = null;
        try
        {
            currentHash = Hashing.hashDirectory(sourcesDirectory.getAbsolutePath(), true);
            if(lastHashingFile.exists()) {
                try(var bufferedReader = new BufferedReader(new FileReader(lastHashingFile))) {
                    var lastHash = bufferedReader.readLine();
                    if(currentHash.contentEquals(lastHash)) {
                        compilationNeeded = !compilationDirectory.exists();
                    }
                }
            }
        }
        catch (IOException eIn)
        {
            throw new RuntimeException(eIn);
        }


        try
        {
            var           libSeperator  = ":";
            if (operatingSystemType().contentEquals("windows"))
            {
                libSeperator = ";";
            }
            if(compilationNeeded)
            {
                removeDirectory(backupDirectory);
                copyDirectory(sourcesDirectory.getAbsolutePath(), backupDirectory.getAbsolutePath());
                removeDirectory(compilationDirectory);
                if (!sourcesDirectory.exists() && !sourcesDirectory.mkdirs())
                {
                    throw new RuntimeException("Cannot make sources folder !");
                }
                List<String> sourcesFiles = checkSources(sourcesDirectory);
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sourcesFile)))
                {
                    for (var sourceFile : sourcesFiles)
                    {
                        bufferedWriter.write(sourceFile + "\n");
                    }
                }

                StringBuilder processReport = new StringBuilder();
                var classpath = " -cp " + workingDirectory.getAbsolutePath() + libSeperator + new File("lib/" + operatingSystemType() + "/jcurses.jar").getAbsolutePath();

                if (!compilationDirectory.exists() && !compilationDirectory.mkdirs())
                {
                    throw new RuntimeException("Cannot make sources compilation folder !");
                }
                var command = "javac" + classpath + " -d " + compilationDirectory.getAbsolutePath() + " @" + sourcesFile.getAbsolutePath();
                if (operatingSystemType().contentEquals("windows"))
                {
                    command = command.replace("\\", "/");
                }

                var success = runProcessIntoAnotherTerminal(processReport, workingDirectory, command);
                if(!success) {
                    System.err.println("Java compilation failed !");
                } else {
                    System.out.println("Java compilation success !");
                }
                System.out.println();
                System.out.println(processReport);
                System.out.println();
                System.out.println();

                try(var bufferedWriter = new BufferedWriter(new FileWriter(lastHashingFile))) {
                    bufferedWriter.write(currentHash);
                }
            }

            var processReport = new StringBuilder();
            var classpath = " -cp " + compilationDirectory.getAbsolutePath() + libSeperator + new File("lib/" + operatingSystemType() + "/*").getAbsolutePath();
            var command =  "java" + classpath + " connectfour.app.ConnectFour";
            if(operatingSystemType().contentEquals("windows")) {
                command = command.replace("\\", "/");
            }

            var success = runProcessIntoAnotherTerminal(processReport, workingDirectory, command);
            if(!success) {
                System.err.println("Java compilation failed !");
            } else {
                System.out.println("Java compilation success !");
            }
            System.out.println();
            System.out.println(processReport);
            System.out.println();
            System.out.println();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void copyDirectory(String from, String to) throws IOException
    {
        Files.walk(Paths.get(from)).forEach(source ->
        {
            Path destination = Paths.get(to, source.toString().substring(from.length()));
            try
            {
                Files.copy(source, destination);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    public static void removeDirectory(File directory) throws IOException
    {
        if (directory.exists() && directory.isDirectory())
        {
            Path dir = Paths.get("path"); //path to the directory
            Files.walk(Path.of(directory.getAbsolutePath())) // Traverse the file tree in depth-first order
                    .sorted(Comparator.reverseOrder()).forEach(path ->
                    {
                        try
                        {
                            System.out.println("Deleting: " + path);
                            Files.delete(path);  //delete each file or directory
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public static List<String> checkSources(File rootIn) throws Exception
    {
        return checkSources(rootIn, rootIn, new ArrayList<>());
    }

    public static List<String> checkSources(File fileIn, File rootIn, List<String> filesListIn) throws Exception
    {
        if (fileIn.isFile() && fileIn.getAbsolutePath().endsWith(".java"))
        {
            filesListIn.add(fileIn.getAbsolutePath().replace(rootIn.getAbsolutePath() + "/", ""));
            return filesListIn;
        }

        var files = fileIn.listFiles();
        if (files == null)
        {
            return filesListIn;
        }

        for (var file : files)
        {
            checkSources(file, rootIn, filesListIn);
        }

        return filesListIn;
    }

    private static boolean runProcessIntoAnotherTerminal(StringBuilder processReport, File workingDirectory, String... commands) throws Exception
    {
        List<String> commandsList = new ArrayList<>();

        if(operatingSystemType().contentEquals("windows")) {
            commandsList.add("cmd");
            commandsList.add("/c");
            commandsList.add("start");
            commandsList.add("cmd.exe");
            commandsList.add("/k");
            commandsList.add("\"" + commandOf(commands) + "\"");
        } else {
            commandsList.add("gnome-terminal");
            commandsList.add("--");
            commandsList.add("bash");
            commandsList.add("-c");
            commandsList.addAll(Arrays.asList(commands));
            commandsList.add(";");
            commandsList.add("exec");
            commandsList.add("bash");
        }

        return runProcess(processReport, workingDirectory, commandsList);
    }

    private static <T> List<T> toList(T... objects) {
        return Arrays.asList(objects);
    }

    private static boolean runProcess(final StringBuilder processReport, File workingDirectory, String... commands)
    {
        return runProcess(processReport, workingDirectory, toList(commands));
    }

    private static boolean runProcess(StringBuilder processReport, File workingDirectory, List<String> commandList)
    {
        final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        processBuilder.directory(workingDirectory);

        var indent = makeSectionReport(0, processReport, "NEW PROCESS LAUNCHED", 64);

        Process process = null;
        try
        {
            appendProcessExecutionReportHeader(indent, processReport, workingDirectory, commandList);
            process = processBuilder.start();
            int waitForValue = process.waitFor();
            processReport.append(start(indent) + "- Wait for value : " + waitForValue + "\n");
            appendProcessExecutionReportDetails(indent, processReport, process);
        }
        catch (IOException e)
        {
            processReport.append(start(indent) + "- IOException : " + e.getMessage());
        }
        catch (InterruptedException e)
        {
            processReport.append(start(indent) + "- InterruptedException : " + e.getMessage());
        }

	    return process != null && process.exitValue() >= 0;
    }

    private static Collection<String> linesFromStream(InputStream inputStream) throws IOException
    {
        List<String> lineList = new ArrayList<>();

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                lineList.add(line);
            }
        }

	    return lineList;
    }

    private static String commandOf(String... commands)
    {
        StringBuilder commandString = new StringBuilder();

        int i = 0;
        for (var command : commands)
        {
            if (i > 0)
            {
                commandString.append(" ");
            }
            commandString.append(command);
            i++;
        }

        return commandString.toString();
    }

    private static String commandOf(Collection<String> commands)
    {
        StringBuilder commandString = new StringBuilder();

        int i = 0;
        for (var command : commands)
        {
            if (i > 0)
            {
                commandString.append(" ");
            }
            commandString.append(command);
            i++;
        }

        return commandString.toString();
    }

    private static String operatingSystemType() {
        String operatingSystem = operatingSystem().toLowerCase().replace(" ", "");

        if(operatingSystem.contains("windows")) {
            return "windows";
        } else if(operatingSystem.contains("mac")) {
            return "mac";
        } else if(operatingSystem.contains("linux")) {
            return "linux";
        }

        return operatingSystem;
    }

    private static String operatingSystem() {
        return  System.getProperty("os.name");
    }

    private static String start(int indent) {
        return "\t".repeat(indent);
    }

    private static int makeSectionReport(int indent, StringBuilder stringBuilder, String name, int width) {
        stringBuilder.repeat(start(indent) + "-", width);
        stringBuilder.append("\n");
        name = "  " + name + "  ";
        int sideWidth = (64-name.length())/2;
        stringBuilder.repeat(start(indent) + "-", sideWidth);
        stringBuilder.append(name);
        stringBuilder.repeat(start(indent) + "-", sideWidth);
        int currentWidth = sideWidth * 2 + name.length();
        if(64 -currentWidth > 0 ) {
            stringBuilder.repeat(start(indent) + "-", 64 - currentWidth);
        }
        stringBuilder.append("\n");
        stringBuilder.repeat(start(indent) + "-", width);
        stringBuilder.append("\n");

        return indent + 1;
    }

    private static <T> int makeReportEnumeration(int indent, StringBuilder stringBuilder, String name, Collection<T> collection) {
        var currentIndent = indent;
        stringBuilder.append(start(currentIndent) + name + " : \n");
        currentIndent ++;
        for(var object : collection) {
            stringBuilder.append(start(currentIndent) + "- " + object + "\n");
        }

        return indent;
    }

    private static <T> int appendOptional(int indent, StringBuilder stringBuilder, String name, Optional<T> optional) {
        if(optional == null || !optional.isPresent()) {
            return indent;
        }

        stringBuilder.append(start(indent) + name + " : " + optional.get() + "\n");

        return indent;
    }

    private static int appendProcessExecutionReportHeader(int indent, StringBuilder stringBuilder, File workingDirectoryIn, Collection<String> commands) {
        stringBuilder.append(start(indent) + commandOf(commands) + " in " + workingDirectoryIn.getAbsolutePath() + " :\n");

        return indent + 1;
    }

    private static int appendProcessExecutionReportDetails(int indent, StringBuilder stringBuilder, Process process) throws IOException
    {
        stringBuilder.append(start(indent) + "Received :\n");
        indent ++;
        indent = appendOptional(indent, stringBuilder, "Command", process.info().command());
        var optionalArguments = process.info().arguments();
        if(optionalArguments.isPresent()) {
            indent = makeReportEnumeration(indent, stringBuilder, "Arguments", toList(optionalArguments.get()));
        }
        indent = appendOptional(indent, stringBuilder, "Command line", process.info().commandLine());
        indent --;

        stringBuilder.append(start(indent) + "Launched :\n");
        indent ++;
        indent = appendOptional(indent, stringBuilder, "- At", process.info().startInstant());
        indent = appendOptional(indent, stringBuilder, "- During", process.info().totalCpuDuration());
        indent = appendOptional(indent, stringBuilder, "- From user", process.info().user());
        stringBuilder.append(start(indent) + "- In PID : " + process.pid() + "\n");
        stringBuilder.append(start(indent) + "- On operating system : " + operatingSystem() + " (" + operatingSystemType() + ")\n");
        if(process.supportsNormalTermination()) {
            stringBuilder.append(start(indent) + "- With supports normal termination\n");
        } else {
            stringBuilder.append(start(indent) + "- Without supports normal termination\n");
        }
        indent  --;

        stringBuilder.append(start(indent) + "States :\n");
        indent ++;
        indent = makeReportEnumeration(indent, stringBuilder, "Errors", linesFromStream(process.getErrorStream()));
        indent = makeReportEnumeration(indent, stringBuilder, "Inputs", linesFromStream(process.getInputStream()));
        if(process.isAlive()) {
            stringBuilder.append(start(indent) + "- State : is alive !\n");
        } else {
            stringBuilder.append(start(indent) + "- State : is not alive !\n");
        }
        stringBuilder.append(start(indent) + "- Exit value : " + process.exitValue());

        return indent;
    }
}