import java.io.File;
import java.math.BigInteger;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PolynomialSecretFinder {


    public static void main(String[] args) {
        try {
            File file = new File("input.json");
            Scanner scanner = new Scanner(file);
            StringBuilder jsonContent = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonContent.append(scanner.nextLine());
            }

            JSONObject obj = new JSONObject(new JSONTokener(jsonContent.toString()));
            JSONObject keys = obj.getJSONObject("keys");
            int k = keys.getInt("k");

            List<BigInteger> xList = new ArrayList<>();
            List<BigInteger> yList = new ArrayList<>();

            for (String key : obj.keySet()) {
                if (key.equals("keys")) continue;
                BigInteger x = new BigInteger(key);
                JSONObject point = obj.getJSONObject(key);
                int base = Integer.parseInt(point.getString("base"));
                BigInteger y = new BigInteger(point.getString("value"), base);
                xList.add(x);
                yList.add(y);
            }

            List<Point> points = new ArrayList<>();
            for (int i = 0; i < xList.size(); i++) {
                points.add(new Point(xList.get(i), yList.get(i)));
            }
            points.sort(Comparator.comparing(p -> p.x));

            List<BigInteger> xSub = new ArrayList<>();
            List<BigInteger> ySub = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                xSub.add(points.get(i).x);
                ySub.add(points.get(i).y);
            }

            BigInteger secret = lagrangeAtZero(xSub, ySub);
            System.out.println("Secret (c) = " + secret);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    static class Point {
        BigInteger x, y;
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static BigInteger lagrangeAtZero(List<BigInteger> x, List<BigInteger> y) {
        BigInteger result = BigInteger.ZERO;
        int k = x.size();

        for (int i = 0; i < k; i++) {
            BigInteger xi = x.get(i);
            BigInteger yi = y.get(i);
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = x.get(j);
                    numerator = numerator.multiply(xj.negate());
                    denominator = denominator.multiply(xi.subtract(xj));
                }
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }
}
