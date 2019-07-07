package xxx.joker.apps.bank.accountold.account.hsbc;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import xxx.joker.apps.bank.accountold.account.hsbc.model.Category;
import xxx.joker.apps.bank.accountold.account.hsbc.model.Condition;
import xxx.joker.apps.bank.accountold.account.hsbc.model.Movement;
import xxx.joker.apps.bank.accountold.account.hsbc.views.MovsView;
import xxx.joker.apps.bank.accountold.account.hsbc.views.StatElem;
import xxx.joker.libs.core.datetime.JkDates;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.format.JkCsvParser;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.objects.JkArea;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.excel2.JkSheet;
import xxx.joker.libs.excel2.JkWorkbook;
import xxx.joker.libs.excel2.JkWorkbookFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

public class HsbcManager {

    private static final String XLS_TEMPLATE_PATH = "hsbc/outTemplate.xlsx";

    public static List<Movement> parseDownloadedCsv(Path rawCsvPath, Path categoriesPath) {
        List<Movement> movs = readMovsHsbcCsv(rawCsvPath);
        List<Category> cats = readCategories(categoriesPath);
        setCategoryToMovs(cats, movs);
        return movs;
    }

    public static Path parseDownloadedCsv(Path rawCsvPath, Path categoriesPath, Path outPath) {
        List<Movement> movs = parseDownloadedCsv(rawCsvPath, categoriesPath);
        JkFiles.writeFile(outPath, JkOutput.formatColl(movs));
        return outPath;
    }

    public static List<Movement> readParsedCsv(Path parsedCsvPath) {
        return JkCsvParser.parseCsv(parsedCsvPath, Movement.class);
    }

    public static void setCategoryToMovs(Path categoriesPath, List<Movement> movs) {
        List<Category> cats = readCategories(categoriesPath);
        setCategoryToMovs(cats, movs);
    }

    public static void setCategoryToMovs(List<Category> cats, List<Movement> movs) {
        for(Movement mov : movs) {
            mov.setCategory(null);
            for(Category cat : cats) {
                boolean match = cat.match(mov.getDescr());
                if(match) {
                    if(StringUtils.isNotBlank(mov.getCategory()) && !mov.getCategory().equals(cat.getName())) {
                        throw new JkRuntimeException("Mov [{}]; duplicated category [{}]", JkOutput.formatObject(mov), cat.getName());
                    }
                    mov.setCategory(cat.getName());
                }
            }
        }
    }

    public static void createXlsStats(Path outXlsPath, List<Movement> movs) {
        try(JkWorkbook wb = JkWorkbookFactory.create(WorkbookFactory.create(HsbcManager.class.getClassLoader().getResourceAsStream(XLS_TEMPLATE_PATH)))) {
            JkSheet sheet = wb.cloneSheet("format", "Total");
            fillXlsSheet(sheet, movs);

            Map<String, List<Movement>> monthMap = JkStreams.toMap(movs, m -> JkDates.format(m.getDate(), "yyyyMM"));
            List<String> months = JkStreams.sortUniq(monthMap.keySet());
            for (String month : months) {
                sheet = wb.cloneSheet("format", month);
                fillXlsSheet(sheet, monthMap.get(month));
            }

            int index = wb.getWorkbook().getSheetIndex("format");
            wb.getWorkbook().removeSheetAt(index);

            wb.persist(outXlsPath);

        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }

    private static void fillXlsSheet(JkSheet sheet, List<Movement> movs) {
        MovsView movsView = new MovsView(movs);

        int irow = 2;
        int icol = 0;
        List<StatElem> statElems = Arrays.asList(movsView.getStatTotal(), movsView.getStatIn(), movsView.getStatOut());
        for (StatElem elem : statElems) {
            icol = 2;
            sheet.setValue(irow, icol++, elem.getNumMovs());
            sheet.setValue(irow, icol++, elem.getAmountMovs());
            irow++;
        }

        irow = 8;
        int fmtRow = irow;
        int startCol = 1;
        Set<String> catNames = movsView.getByCategory().keySet();
        for (String cat : catNames) {
            icol = startCol;
            StatElem elem = movsView.getByCategory().get(cat);
            sheet.setValue(irow, icol++, cat);
            sheet.setValue(irow, icol++, elem.getNumMovs());
            sheet.setValue(irow, icol++, elem.getAmountMovs());
            irow++;
        }

        for(int nc = startCol; nc < icol; nc++) {
            CellStyle cs = sheet.getCellStyle(fmtRow, nc);
            JkArea area = new JkArea(nc, fmtRow,1, catNames.size());
            sheet.setStyle(area, cs);
        }

        irow = 2;
        fmtRow = irow;
        startCol = 6;
        for (Movement mov : movsView.getMovs()) {
            icol = startCol;
            sheet.setValue(irow, icol++, mov.getDate());
            sheet.setValue(irow, icol++, mov.getAmount());
            sheet.setValue(irow, icol++, mov.getCategory());
            sheet.setValue(irow, icol++, mov.getDescr());
            irow++;
        }

        for(int nc = startCol; nc < icol; nc++) {
            CellStyle cs = sheet.getCellStyle(fmtRow, nc);
            JkArea area = new JkArea(nc, fmtRow, 1, movs.size());
            sheet.setStyle(area, cs);
//            CellStyle cs = sheet.getCellStyle(fmtRow, nc);
//            for(int i = 1, nr = fmtRow + i; i < movs.size(); i++, nr++) {
//                sheet.setStyle(nr, nc, cs);
        }

    }

    private static List<Category> readCategories(Path catFile) {
        List<String> lines = JkFiles.readLinesNotBlank(catFile);
        lines.removeIf(s -> s.startsWith("#"));
        TreeMap<String, Category> cats =  new TreeMap<>();
        for(String line : lines) {
            String[] arr = JkStrings.splitArr(line, "|");
            Condition cond = new Condition(arr[1], arr[2], arr[3], arr[4]);
            String catName = arr[0];
            cats.putIfAbsent(catName, new Category(catName));
            cats.get(catName).getConditions().add(cond);
        }
        return JkConvert.toList(cats.values());
    }

    private static List<Movement> readMovsHsbcCsv(Path movsFile) {
        try {
            List<String> lines = JkFiles.readLines(movsFile, true, true);
            List<Movement> movs = new ArrayList<>();
            for (String line : lines) {
//                line = line.replaceAll("^[^\\d]", "");
                int idxA = line.indexOf(",");
                int idxB = line.replaceAll("\"$", "").lastIndexOf("\"");
                Movement mov = new Movement();
                mov.setDate(JkDates.toDate(line.substring(0, idxA), "dd/MM/yyyy"));
                mov.setDescr(line.substring(idxA + 1, idxB).replaceAll(",$", ""));
                String strAmount = line.substring(idxB + 1).replace("\"", "");
                NumberFormat nf = JkOutput.getNumberFmtEN(2);
                mov.setAmount(nf.parse(strAmount).doubleValue());
                movs.add(mov);
            }
            return movs;

        } catch (ParseException ex) {
            throw new JkRuntimeException(ex);
        }
    }

}
