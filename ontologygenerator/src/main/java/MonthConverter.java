import java.util.HashMap; // import the HashMap class

public class MonthConverter {
    public static HashMap<String, Integer> month2int = new HashMap<String, Integer>();

    public static void initializeHashMap() {
        month2int.put("january", 1);
        month2int.put("february", 2);
        month2int.put("march", 3);
        month2int.put("april", 4);
        month2int.put("may", 5);
        month2int.put("june", 6);
        month2int.put("july", 7);
        month2int.put("august", 8);
        month2int.put("september", 9);
        month2int.put("october", 10);
        month2int.put("november", 11);
        month2int.put("december", 12);

    }

    public static int string2int(String month) {
        month = month.toLowerCase();
        return month2int.get(month);
    }

    // String[] mm = String.values();

    // for (int i = 0; i < 12; i++) {
    // if (month.equals(mm[i])) {
    // System.out.println(mm[i]);
    // }
    // }
    // }

}
