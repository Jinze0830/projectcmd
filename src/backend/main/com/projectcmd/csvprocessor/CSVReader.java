package backend.main.com.projectcmd.csvprocessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CSVReader {
    public List<List<String>> getColumnBuckets(String path) throws FileNotFoundException {
        Set<String> records = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(path));) {
            while (scanner.hasNextLine()) {
                records.add(scanner.nextLine().split(",")[0]);
            }
        }
        List<List<String>> buckets = buildBucket(records);

        return buckets;
    }

    private List<List<String>> buildBucket(Set<String> records) {
        List<List<String>> buckets = new ArrayList<>();
        List<String> curBucket = new ArrayList<>();

        for(String record : records) {
            if(curBucket.size() == 80) {
                buckets.add(new ArrayList<>(curBucket));
                curBucket = new ArrayList<>();
            }

            curBucket.add(record);
        }

        return buckets;
    }
}
