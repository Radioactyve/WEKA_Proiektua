package Sailkatzailea;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

public class J48BaseLine {
    public static void main(String trainArff) throws Exception {

        DataSource source = new DataSource(trainArff);
        Instances data = source.getDataSet();
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }

        J48 j48 = new J48();
        String[] options = new String[1];
        options[0] = "-U"; // Usar un árbol sin poda
        j48.setOptions(options);
        j48.buildClassifier(data);

        //Modeloa gorde
        SerializationHelper.write("src/x_out/Sailkatzailea/j48.model", j48);
    }
}
