package Storing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

public class GreatCSV {

    private final CoreFunctions.Computable function;
    private final String filePath;

    public GreatCSV(CoreFunctions.Computable function, String filePath) {
        this.function = function;
        this.filePath = filePath;
    }

    public void write(BigDecimal start, BigDecimal end, BigDecimal step, BigDecimal accuracy) {
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("x;y");

            for (BigDecimal x = start; x.compareTo(end) <= 0; x = x.add(step)) {
                try {
                    writer.println(x + ";" + function.compute(x, accuracy));
                } catch (ArithmeticException e) {
                    writer.println(x + ";undefined");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи в файл: " + filePath, e);
        }
    }
}