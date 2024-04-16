package Sailkatzailea;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import java.util.Random;

public class J48BaseLine {
    public static void main(String trainArff, String devArff) throws Exception {
        // ------- [DATA] -------
        DataSource source = new DataSource(trainArff);
        Instances data = source.getDataSet();
        if (data.classIndex() == -1) {
            data.setClassIndex(data.attribute("claseValue").index());
        }

        DataSource sourceDev = new DataSource(devArff);
        Instances dataDev = sourceDev.getDataSet();
        if (dataDev.classIndex() == -1) {
            dataDev.setClassIndex(dataDev.attribute("claseValue").index());
        }



        J48 j48 = new J48();
        String[] options = new String[1];
        options[0] = "-U"; // Usar un árbol sin poda
        j48.setOptions(options);
        j48.buildClassifier(data);


        // Realizar evaluación cruzada del modelo
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(j48, dataDev, 5, new Random(1));

        // Imprimir resultados
        System.out.println("F-Measure: " + eval.fMeasure(1));
        System.out.println("\nAccuracy: " + eval.pctCorrect());
        System.out.println("Kappa: " + eval.kappa());
        System.out.println("Confusion Matrix:\n" + eval.toMatrixString());
        System.out.println("Summary:\n" + eval.toSummaryString());



        //Modeloa gorde
        SerializationHelper.write("src/x_out/Sailkatzailea/j48.model", j48);
    }
}
