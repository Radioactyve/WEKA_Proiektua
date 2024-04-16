package Data;

import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.FileWriter;

public class AnalizeData {
    private static String textAttributename = "claseValue";

    public static void main(String arffToAnalize, String AnalisisTxt) {
        /**
         * Datuen hainbat informazio gordetzen du, hala nola, instantza kopurua, atributu kopurua...
         */
        try {
            // --- DataSource ---
            DataSource source = new DataSource(arffToAnalize);
            Instances data = source.getDataSet();
            data.setClassIndex(data.attribute(textAttributename).index());

            FileWriter fw = new FileWriter(AnalisisTxt);

            fw.write("\nInstantzia kopurua: " + data.numInstances());
            fw.write("\nAtributu kopurua: " + data.numAttributes());
            fw.write("\nKlase atributuko missing value kopurua: " + countMissingValues(data,data.attribute(textAttributename).index()));

            //Klase minoritarioa kalkulatu
            AttributeStats attrStats = data.attributeStats(data.attribute("claseValue").index());
            int minoritarioa = -1;
            double KlaseMinoMaiz = Integer.MAX_VALUE;
            for (int i = 0; i < data.numClasses(); i++) {
                fw.write("\n balioa: " + data.attribute(data.attribute("claseValue").index()).value(i) + "; maiztasuna: " + attrStats.nominalCounts[i]);
                if (KlaseMinoMaiz > attrStats.nominalCounts[i]) {
                    minoritarioa = i;
                    KlaseMinoMaiz = attrStats.nominalCounts[i];
                }
            }
            System.out.println(minoritarioa);


            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int countMissingValues(Instances data, int attributeIndex) {
        /**
         * Missing balioak kontatzen ditu
         */
        int missingValues = 0;

        for (int i = 0; i < data.numInstances(); i++) {
            if (data.instance(i).isMissing(attributeIndex)) {
                missingValues++;

            }

        }
        return missingValues;
    }
}
