package Data;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.FileWriter;
import java.util.*;

public class AnalizeData {
    private static String textAttributename = "claseValue";

    public static void main(String arffToAnalize, String AnalisisTxt) {
        try {
            // --- DataSource ---
            DataSource source = new DataSource(arffToAnalize);
            Instances data = source.getDataSet();
            data.setClassIndex(data.attribute(textAttributename).index());

            FileWriter fw = new FileWriter(AnalisisTxt);

            fw.write("\nInstantzia kopurua: " + data.numInstances());
            fw.write("\nAtributu kopurua: " + data.numAttributes());
            fw.write("\nMaiztasun txikien duen klasearen identifikatzailea: " + getMinFrequencyClass(data));
            fw.write("\nKlase atributuko missing value kopurua: " + countMissingValues(data,data.attribute(textAttributename).index()));

            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getMinFrequencyClass(Instances data) {
        Attribute classAttribute = data.classAttribute();
        HashMap<String, Integer> classFrequencyMap = new HashMap<>();

        for (int i = 0; i < data.numInstances(); i++) {
            String classValue = data.instance(i).stringValue(classAttribute);
            classFrequencyMap.put(classValue, classFrequencyMap.getOrDefault(classValue, 0) + 1);
        }

        String minFrequencyClass = null;
        int minFrequency = Integer.MAX_VALUE;

        for (String value : classFrequencyMap.keySet()) {
            int frequency = classFrequencyMap.get(value);
            if (frequency < minFrequency) {
                minFrequency = frequency;
                minFrequencyClass = value;
            }
        }

        return minFrequencyClass;
    }


    private static int countMissingValues(Instances data, int attributeIndex) {
        int missingValues = 0;

        for (int i = 0; i < data.numInstances(); i++) {
            if (data.instance(i).isMissing(attributeIndex)) {
                missingValues++;

            }

        }
        return missingValues;
    }
}
