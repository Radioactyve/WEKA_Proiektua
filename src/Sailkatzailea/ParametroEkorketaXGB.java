package Sailkatzailea;




import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
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
            data.setClassIndex(data.attribute("claseValue").index());


            ConverterUtils.DataSource devSource = new ConverterUtils.DataSource(args[1]);
            Instances dev = devSource.getDataSet();
            dev.setClassIndex(data.attribute("claseValue").index());


            DMatrix dataXGB = convertToDMatrix(data);
            DMatrix devXGB = convertToDMatrix(dev);


            //CSV fitxategia sortu eta hasierako infromazioa sartu
            BufferedWriter writer = new BufferedWriter(new FileWriter("EkorketaDatuakXGB.csv"));
            String[] header = {"NumIterations", "LearningRate", "MaxDepth", "SubSampleRows","SubSampleCols", "Apha", "Lambda", "F-measure", "Denbora"};
            for (int i = 0; i < header.length; i++) {
                writer.write(header[i]);
                if (i < header.length - 1) {
                    writer.write(",");
                }
            }


            //Parametroak sortu eta hasieratu 0 ra
            double optFScore = 0.0;
            int NumItOpt = 0;
            double LearnRateOpT = 0;
            int MDopt = 0;
            double SSCopt = 0;
            double SSRopt = 0;
            double Aopt = 0;
            double Lopt = 0;




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
                                        long Hasiera = System.nanoTime();
                                        Booster booster = XGBoost.train(dataXGB, parametroak, NI, null, null, null);


                                        float[][] predictions = booster.predict(devXGB);
                                        int prediction=0;
                                        int tp=0;
                                        int fp=0;
                                        int fn=0;


                                        for (int i = 0; i < predictions.length; i++) {
                                            if (predictions[i][0] > 0.5 ){
                                                prediction=1;
                                            }
                                            else {prediction=0;}
                                            if (prediction == 1 && dev.instance(i).classValue() == 1) {
                                                tp++;
                                            } else if (prediction == 1 && dev.instance(i).classValue() == 0) {
                                                fp++;
                                            } else if (prediction == 0 && dev.instance(i).classValue() == 1) {
                                                fn++;
                                            }


                                        }
                                        //precision
                                        double precision = (double) tp / (tp + fp);
                                        //recall
                                        double recall = (double) tp / (tp + fn);
                                        //FScore
                                        double FScore = 2 * (precision * recall) / (precision + recall);


                                        long Amaiera = System.nanoTime();
                                        long exDenb=Amaiera-Hasiera;


                                        datuak = new String[]{String.valueOf(NI), String.valueOf(LR), String.valueOf(MD), String.valueOf(SR), String.valueOf(SC), String.valueOf(A), String.valueOf(L), String.valueOf(FScore), String.valueOf(exDenb)};
                                        for (int i = 0; i < datuak.length; i++) {
                                            writer.write(datuak[i]);
                                            if (i < datuak.length - 1) {
                                                writer.write(",");
                                            }
                                        }
                                        writer.newLine();
                                        if ((FScore > optFScore)||(FScore == optFScore && exDenb < denbOpt)) {
                                            optFScore= FScore;
                                            NumItOpt = NI;
                                            LearnRateOpT = LR;
                                            MDopt = MD;
                                            SSCopt = SR;
                                            SSRopt = SC;
                                            Aopt = A;
                                            Lopt = L;
                                            denbOpt = exDenb;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println("Parametro optimoak hauek dira:");
                System.out.println("NumIterations: " + NumItOpt);
                System.out.println("LeraningRate: " + LearnRateOpT);
                System.out.println("MaxDepth: " + MDopt);
                System.out.println("SubsampleRows: " + SSRopt);
                System.out.println("SubsampleCols: " + SSCopt);
                System.out.println("Alpha: " + Aopt);
                System.out.println("Lambda: " + Lopt);
                System.out.println();
                System.out.println("Eta hauek dira emaitzak:");
                System.out.println("F-measure: " + optFScore);
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
