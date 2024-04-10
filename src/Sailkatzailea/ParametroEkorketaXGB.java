package Sailkatzailea;


import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;
import weka.classifiers.Evaluation;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;



public class ParametroEkorketaXGB {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Atributuak sartzean akats bat izan duzu!");
            System.out.println("Erabilera:");
            System.out.println("java -jar ParametroEkorketa.jar train.arff dev.arff ");
        } else {
            //Datuak kargatu
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(args[0]);
            Instances data = dataSource.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);

            ConverterUtils.DataSource devSource = new ConverterUtils.DataSource(args[1]);
            Instances dev = devSource.getDataSet();
            dev.setClassIndex(data.numAttributes() - 1);

            DMatrix dataXGB = convertToDMatrix(data);
            DMatrix devXGB = convertToDMatrix(dev);

            //CSV fitxategia sortu eta hasierako infromazioa sartu
            BufferedWriter writer = new BufferedWriter(new FileWriter("EkorketaDatuakXGB.csv"));
            String[] header = {"NumIterations", "LearningRate", "MaxDepth", "SubSample", "Apha", "Lambda", "F-measure", "Denbora"};
            for (int i = 0; i < header.length; i++) {
                writer.write(header[i]);
                if (i < header.length - 1) {
                    writer.write(",");
                }
            }


            //Klase minoritarioa kalkulatu
            AttributeStats attrStats = data.attributeStats(data.numAttributes() - 1);
            int minoritarioa = -1;
            double KlaseMinoMaiz = 0;
            for (int i = 0; i < data.numClasses(); i++) {
                System.out.println("i: " + i + "; izena: " + data.attribute(data.numAttributes() - 1).value(i) + "; maiztasuna: " + attrStats.nominalCounts[i]);
                if (KlaseMinoMaiz > attrStats.nominalCounts[i]) {
                    minoritarioa = i;
                    KlaseMinoMaiz = attrStats.nominalCounts[i];
                }
            }


            //Parametroak sortu eta hasieratu 0 ra
            double optFMeasure = 0.0;


            int NumItOpt = 0;
            double LearnRateOpT = 0;
            int MDopt = 0;
            double SSCopt = 0;
            double SSRopt = 0;
            double Aopt = 0;
            double Gopt = 0;


            long denbOpt = 0;


            String[] datuak = {};


            // NumIterations
            for (int NI = 100; NI <= 200; NI += 10) { //NumItertations 100 etik 200ra
                //LearningRate
                for (double LR = 0.01; LR < 0.5; LR += 0.05) {//LearningRate 0 eta 0.5 tartean
                    //maxDepth
                    for (int MD = 1; MD < 20; MD += 2) {//MaxDepth 1etik 20ra
                        // SubsampleRows
                        for (double SR = 0.5; SR <= 1.0; SR += 0.1) { // SubsampleRows de 0.5tik  1era
                            // SubsampleCols
                            for (double SC = 0.5; SC <= 1.0; SC += 0.1) { // SubsampleCols 0.5tik 1era
                                //Alpha
                                for (double A = 0.01; A < 0.5; A += 0.05) { //Aplha 0.01tik 0.05era
                                    //Lambda
                                    for (Double L = 0.01; L < 0.5; L += 0.05) { //Lammbda 0.01tik 0.05era
                                        Map<String, Object> parametroak = new HashMap<>();
                                        parametroak.put("eta", String.valueOf(LR));  // LearningRate
                                        parametroak.put("max_depth", String.valueOf(MD));  // MaxDepth
                                        parametroak.put("subsample_rows", String.valueOf(SR));  // SubsampleRows
                                        parametroak.put("subsample_cols", String.valueOf(SC));  // SubsampleCols
                                        parametroak.put("alpha", String.valueOf(A));  // Alpha
                                        parametroak.put("lambda", String.valueOf(L));  // Lambda

                                        Booster booster = XGBoost.train(dataXGB, parametroak, NI, null, null, null);


                                        //Seguir desde aqui

                                        long Hasiera = System.nanoTime();
                                        Evaluation evaluator = new Evaluation(data);
                                        evaluator.crossValidateModel(booster, data, 10, new Random(1));
                                        long Amaiera = System.nanoTime();
                                        long exDenb = Amaiera - Hasiera;
                                        double Fmeasure = evaluator.fMeasure(minoritarioa);
                                        datuak = new String[]{String.valueOf(PN), String.valueOf(BSP), String.valueOf(MD), String.valueOf(NT), String.valueOf(Fmeasure), String.valueOf(exDenb)};
                                        for (int i = 0; i < datuak.length; i++) {
                                            writer.write(datuak[i]);
                                            if (i < datuak.length - 1) {
                                                writer.write(",");
                                            }
                                        }
                                        writer.newLine();
                                        if (evaluator.fMeasure(minoritarioa) > optFMeasure) {
                                            optFMeasure = evaluator.fMeasure(minoritarioa);
                                            PNopt = PN;
                                            BSPopt = BSP;
                                            NTopt = NT;
                                            denbOpt = exDenb;
                                        } else if (evaluator.fMeasure(minoritarioa) == optFMeasure && exDenb < denbOpt) {
                                            optFMeasure = evaluator.fMeasure(minoritarioa);
                                            PNopt = PN;
                                            BSPopt = BSP;
                                            NTopt = NT;


                                            denbOpt = exDenb;
                                        }
                                    }


                                }
                            }
                        }
                    }
                }
                System.out.println("Parametro optimoak hauek dira:");
                System.out.println("P/Nratio: " + PNopt);
                System.out.println("BagSizePercentage: " + BSPopt);
                System.out.println("MaxDepth: " + MDopt);
                System.out.println("NumTree: " + NTopt);
                System.out.println();
                System.out.println("Eta hauek dira emaitzak:");
                System.out.println("F-measure: " + optFMeasure);
                System.out.println("Exekuzio denbora: " + denbOpt);
            }
        }
    }
    private static DMatrix convertToDMatrix(Instances data) throws XGBoostError {
        int numInstances = data.numInstances();
        int numAttributes = data.numAttributes() - 1;
        float[] dataFloats = new float[numInstances * numAttributes];
        float[] labels = new float[numInstances];

        for (int i = 0; i < numInstances; i++) {
            for (int j = 0; j < numAttributes; j++) {
                dataFloats[i * numAttributes + j] = (float)data.instance(i).value(j);
            }
            labels[i] = (float)data.instance(i).classValue();
        }

        DMatrix dMatrix = new DMatrix(dataFloats, numInstances, numAttributes);
        dMatrix.setLabel(labels);
        return dMatrix;
    }
}

