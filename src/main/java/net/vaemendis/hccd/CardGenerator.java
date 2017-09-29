package net.vaemendis.hccd;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CardGenerator {

    private static final String GENERATED_SUFFIX = "-GENERATED";

    public static void generateCards(WatchedFiles projectFiles, UserConfiguration config) throws IOException {
        Hccd.log("Generating card sheet file...");
        if (!projectFiles.getCsvFile().isFile()) {
            Hccd.log("CSV file does not exist. Generation aborted.");
        } else {
            String root = FilenameUtils.getBaseName(projectFiles.getHtmlFile().getName());
            File target = new File(projectFiles.getParentDir(), root + GENERATED_SUFFIX + ".html");

            String html = FileUtils.readFileToString(projectFiles.getHtmlFile());
            Document doc = Jsoup.parse(html);
            String card = doc.select(".card").first().outerHtml();
            Template template = Mustache.compiler().escapeHTML(false).defaultValue("[NOT FOUND]").compile(card);
            Iterable<CSVRecord> records = getData(projectFiles.getCsvFile(), config);

            // filter cards
            List<Integer> filter = config.getCardFilter();
            List<CSVRecord> recordList = new ArrayList<>();
            int index = 1;
            for (CSVRecord record : records) {
                if (filter.size() == 0 || filter.contains(index)) {
                    recordList.add(record);
                }
                index++;
            }



            StringBuilder sb = new StringBuilder();
            writeHeader(sb, projectFiles.getCssFile().getName());

            Iterator<CSVRecord> recordIter = recordList.iterator();
            int rows = config.getGridRowNumber();
            int cols = config.getGridColNumber();

            recordLoop:
            {
                while (recordIter.hasNext()) {
                    sb.append("<table class=\"page\">");
                    for (int i = 0; i < rows; i++) {
                        sb.append("<ht>");
                        for (int j = 0; j < cols; j++) {
                            if (recordIter.hasNext()) {
                                sb.append("<td>");
                                sb.append(template.execute(recordIter.next().toMap()));
                                sb.append("</td>");
                            } else {
                                sb.append("</tr></table>");
                                break recordLoop;
                            }
                        }
                        sb.append("</tr>");
                    }
                    sb.append("</table>");
                }
            }
            writeFooter(sb);

            FileUtils.writeStringToFile(target, sb.toString());
            Hccd.log("Card sheet file written to " + target.getPath());
        }
    }

    private static void writeHeader(StringBuilder sb, String cssFilePath) {
        sb.append("<!doctype html>" +
                "<html>" +
                "<head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></meta>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"");
        sb.append(cssFilePath);
        sb.append("\"><style>" +
                "body {\n" +
                "    margin: 10mm;\n" +
                "}\n" +
                "\n" +
                "table.page {\n" +
                "    border: 0mm;\n" +
                "    page-break-after: always;    \n" +
                "    border-spacing: 0;\n" +
                "    border-collapse: collapse;\n" +
                "    display:block;        \n" +
                "    clear: both;\n" +
                "}\n" +
                "\n" +
                "table.page td {\n" +
                "    padding: 0;\n" +
                "}\n" +
                "</style>\n</head><body>");
    }

    private static void writeFooter(StringBuilder sb) {
        sb.append("</body></html>");

    }

    private static List<CSVRecord> getData(File csvFile, UserConfiguration config) throws IOException {
        List<CSVRecord> recordList = new ArrayList<>();
        Iterable<CSVRecord> records;
        if (config.useExcelFormat()) {
            try(Reader reader = new InputStreamReader(new BOMInputStream(new FileInputStream(csvFile)), StandardCharsets.UTF_8)){
                records = CSVFormat.EXCEL.withDelimiter(config.getDelimiter()).withFirstRecordAsHeader().parse(reader);
                for (CSVRecord record : records) {
                    recordList.add(record);
                }
            }
        }else{
            try (Reader in = new FileReader(csvFile)){
                records = CSVFormat.RFC4180.withDelimiter(config.getDelimiter()).withFirstRecordAsHeader().parse(in);
                for (CSVRecord record : records) {
                    recordList.add(record);
                }
            }
        }
        return recordList;
    }

}
