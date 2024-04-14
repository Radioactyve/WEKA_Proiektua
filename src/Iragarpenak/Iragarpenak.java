package Iragarpenak;


import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;

public class Iragarpenak {
    public static  void main(String modelPath, String modeloMota, String testPath, String outputPath) throws Exception {
        // -------- [DATA] ---------
        DataSource source = new DataSource(testPath);
        Instances data = source.getDataSet();
        if (data.classIndex()==-1){
            data.setClassIndex(data.attribute("claseValue").index());
        }

        // -------- [MODEL] ---------
        J48 j48 = new J48();
        RandomForest RF = new RandomForest();
        //XGBoost

        if (modeloMota.equals("J48")){
            j48 = (J48) SerializationHelper.read(new FileInputStream(modelPath));
        }
        else if (modeloMota.equals("RF")){
            RF = (RandomForest) SerializationHelper.read(new FileInputStream(modelPath));
        }
        /*
        else if (modeloMota == "XGBoost"){
            J48 j48 = (J48) SerializationHelper.read(new FileInputStream(modelPath));
        }
        */


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            for (int i = 0; i < data.numInstances(); i++) {
                Instance instance = data.instance(i);
                double label =0;
                if (modeloMota.equals("J48")){
                    label = j48.classifyInstance(instance);
                }
                if (modeloMota.equals("RF")){
                    label = RF.classifyInstance(instance);
                }
                /*
                if (modeloMota == "XGBoost"){
                    double label = Boost.classifyInstance(instance);
                }
                */
                String prediction = data.classAttribute().value((int) label);
                writer.write("Instancia " + (i + 1) + ": Iragarpena = " + prediction + "\n");
            }
        }
    }
}
