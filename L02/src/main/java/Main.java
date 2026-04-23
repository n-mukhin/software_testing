import java.io.File;
import java.math.BigDecimal;

import Lower.*;
import Upper.*;
import Storing.*;

public class Main {

    public static void main(String[] args) {
        BigDecimal start = new BigDecimal("-10");
        BigDecimal end = new BigDecimal("10");
        BigDecimal step = new BigDecimal("0.01");
        BigDecimal accuracy = new BigDecimal("0.0000001");

        String outputDir = "lab2/result/";

        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        CoreFunctions.Computable sin = new Sin();
        CoreFunctions.Computable cos = new Cos();
        CoreFunctions.Computable tan = new Tan();
        CoreFunctions.Computable cot = new Cot();
        CoreFunctions.Computable sec = new Sec();

        CoreFunctions.Computable ln = new NaturalL();
        CoreFunctions.Computable log2 = new BaseL(2);
        CoreFunctions.Computable log3 = new BaseL(3);
        CoreFunctions.Computable log5 = new BaseL(5);

        CoreFunctions.Computable equation = new Equation();

        new GreatCSV(sin, outputDir + "sin-output.csv").write(start, end, step, accuracy);
        new GreatCSV(cos, outputDir + "cos-output.csv").write(start, end, step, accuracy);
        new GreatCSV(tan, outputDir + "tan-output.csv").write(start, end, step, accuracy);
        new GreatCSV(cot, outputDir + "cot-output.csv").write(start, end, step, accuracy);
        new GreatCSV(sec, outputDir + "sec-output.csv").write(start, end, step, accuracy);

        new GreatCSV(ln, outputDir + "ln-output.csv").write(start, end, step, accuracy);
        new GreatCSV(log2, outputDir + "log2-output.csv").write(start, end, step, accuracy);
        new GreatCSV(log3, outputDir + "log3-output.csv").write(start, end, step, accuracy);
        new GreatCSV(log5, outputDir + "log5-output.csv").write(start, end, step, accuracy);

        new GreatCSV(equation, outputDir + "equation-output.csv").write(start, end, step, accuracy);
    }
}