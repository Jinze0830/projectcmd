package backend.main.com.projectcmd.csvprocessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CSVReader {

    public Queue<String> getAllBarcodes(String path) throws FileNotFoundException {
        Queue<String> queue = new LinkedList<>();
        try (Scanner scanner = new Scanner(new File(path));) {
            while (scanner.hasNextLine()) {
                queue.add(scanner.nextLine().split(",")[0]);
            }
        }
        // split to bucket with 80 barcode in each

        return queue;
    }

    public List<List<String>> getColumnBuckets(String path) throws FileNotFoundException {
        Set<String> records = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(path));) {
            while (scanner.hasNextLine()) {
                records.add(scanner.nextLine().split(",")[0]);
            }
        }
        // split to bucket with 80 barcode in each
        List<List<String>> buckets = buildBucket(records);

        return buckets;
    }

    // split to bucket with 80 barcode in each
    private List<List<String>> buildBucket(Set<String> records) {
        List<List<String>> buckets = new ArrayList<>();
        List<String> curBucket = new ArrayList<>();

        //TODO: try 5 line each time first
        for(String record : records) {
            if(curBucket.size() == 5) {
                buckets.add(new ArrayList<>(curBucket));
                curBucket = new ArrayList<>();
            }

            curBucket.add(record);
        }

        buckets.add(new ArrayList<>(curBucket));
        return buckets;
    }

    public static List<String> getBarcodeBucketByLotNum(Queue<String> barcodes, int count) {
        int curCount = 1;
        List<String> res = new ArrayList<>();
        while(barcodes.size() > 0 && curCount <= count) {
            res.add(barcodes.poll());
        }

        return res;
    }
}
