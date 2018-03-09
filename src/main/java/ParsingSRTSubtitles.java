import com.github.dnbn.submerge.api.parser.SRTParser;
import com.github.dnbn.submerge.api.subtitle.srt.SRTLine;
import com.github.dnbn.submerge.api.subtitle.srt.SRTSub;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

public class ParsingSRTSubtitles {

    private String sourceFilesPath;
    private String[] fileExtensions;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Required parameter missing");
            System.out.println("java -cp \"SRT2Text.jar;lib/*\" ParsingSRTSubtitles app.properties");
        } else {
            ParsingSRTSubtitles parsingSTR = new ParsingSRTSubtitles();
            parsingSTR.init();
            parsingSTR.start(args);
        }
        System.exit(0);
    }

    /**********************************************************/
    /* This program doesn't have any specific initialization. */
    /* If it did, it could go here.                           */
    /**********************************************************/
    public void init() {
    }

    public void start(String args[]) throws IOException {
        //        String fileAppProp = args[0];
        //the base folder is ./, the root of the main.properties file
        String path = args[0]; //"./main.properties";

        readSourcePathFromAppProp(path);

        File dir = new File(sourceFilesPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            processFiles(directoryListing);
        }
    }

    private void processFiles(File[] files) throws IOException {
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                processFiles(file.listFiles()); // Calls same method again.
            } else if (file.isFile()) {
                String ext = FilenameUtils.getExtension(file.getName());
                if (Arrays.asList(fileExtensions).contains(ext)) {
                    System.out.println("File: " + file.getName());
                    parse(file.getAbsolutePath());
                }
            }
        }
    }

    private void readSourcePathFromAppProp(String path) throws IOException {
        //to load application's properties, we use this class
        Properties mainProperties = new Properties();
        FileInputStream file;

        //load the file handle for main.properties
        file = new FileInputStream(path);

        //load all the properties from this file
        mainProperties.load(file);

        //we have loaded the properties, so close the file handle
        file.close();

        //retrieve the property we are intrested, the app.version
        sourceFilesPath = mainProperties.getProperty("sourceFilesPath");
        fileExtensions = mainProperties.getProperty("fileExtension").split(",");
    }

    private void parse(String sourceFileName) throws IOException {
        File fileSRT = new File(sourceFileName);
        SRTParser parser = new SRTParser();

        SRTSub subtitle = parser.parse(fileSRT);
        Set<SRTLine> srtLines = subtitle.getLines();

        StringBuilder b = new StringBuilder();

        srtLines.forEach(
//                (str) -> {
//                    str.getTextLines().stream().map(sentence -> sentence.toString()).collect(Collectors.joining(" "));
//                }

                (str) -> {
                    str.getTextLines().forEach(
                            s -> {
                                b.append(s.replaceAll("[\r\n]+", " ")).append(" ");
                            }
//                            s -> {
//                                System.out.println(s.replaceAll("[\r\n]+", " "));
//                            }
                    );
                }

        );

        String targetFileName = fileSRT.getAbsolutePath() + ".txt";
        File txtFile = new File(targetFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile))) {
            writer.write(b.toString());
        }
    }
}
