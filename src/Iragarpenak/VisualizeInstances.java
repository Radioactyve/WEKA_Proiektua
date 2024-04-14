package Iragarpenak;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class VisualizeInstances {
    public VisualizeInstances() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Txarto sartu dituzu argumentuak");
            System.out.println("Argumentuak hauek era honetara izan behar dira: DatuenPath OptimoenPath ");
        } else {
            String csvPath = args[0];
            String csvParametroOpt = args[1];
            Map<String, String> ParametroOpt = new HashMap();

            FileReader reader;
            CSVParser parser;
            try {
                reader = new FileReader(csvParametroOpt);

                try {
                    parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                    CSVRecord firstRecord = (CSVRecord)parser.getRecords().stream().findFirst().orElse((CSVRecord) null);
                    if (firstRecord != null) {
                        Map<String, String> var10000 = firstRecord.toMap();
                        Objects.requireNonNull(ParametroOpt);
                        var10000.forEach(ParametroOpt::put);
                    } else {
                        System.out.println("Ez daude parametro optimoak.");
                    }


                    System.out.println("Parametro optimoak:");
                    System.out.println(ParametroOpt);
                    System.out.println();
                } catch (Throwable var26) {
                    try {
                        reader.close();
                    } catch (Throwable var25) {
                        var26.addSuppressed(var25);
                    }
                    throw var26;
                }
                reader.close();
            } catch (IOException var27) {
                System.out.println("Ez da dokumentua aurkitu");
                var27.printStackTrace();
            }

            try {
                reader = new FileReader(csvPath);
                parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
                Map<String, Integer> headers = parser.getHeaderMap();
                Iterator var7 = headers.keySet().iterator();

                while(var7.hasNext()) {
                    String header = (String)var7.next();
                    if (!header.equals("F-measure") && !header.equals("Denbora")) {
                        try {
                            FileReader fr = new FileReader(csvPath);
                            try {
                                CSVParser newParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(fr);
                                XYSeriesCollection data = createDataset(newParser, header, ParametroOpt);
                                createChart(data, header);
                            } catch (Throwable var23) {
                                try {
                                    fr.close();
                                } catch (Throwable var22) {
                                    var23.addSuppressed(var22);
                                }
                                throw var23;
                            }
                            fr.close();
                        } catch (IOException var24) {
                            var24.printStackTrace();
                        }
                    }
                }
            } catch (IOException var28) {
                var28.printStackTrace();
            }

            JFrame frame = new JFrame("Estatistikak");
            frame.setDefaultCloseOperation(3);
            frame.setLayout(new BorderLayout());
            JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
            ImageIcon image1 = new ImageIcon("F-measure_vs_BagSizePercentage.jpg");
            Image scaledImage1 = image1.getImage().getScaledInstance(500, 375, 4);
            ImageIcon scaledIcon1 = new ImageIcon(scaledImage1);
            JLabel label1 = new JLabel(scaledIcon1);
            panel.add(label1);
            ImageIcon image2 = new ImageIcon("F-measure_vs_MaxDepth.jpg");
            Image scaledImage2 = image2.getImage().getScaledInstance(500, 375, 4);
            ImageIcon scaledIcon2 = new ImageIcon(scaledImage2);
            JLabel label2 = new JLabel(scaledIcon2);
            panel.add(label2);
            ImageIcon image3 = new ImageIcon("F-measure_vs_NumTree.jpg");
            Image scaledImage3 = image3.getImage().getScaledInstance(500, 375, 4);
            ImageIcon scaledIcon3 = new ImageIcon(scaledImage3);
            JLabel label3 = new JLabel(scaledIcon3);
            panel.add(label3);
            ImageIcon image4 = new ImageIcon("F-measure_vs_PNratio.jpg");
            Image scaledImage4 = image4.getImage().getScaledInstance(500, 375, 4);
            ImageIcon scaledIcon4 = new ImageIcon(scaledImage4);
            JLabel label4 = new JLabel(scaledIcon4);
            panel.add(label4);
            frame.add(panel, "Center");
            frame.pack();
            frame.setLocationRelativeTo((Component) null);
            frame.setVisible(true);
        }
    }
    private static XYSeriesCollection createDataset(CSVParser records, String variableName, Map<String, String> ParametroOpt) {
        XYSeries series = new XYSeries("F-measure vs " + variableName);
        Iterator var4 = records.iterator();
        System.out.println(variableName);

        while(var4.hasNext()) {
            CSVRecord record = (CSVRecord)var4.next();
            boolean besteGuztiakOptimoak = true;
            Iterator var7 = record.toMap().keySet().iterator();

            while(var7.hasNext()) {
                String columnName = (String)var7.next();
                if (!columnName.equals(variableName) && !columnName.equals("F-measure") && !columnName.equals("Denbora")) {
                    String value = record.get(columnName);
                    //System.out.println(value + (String)ParametroOpt.get(columnName));
                    if (!((String)ParametroOpt.get(columnName)).equals(value)) {
                        besteGuztiakOptimoak = false;
                        break;
                    }
                }
            }
            if (besteGuztiakOptimoak) {
                double fMeasure = Double.parseDouble(record.get("F-measure"));
                double variableValue = Double.parseDouble(record.get(variableName));
                System.out.println(variableValue);
                series.add(variableValue, fMeasure);
            }
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }
    private static void createChart(XYSeriesCollection dataset, String variableName) {
        JFreeChart chart = ChartFactory.createScatterPlot("F-measure vs " + variableName, variableName, "F-measure", dataset);

        try {
            File grafikoa = new File("F_measure_vs_" + variableName + ".jpg");
            if (grafikoa.exists()) {
                grafikoa.delete();
            }

            ChartUtils.saveChartAsJPEG(new File("F-measure_vs_" + variableName + ".jpg"), chart, 800, 600);
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }
}
