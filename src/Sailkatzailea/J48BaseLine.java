package Sailkatzailea;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

public class J48BaseLine {
    public static void main(String args) throws Exception {

        DataSource source = new DataSource(args);
        Instances data = source.getDataSet();

        // Asegura que el atributo de clase está correctamente establecido
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1); // Normalmente el último atributo
        }

        J48 j48 = new J48();
        j48.buildClassifier(data);

        //Modeloa gorde
        SerializationHelper.write("src/x_out/Sailkatzailea/Modeloak/j48.model", j48);
    }
}
