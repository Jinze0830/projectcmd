package backend.main.com.projectcmd.csvprocessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVReader {
    public List<String> getColumn(String path) throws FileNotFoundException {
        List<String> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(path));) {
            while (scanner.hasNextLine()) {
                records.add(scanner.nextLine().split(",")[0]);
            }
        }

        return records;
    }
}
