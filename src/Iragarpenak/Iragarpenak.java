package Iragarpenak;


import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;

public class Iragarpenak {
    public static  void main() throws Exception {
        String modelPath = "src/x_out/Sailkatzailea/j48.model";
        String testPath = "src/x_out/Data/test/compatible_test.arff";
        String outputPath = "src/x_out/Iragarpenak/iragarpenakBaseLine.txt";

        J48 j48 = (J48) SerializationHelper.read(new FileInputStream(modelPath));

        DataSource source = new DataSource(testPath);
        Instances data = source.getDataSet();
        if (data.classIndex()==-1){
            data.setClassIndex(0);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            for (int i = 0; i < data.numInstances(); i++) {
                Instance instance = data.instance(i);
                double label = j48.classifyInstance(instance);
                String prediction = data.classAttribute().value((int) label);
                writer.write("Instancia " + (i + 1) + ": Iragarpena = " + prediction + "\n");
            }
        }
    }
}
