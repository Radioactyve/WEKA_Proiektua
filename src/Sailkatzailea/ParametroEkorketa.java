package Sailkatzailea;


import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class ParametroEkorketa {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Atributuak sartzean akats bat izan duzu!");
            System.out.println("Erabilera:");
            System.out.println("java -jar ParametroEkorketa.jar train.arff ");
        } else {
            //Datuak kargatu
            DataSource dataSource = new DataSource(args[0]);
            Instances data = dataSource.getDataSet();
            data.setClassIndex(data.numAttributes()-1);

            //CSV fitxategia sortu eta hasierako infromazioa sartu
            BufferedWriter writer =new BufferedWriter(new FileWriter("EkorketaDatuak.csv"));
            String[] header={"P/Nratio", "BagSizePercentage", "MaxDepth", "NumTree", "F-measure", "Denbora"};
            for (int i = 0; i < header.length; i++) {
                writer.write(header[i]);
                if (i < header.length - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();
            writer.write("P/Nratio BagSizePercentage MaxDepth NumTree F-measure Denbora");

            //Klase minoritarioa kalkulatu
            AttributeStats attrStats = data.attributeStats(data.numAttributes() - 1);
            int minoritarioa = -1;
            double KlaseMinoMaiz = 0;
            for(int i=0; i<data.numClasses(); i++){
                System.out.println("i: " + i + "; izena: " + data.attribute(data.numAttributes()-1).value(i) + "; maiztasuna: " + attrStats.nominalCounts[i]);
                if (KlaseMinoMaiz > attrStats.nominalCounts[i]){
                    minoritarioa = i;
                    KlaseMinoMaiz = attrStats.nominalCounts[i];
                }
            }

            //Parametroak sortu eta hasieratu 0 ra
            double optFMeasure = 0.0;
            int PNopt = 0;
            int BSPopt = 0;
            int NTopt = 0;
            int MDopt = 0;
            long denbOpt= 0;
            String[] datuak={};

            //Random forest-a sortu eta atributuen erro karratua
            RandomForest RF= new RandomForest();
            int erroAtributu=(int)(Math.sqrt(data.numAttributes()));
            //PN ratio
            for (int PN=0;PN<erroAtributu;PN+=1) { //atributuen erroa bainon txikiagorarte
                RF.setNumFeatures(PN);
                //BagSizePercentage
                for (int BSP=1;BSP<25;BSP+=4){//Gure kasuan datu askorekin lan egingo dugunez, portzentai txiki bat erabiliko dugu. 4%-ko saltoak
                    RF.setBagSizePercent(BSP);
                    //maxDepth
                    for (int MD=1;MD<erroAtributu;MD=1){
                        RF.setMaxDepth(MD);
                        //numTree
                        for (int NT=50;NT<200;NT+=25){
                            RF.setNumIterations(NT);
                            long Hasiera = System.nanoTime();
                            Evaluation evaluator = new Evaluation(data);
                            evaluator.crossValidateModel(RF, data, 10, new Random(1));
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
            System.out.println("Parametro optimoak hauek dira:");
            System.out.println("P/Nratio: "+PNopt);
            System.out.println("BagSizePercentage: "+BSPopt);
            System.out.println("MaxDepth: "+MDopt);
            System.out.println("NumTree: "+NTopt);
            System.out.println();
            System.out.println("Eta hauek dira emaitzak:");
            System.out.println("F-measure: "+optFMeasure);
            System.out.println("Exekuzio denbora: "+denbOpt);
        }
    }
}