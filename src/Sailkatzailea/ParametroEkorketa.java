package Sailkatzailea;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;


public class ParametroEkorketa {
    public static void main(String[] args, Boolean usePN, Boolean useBSP, Boolean useMD, Boolean useNT) throws Exception {
        long start = System.nanoTime();

        //Datuak kargatu
        DataSource dataSource = new DataSource(args[0]);
        Instances data = dataSource.getDataSet();
        if (data.classIndex() == -1) {
            data.setClassIndex(data.attribute("claseValue").index());
        }

        DataSource dataSource1 = new DataSource(args[1]);
        Instances dataDev = dataSource1.getDataSet();
        if (dataDev.classIndex() == -1) {
            dataDev.setClassIndex(dataDev.attribute("claseValue").index());
        }

        //CSV fitxategia sortu eta hasierako infromazioa sartu
        BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]));
        String[] header = {"PNratio", "BagSizePercentage", "MaxDepth", "NumTree", "F-measure", "Denbora"};
        for (int i = 0; i < header.length; i++) {
            writer.write(header[i]);
            if (i < header.length - 1) {
                writer.write(",");
            }
        }
        writer.newLine();

        PrintWriter writer1 = new PrintWriter(new FileWriter(args[3]));
        // Escribir la cabecera
        for (int i = 0; i < header.length; i++) {
            writer1.print(header[i]);
            if (i < header.length - 1) {
                writer1.print(",");
            }
        }
        writer1.println();

        //Klase minoritarioa kalkulatu
        AttributeStats attrStats = data.attributeStats(data.attribute("claseValue").index());
        int minoritarioa = -1;
        double KlaseMinoMaiz = Integer.MAX_VALUE;
        for (int i = 0; i < data.numClasses(); i++) {
            System.out.println("i: " + i + "; izena: " + data.attribute(data.attribute("claseValue").index()).value(i) + "; maiztasuna: " + attrStats.nominalCounts[i]);
            if (KlaseMinoMaiz > attrStats.nominalCounts[i]) {
                minoritarioa = i;
                KlaseMinoMaiz = attrStats.nominalCounts[i];
            }
        }
        System.out.println(minoritarioa);

        //Parametroak sortu eta hasieratu 0 raa
        Evaluation evalOpt = null;
        double optFMeasure = 0.0;
        int PNopt = 0;
        int BSPopt = 0;
        int NTopt = 0;
        int MDopt = 0;
        long denbOpt = 0;
        String[] datuak;

        int loop = 0;
        //Random forest-a sortu eta atributuen erro karratua
        RandomForest RF = new RandomForest();
        RF.setNumExecutionSlots(Runtime.getRuntime().availableProcessors());
        int erroAtributuPN = (int) (Math.sqrt(data.numAttributes()));
        int erroAtributuMD = (int) (Math.sqrt(data.numAttributes()));
        int maxBSP = 25;
        int maxNT = 35;


        //loop ignore
        if (!usePN) {
            erroAtributuPN = 1;
        }
        if (!useBSP) {
            maxBSP = 2;
        }
        if (!useMD) {
            erroAtributuMD = 2;
        }
        if (!useNT) {
            maxNT = 51;
        }

        //PN ratio
        for (int PN = 10; PN < erroAtributuPN; PN += 1) { //atributuen erroa bainon txikiagorarte
            //BagSizePercentage
            for (int BSP = 1; BSP < maxBSP; BSP += 4) {//Gure kasuan datu askorekin lan egingo dugunez, portzentai txiki bat erabiliko dugu. 4%-ko saltoak
                //maxDepth
                for (int MD = 1; MD < erroAtributuMD; MD += 5) {
                    //numTree
                    for (int NT = 10; NT < maxNT; NT += 1) {
                        //loop count
                        loop++;
                        if (loop%25==0 || loop==1){
                            System.out.println("Loop zenbakia: " + loop);
                        }

                        //parametroak zehaztu
                        if (usePN) {
                            RF.setNumFeatures(PN);
                        }
                        if (useBSP) {
                            RF.setBagSizePercent(BSP);
                        }
                        if (useMD) {
                            RF.setMaxDepth(MD);
                        }
                        if (useNT) {
                            RF.setNumIterations(NT);
                        }
                        RF.buildClassifier(data);
                        long Hasiera = System.nanoTime();
                        Evaluation evaluator = new Evaluation(dataDev);
                        evaluator.evaluateModel(RF,dataDev);
                        //evaluator.crossValidateModel(RF, dataDev, 5, new Random(1));
                        long Amaiera = System.nanoTime();
                        long exDenb = Amaiera - Hasiera;
                        double Fmeasure = evaluator.fMeasure(1);

                        //Datuak gorde
                        datuak = new String[]{String.valueOf(PN), String.valueOf(BSP), String.valueOf(MD), String.valueOf(NT), String.valueOf(Fmeasure), String.valueOf(exDenb)};
                        for (int i = 0; i < datuak.length; i++) {
                            writer.write(datuak[i]);
                            if (i < datuak.length - 1) {
                                writer.write(",");
                            }
                        }
                        writer.flush();
                        writer.newLine();

                        //Balio optimoak eguneratu
                        if (evaluator.fMeasure(1) > optFMeasure) {
                            evalOpt = evaluator;
                            optFMeasure = evaluator.fMeasure(1);
                            PNopt = PN;
                            BSPopt = BSP;
                            MDopt = MD;
                            NTopt = NT;
                            denbOpt = exDenb;
                        } else if (evaluator.fMeasure(1) == optFMeasure && exDenb < denbOpt) {
                            evalOpt = evaluator;
                            optFMeasure = evaluator.fMeasure(1);
                            PNopt = PN;
                            BSPopt = BSP;
                            MDopt = MD;
                            NTopt = NT;
                            denbOpt = exDenb;
                        }
                    }
                }
            }
        }
        writer.close();
        datuak = new String[]{String.valueOf(PNopt), String.valueOf(BSPopt), String.valueOf(MDopt), String.valueOf(NTopt), String.valueOf(optFMeasure), String.valueOf(denbOpt)};
        for (int i = 0; i < datuak.length; i++) {
            writer1.print(datuak[i]);
            if (i < datuak.length - 1) {
                writer1.print(",");
            }
        }
        writer1.close();
        System.out.println("\nParametro optimoak hauek dira:");
        if (usePN) {
            System.out.println("P/Nratio: " + PNopt);
        }
        if (useBSP) {
            System.out.println("BagSizePercentage: " + BSPopt);
        }
        if (useMD) {
            System.out.println("MaxDepth: " + MDopt);
        }
        if (useNT) {
            System.out.println("NumTree: " + NTopt);
        }
        System.out.println();
        System.out.println("Hold-Out aplikatuz hurrengo emaitzak lortu dira:");
        System.out.println("F-measure: " + optFMeasure);
        long end = System.nanoTime();
        long time = end - start;
        double duration = time / 1_000_000_000.0;
        System.out.println("Parametro ekorketaren exekuzio denbora: " + duration + " seg");


        System.out.println(evalOpt.toSummaryString());
        System.out.println(evalOpt.toClassDetailsString());
        System.out.println(evalOpt.toMatrixString());


    }
}