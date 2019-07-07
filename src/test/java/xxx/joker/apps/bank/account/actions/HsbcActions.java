package xxx.joker.apps.bank.account.actions;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xxx.joker.apps.bank.accountold.account.hsbc.HsbcManager;
import xxx.joker.apps.bank.accountold.account.hsbc.model.Movement;
import xxx.joker.libs.core.datetime.JkDates;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.format.JkCsvParser;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStruct;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkConsole.displayColl;

public class HsbcActions {

    static Path BASE = Paths.get("src/test/resources/hsbc");

    static Path CATS_FILE = BASE.resolve("categories.csv");
    static Path RAW_MOVS_FILE = BASE.resolve("sourceMovs.csv");
    static Path PARSED_MOVS_FILE = BASE.resolve("parsedMovs.csv");
    static Path XLS_OUTPATH = BASE.resolve("out/report.xlsx");

//    static String CATS_HEADER = "CAT_NAME|EQUALS|START_WITH|END_WITH|CONTAINS";
//    static String MOVS_HEADER = "DATE|AMOUNT|DESCR|CATEGORIES";

    @Test
    public void doParseHsbcDwCsv() {
        HsbcManager.parseDownloadedCsv(RAW_MOVS_FILE, CATS_FILE, PARSED_MOVS_FILE);
        display("Created file {}", PARSED_MOVS_FILE);
    }

    @Test
    public void doSetMovsCategories() {
        List<Movement> movs = HsbcManager.readParsedCsv(PARSED_MOVS_FILE);
        HsbcManager.setCategoryToMovs(CATS_FILE, movs);
        JkFiles.writeFile(PARSED_MOVS_FILE, JkOutput.formatColl(movs));
        display("Created file {}", PARSED_MOVS_FILE);
        showMovsWithoutCat();
    }

    @Test
    public void doCreateXls() {
        List<Movement> movs = HsbcManager.readParsedCsv(PARSED_MOVS_FILE);
        HsbcManager.createXlsStats(XLS_OUTPATH, movs);
        display("Created file {}", XLS_OUTPATH);
    }

    @Test
    public void orderCatsCsv() {
        List<String> lines = JkFiles.readLines(CATS_FILE);
        lines.removeIf(s -> s.startsWith("#"));
        lines = JkStreams.sortUniq(lines, String::compareToIgnoreCase);
        lines.add(0, "#CAT_NAME|EQUALS|START_WITH|END_WITH|CONTAINS");
        JkFiles.writeFile(CATS_FILE, lines);
        display("Ordered categories file {}", CATS_FILE);
        displayColl(lines);
    }

    @Test
    public void showDupMovsHsbcCsv() {
        List<String> lines = JkFiles.readLines(RAW_MOVS_FILE, true, true);
        display("DUPLICATES:");
        JkStruct.getDuplicates(lines).forEach(l -> {
            String str = l.substring(0, 10);
            LocalDate ld = JkDates.toDate(str, "dd/MM/yyyy");
            display("{}   {}", ld.getDayOfWeek(), l);
        });
    }

    @Test
    public void showMovsWithoutCat() {
        List<Movement> movs = JkCsvParser.parseCsv(PARSED_MOVS_FILE, Movement.class);
        List<Movement> noCats = JkStreams.filter(movs, m -> StringUtils.isBlank(m.getCategory()));
        display("no cats: {}", noCats.size());
        display(JkOutput.formatColl(noCats));
    }

}
