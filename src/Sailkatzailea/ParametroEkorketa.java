package Sailkatzailea;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class ParametroEkorketa {


    public static void main(String[] args) throws Exception {
            //Datuak kargatu
            DataSource dataSource = new DataSource("src/x_out/Data/dev/compatible_dev.arff");
            Instances data = dataSource.getDataSet();
            if (data.classIndex() == -1) {
                data.setClassIndex(data.attribute("claseValue").index());
            }


            DataSource dataSource1 = new DataSource("src/x_out/Data/train/FSS_train.arff");
            Instances dataDev = dataSource1.getDataSet();
            if (dataDev.classIndex() == -1) {
                dataDev.setClassIndex(data.attribute("claseValue").index());
            }


            //CSV fitxategia sortu eta hasierako infromazioa sartu
            BufferedWriter writer =new BufferedWriter(new FileWriter("EkorketaDatuakRF.csv"));
            String[] header={"PNratio", "BagSizePercentage", "MaxDepth", "NumTree", "F-measure", "Denbora"};
            for (int i = 0; i < header.length; i++) {
                writer.write(header[i]);
                if (i < header.length - 1) {
                    writer.write(",");
                }
            }
            BufferedWriter writer1 =new BufferedWriter(new FileWriter("DF_parametroOpt.csv"));
            for (int i = 0; i < header.length; i++) {
                writer.write(header[i]);
                if (i < header.length - 1) {
                    writer.write(",");
                }
            }


            //Klase minoritarioa kalkulatu
            AttributeStats attrStats = data.attributeStats(data.attribute("claseValue").index());
            int minoritarioa = -1;
            double KlaseMinoMaiz = Integer.MAX_VALUE;
            for(int i=0; i<data.numClasses(); i++){
                System.out.println("i: " + i + "; izena: " + data.attribute(data.attribute("claseValue").index()).value(i) + "; maiztasuna: " + attrStats.nominalCounts[i]);
                if (KlaseMinoMaiz > attrStats.nominalCounts[i]){
                    minoritarioa = i;
                    KlaseMinoMaiz = attrStats.nominalCounts[i];
                }
            }
            System.out.println(minoritarioa);





            /*
            //Parametroak sortu eta hasieratu 0 ra
            double optFMeasure = 0.0;
            double PNopt = 0;
            double BSPopt = 0;
            double NTopt = 0;
            double MDopt = 0;
            long denbOpt= 0;
            String[] datuak={};

            int loop =0;
            //Random forest-a sortu eta atributuen erro karratua
            RandomForest RF= new RandomForest();
            RF.setNumExecutionSlots(Runtime.getRuntime().availableProcessors());
            int erroAtributu=(int)(Math.sqrt(data.numAttributes()));
            //PN ratio
            for (int PN=0;PN<erroAtributu;PN+=10) { //atributuen erroa bainon txikiagorarte
                RF.setNumFeatures(PN);
                //BagSizePercentage
                for (int BSP=1;BSP<25;BSP+=4){//Gure kasuan datu askorekin lan egingo dugunez, portzentai txiki bat erabiliko dugu. 4%-ko saltoak
                    RF.setBagSizePercent(BSP);
                    //maxDepth
                    for (int MD=1;MD<erroAtributu;MD=10){
                        RF.setMaxDepth(MD);
                        //numTree
                        for (int NT=50;NT<200;NT+=25){
                            RF.setNumIterations(NT);
                            long Hasiera = System.nanoTime();
                            Evaluation evaluator = new Evaluation(data);
                            evaluator.evaluateModel(RF,dataDev);
                            //evaluator.crossValidateModel(RF, dataDev, 3, new Random(1));
                            long Amaiera = System.nanoTime();
                            long exDenb=Amaiera-Hasiera;
                            double Fmeasure = evaluator.fMeasure(minoritarioa);
                            datuak= new String[]{String.valueOf(PN), String.valueOf(BSP), String.valueOf(MD), String.valueOf(NT), String.valueOf(Fmeasure), String.valueOf(exDenb)};
                            for (int i = 0; i < datuak.length; i++) {
                                writer.write(datuak[i]);
                                if (i < datuak.length - 1) {
                                    writer.write(",");
                                }
                            }
                            writer.newLine();
                            loop++;
                            System.out.println(loop);
                            if(evaluator.fMeasure(minoritarioa)>optFMeasure){
                                optFMeasure = evaluator.fMeasure(minoritarioa);
                                PNopt = PN;
                                BSPopt = BSP;
                                NTopt = NT;
                                denbOpt=exDenb;
                            }
                            else if(evaluator.fMeasure(minoritarioa)==optFMeasure&&exDenb<denbOpt){
                                optFMeasure = evaluator.fMeasure(minoritarioa);
                                PNopt = PN;
                                BSPopt = BSP;
                                NTopt = NT;


                                denbOpt=exDenb;


                            }
                        }
                    }
                }
            }








            datuak= new String[]{String.valueOf(PNopt), String.valueOf(BSPopt), String.valueOf(MDopt), String.valueOf(NTopt), String.valueOf(optFMeasure), String.valueOf(denbOpt)};
            for (int i = 0; i < datuak.length; i++) {
                writer1.write(datuak[i]);
                if (i < datuak.length - 1) {
                    writer.write(",");
                }
            }
            System.out.println("Parametro optimoak hauek dira:");
            System.out.println("P/Nratio: "+PNopt);
            System.out.println("BagSizePercentage: "+BSPopt);
            System.out.println("MaxDepth: "+MDopt);
            System.out.println("NumTree: "+NTopt);
            System.out.println();
            System.out.println("Eta hauek dira emaitzak:");
            System.out.println("F-measure: "+optFMeasure);
            System.out.println("Exekuzio denbora: "+denbOpt);


             */

        // Crear un clasificador RandomForest
        RandomForest forest = new RandomForest();
        forest.setNumExecutionSlots(Runtime.getRuntime().availableProcessors());

        // Entrenar el clasificador con los datos
        forest.buildClassifier(data);

        // Realizar evaluación cruzada del modelo
        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(forest,dataDev);
        //eval.crossValidateModel(forest, data, 10, new Random(1));

        // Imprimir resultados
        System.out.println("Accuracy: " + eval.pctCorrect());
        System.out.println("Kappa: " + eval.kappa());
        System.out.println("Confusion Matrix:\n" + eval.toMatrixString());
        System.out.println("Summary:\n" + eval.toSummaryString());

    }

}
