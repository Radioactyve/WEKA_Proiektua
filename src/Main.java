import Data.AnalizeData;
import Iragarpenak.Iragarpenak;
import Sailkatzailea.ParametroEkorketa;
import Sailkatzailea.ParametroEkorketaXGB;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;


import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Main {
    // ------------------------------ [PATHS] --------------------------------------


    //---GET RAW DATA---
    private static String PATH_IN = "src/x_in/";
    private static String PATH_OUT = "src/x_out/Data/";
    private static String MODIFIED_PATH = "/modified_";
    private static String FINAL_PATH = "/final_";
    private static String EMOJI_LIST = "src/x_in/emoji.txt";


    //---ANALISIS---
    private static String ANALISIS_TRAIN_TXT_PATH = "src/x_out/Data/train/analisis.txt";
    private static String ANALISIS_DEV_TXT_PATH = "src/x_out/Data/dev/analisis.txt";


    //---TRAIN---
    private static String TRAIN_ARFF_PATH = "src/x_out/Data/train/train.arff";
    private static String NEW_TRAIN_ARFF_PATH = "src/x_out/Data/train/new_train.arff";
    private static String BOW_TRAIN_ARFF_PATH = "src/x_out/Data/train/BoW_train.arff";
    private static String FSS_TRAIN_ARFF_PATH = "src/x_out/Data/train/FSS_train.arff";


    //---DEV---
    private static String DEV_ARFF_PATH = "src/x_out/Data/dev/dev.arff";
    private static String NEW_DEV_ARFF_PATH = "src/x_out/Data/dev/new_dev.arff";
    private static String COMPATIBLE_DEV_ARFF_PATH = "src/x_out/Data/dev/compatible_dev.arff";


    //---TEST---
    private static String TEST_ARFF_PATH = "src/x_out/Data/test/test.arff";
    private static String COMPATIBLE_TEST_PATH = "src/x_out/Data/test/compatible_test.arff";


    //---COMBINED---
    private static String COMBINED_ARFF_PATH = "src/x_out/Data/combined.arff";
    private static String BOW_COMBINED_ARFF_PATH = "src/x_out/Data/BoW_combined.arff";
    private static String FSS_COMBINED_ARFF_PATH = "src/x_out/Data/FSS_combined.arff";




    //---MODELS---
    private static String J48_MODEL_PATH = "src/x_out/Sailkatzailea/j48.model";
    private static String RANDOMFOREST_MODEL_PATH = "src/x_out/Sailkatzailea/rf.model";
    private static String RF_EKORKETA_DATUAK = "src/x_out/Sailkatzailea/EkorketaDatuakRF.csv";
    private static String RF_PARAMETRO_OPT = "src/x_out/Sailkatzailea/RF_parametroOpt.csv";
    private static String XGBOOST_MODEL_PATH = "src/x_out/Sailkatzailea/boost.model";




    //---EXTRAS---
    private static String DICTIONARY_TXT_PATH = "src/x_out/Data/dictionary.txt";
    private static String IRAGARPENAK_PATH = "src/x_out/Iragarpenak/iragarpenakBaseLine.txt";






    // ------------------------------ [SETTINGS] --------------------------------------
    private static boolean READ_CSV_EMOJIS = true;
    private static int HOLD_OUT_PERCENTAGE = 70;
    private static int FSS_WORDS_TO_KEEP = 2000;
    private static String IRAGARPEN_MODELOA = "RF";
    private static String ANALIZATUTAKO_DATUAK = "train";


    // ---- (RF PARAMETERS) ----
    private static Boolean PN = true;
    private static Boolean BSP = true;
    private static Boolean MD = true;
    private static Boolean NT = true;










    // ------------------------------ [MAIN] --------------------------------------
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);


        while (true) {
            // Menú de opciones
            System.out.println("MENU NAGUSIA:");
            System.out.println("1. Modeloa eraiki");
            System.out.println("2. Baseline kalitatea");
            System.out.println("3. RandomForest ekorketa + kalitatea");
            System.out.println("4. XGBoost ekorketa");
            System.out.println("5. Iragarpenak egin");
            System.out.println("6. Datuak analizatu");
            System.out.println("7. Fitxategien kokapena aldatu");
            System.out.println("8. Programen exekuzio aukerak aldatu");
            System.out.println("9. Irten");


            int aukera = scanner.nextInt();


            switch (aukera) {
                case 1:
                    modeloaEraiki();
                    break;
                case 2:
                    prestatuData();
                    baselineExec();
                    break;
                case 3:
                    prestatuData();
                    randomForestExec();
                    break;
                case 4:
                    prestatuData();
                    XGBoostExec();
                    break;
                case 5:
                    iragarpenakEgin();
                    break;
                case 6:
                    analizeData();
                    break;


                case 7:
                    fitxategiKokapenAldaketa();
                    break;
                case 8:
                    settingsAldaketa();
                    break;
                case 9:
                    System.out.println("Irtetzen...");
                    return;
                default:
                    System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
            }
        }
    }






    // ------------------------------ [METODOAK] --------------------------------------




    private static void prestatuData() throws Exception {
        Data.GetRawData.GetRawData("train", TRAIN_ARFF_PATH,PATH_IN,PATH_OUT,MODIFIED_PATH,FINAL_PATH,EMOJI_LIST,READ_CSV_EMOJIS);
        Data.GetRawData.GetRawData("dev", DEV_ARFF_PATH,PATH_IN,PATH_OUT,MODIFIED_PATH,FINAL_PATH,EMOJI_LIST,READ_CSV_EMOJIS);


        AnalizeData.main(TRAIN_ARFF_PATH, ANALISIS_TRAIN_TXT_PATH);
        AnalizeData.main(DEV_ARFF_PATH, ANALISIS_DEV_TXT_PATH);


        Data.NewStratifiedHoldOut.NewStratifiedHoldOut(HOLD_OUT_PERCENTAGE, TRAIN_ARFF_PATH, NEW_TRAIN_ARFF_PATH,  DEV_ARFF_PATH, NEW_DEV_ARFF_PATH, COMBINED_ARFF_PATH);


        Data.S2WData.S2WData(NEW_TRAIN_ARFF_PATH, BOW_TRAIN_ARFF_PATH);
        Data.FSS.FSS(BOW_TRAIN_ARFF_PATH, FSS_TRAIN_ARFF_PATH, DICTIONARY_TXT_PATH,FSS_WORDS_TO_KEEP);


        Data.MakeCompatible.MakeCompatible(NEW_DEV_ARFF_PATH, COMPATIBLE_DEV_ARFF_PATH, DICTIONARY_TXT_PATH);
    }


    private static void analizeData() throws Exception {
        System.out.println(ANALIZATUTAKO_DATUAK + " aztertzen...");


        if (ANALIZATUTAKO_DATUAK.equals("dev")){
            Data.AnalizeData.main(DEV_ARFF_PATH,ANALISIS_DEV_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_DEV_TXT_PATH);
        }
        else{
            Data.AnalizeData.main(TRAIN_ARFF_PATH,ANALISIS_TRAIN_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_TRAIN_TXT_PATH);
        }






    }


    private static void baselineExec() throws Exception {
        Sailkatzailea.J48BaseLine.main(FSS_TRAIN_ARFF_PATH,COMPATIBLE_DEV_ARFF_PATH);
    }


    private static void randomForestExec() throws Exception {
        ParametroEkorketa.main(new String[]{FSS_TRAIN_ARFF_PATH, COMPATIBLE_DEV_ARFF_PATH, RF_EKORKETA_DATUAK, RF_PARAMETRO_OPT},PN,BSP,MD,NT);
    }


    private static void XGBoostExec() throws Exception {
        ParametroEkorketaXGB.main(new String[]{FSS_TRAIN_ARFF_PATH, COMPATIBLE_DEV_ARFF_PATH});
    }


    private static void modeloaEraiki() throws Exception {
        //DATA PRESTATU
        Data.GetRawData.GetRawData("train", TRAIN_ARFF_PATH,PATH_IN,PATH_OUT,MODIFIED_PATH,FINAL_PATH,EMOJI_LIST,READ_CSV_EMOJIS);
        Data.GetRawData.GetRawData("dev", DEV_ARFF_PATH,PATH_IN,PATH_OUT,MODIFIED_PATH,FINAL_PATH,EMOJI_LIST,READ_CSV_EMOJIS);
        Data.NewStratifiedHoldOut.NewStratifiedHoldOut(HOLD_OUT_PERCENTAGE, TRAIN_ARFF_PATH, NEW_TRAIN_ARFF_PATH,  DEV_ARFF_PATH, NEW_DEV_ARFF_PATH, COMBINED_ARFF_PATH);


        Data.S2WData.S2WData(COMBINED_ARFF_PATH, BOW_COMBINED_ARFF_PATH);
        Data.FSS.FSS(BOW_COMBINED_ARFF_PATH, FSS_COMBINED_ARFF_PATH, DICTIONARY_TXT_PATH,FSS_WORDS_TO_KEEP);


        Data.MakeCompatible.MakeCompatible(TEST_ARFF_PATH, COMPATIBLE_TEST_PATH, DICTIONARY_TXT_PATH);


        //Datuak kargatu
        ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(FSS_COMBINED_ARFF_PATH);
        Instances data = dataSource.getDataSet();
        if (data.classIndex() == -1) {
            data.setClassIndex(data.attribute("claseValue").index());
        }


        // MODELO EZBERDINAK
        if (IRAGARPEN_MODELOA.equals("RF")){


            //CSV-ko parametro optimoak irakurri
            FileReader reader;
            CSVParser parser;
            Map<String, String> ParametroOpt = new HashMap();


            try {
                reader = new FileReader(RF_PARAMETRO_OPT);
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






            //balioak esleitu
            RandomForest RandomForest = new RandomForest();


            if (PN){
                RandomForest.setNumFeatures(Integer.parseInt(ParametroOpt.get("PNratio")));
            }
            if (BSP){
                RandomForest.setBagSizePercent(Integer.parseInt(ParametroOpt.get("BagSizePercentage")));
            }
            if (MD){
                RandomForest.setMaxDepth(Integer.parseInt(ParametroOpt.get("MaxDepth")));
            }
            if (NT){
                RandomForest.setNumIterations(Integer.parseInt(ParametroOpt.get("NumTree")));
            }
            System.out.println("Parametro hauekin RandomForest-a eraikitzen:");
            System.out.println("PNratio" + ParametroOpt.get("PNratio"));
            System.out.println("BagSizePercentage" + ParametroOpt.get("BagSizePercentage"));
            System.out.println("MaxDepth" + ParametroOpt.get("MaxDepth"));
            System.out.println("NumTree" + ParametroOpt.get("NumTree"));


            //Modeloa sortu
            RandomForest.buildClassifier(data);
            //Modeloa gorde
            SerializationHelper.write(RANDOMFOREST_MODEL_PATH, RandomForest);
            System.out.println("RandomForest modeloa eraiki da hurrengo path-ean:" + RANDOMFOREST_MODEL_PATH);
        }
        else if (IRAGARPEN_MODELOA.equals("XGBoost")){
            //create XGBoost
            //iragarpenak XGBoost (es otro programa aparte)
        }
        else if (IRAGARPEN_MODELOA.equals("J48")){
            J48 j48 = new J48();
            String[] options = new String[1];
            options[0] = "-U"; // aaaa soy +
            j48.setOptions(options);
            j48.buildClassifier(data);
            System.out.println("J48 modeloa eraiki da hurrengo path-ean:" + J48_MODEL_PATH);
            //Modeloa gorde
            SerializationHelper.write(J48_MODEL_PATH, j48);
        }
    }


    private static void iragarpenakEgin() throws Exception {
        System.out.println("Iragarpenak egiten...");


        if (IRAGARPEN_MODELOA.equals("RF")){
            System.out.println(RANDOMFOREST_MODEL_PATH + " erabiliz iragarpenak egiten...");
            Iragarpenak.main(RANDOMFOREST_MODEL_PATH,IRAGARPEN_MODELOA,COMPATIBLE_TEST_PATH,IRAGARPENAK_PATH);
        }
        else if (IRAGARPEN_MODELOA.equals("XGBoost")){
            //create XGBoost
            //iragarpenak XGBoost (es otro programa aparte)
        }
        else if (IRAGARPEN_MODELOA.equals("J48")){
            System.out.println(J48_MODEL_PATH + " erabiliz iragarpenak egiten...");
            Iragarpenak.main(J48_MODEL_PATH,IRAGARPEN_MODELOA,COMPATIBLE_TEST_PATH,IRAGARPENAK_PATH);
        }


        System.out.println("Emaitzak lortu dira, iragarpenak hurrengo fitxategian gorde dira:" + IRAGARPENAK_PATH);
    }








    // ----------------------- PARAM ALDAKETAK -----------------------------
    private static void fitxategiKokapenAldaketa() {
        Boolean exitMenu = false;
        while (!exitMenu){
            Scanner scanner = new Scanner(System.in);


            System.out.println("Fitxategien kokapenaren aldaketa:");
            System.out.println("1. TRAIN_ARFF_PATH: " + TRAIN_ARFF_PATH);
            System.out.println("2. DEV_ARFF_PATH: " + DEV_ARFF_PATH);
            System.out.println("3. ANALISIS_TRAIN_TXT_PATH: " + ANALISIS_TRAIN_TXT_PATH);
            System.out.println("4. ANALISIS_DEV_TXT_PATH: " + ANALISIS_DEV_TXT_PATH);
            System.out.println("5. NEW_TRAIN_ARFF_PATH: " + NEW_TRAIN_ARFF_PATH);
            System.out.println("6. NEW_DEV_ARFF_PATH: " + NEW_DEV_ARFF_PATH);
            System.out.println("7. DATA_COMBINED_PATH: " + COMBINED_ARFF_PATH);
            System.out.println("8. BOW_TRAIN_ARFF_PATH: " + BOW_TRAIN_ARFF_PATH);
            System.out.println("9. FSS_TRAIN_ARFF_PATH: " + FSS_TRAIN_ARFF_PATH);
            System.out.println("10. DICTIONARY_TXT_PATH: " + DICTIONARY_TXT_PATH);
            System.out.println("11. COMPATIBLE_DEV_ARFF_PATH: " + COMPATIBLE_DEV_ARFF_PATH);
            System.out.println("12. Irten");


            int aukera = scanner.nextInt();


            switch (aukera) {
                case 1:
                    TRAIN_ARFF_PATH = pathAldaketa("TRAIN_ARFF_PATH", scanner);
                    break;
                case 2:
                    DEV_ARFF_PATH = pathAldaketa("DEV_ARFF_PATH", scanner);
                    break;
                case 3:
                    ANALISIS_TRAIN_TXT_PATH = pathAldaketa("ANALISIS_TRAIN_TXT_PATH", scanner);
                    break;
                case 4:
                    ANALISIS_DEV_TXT_PATH = pathAldaketa("ANALISIS_DEV_TXT_PATH", scanner);
                    break;
                case 5:
                    NEW_TRAIN_ARFF_PATH = pathAldaketa("NEW_TRAIN_ARFF_PATH", scanner);
                    break;
                case 6:
                    NEW_DEV_ARFF_PATH = pathAldaketa("NEW_DEV_ARFF_PATH", scanner);
                    break;
                case 7:
                    COMBINED_ARFF_PATH = pathAldaketa("DATA_COMBINED_PATH", scanner);
                    break;
                case 8:
                    BOW_TRAIN_ARFF_PATH = pathAldaketa("BOW_TRAIN_ARFF_PATH", scanner);
                    break;
                case 9:
                    FSS_TRAIN_ARFF_PATH = pathAldaketa("FSS_TRAIN_ARFF_PATH", scanner);
                    break;
                case 10:
                    DICTIONARY_TXT_PATH = pathAldaketa("DICTIONARY_TXT_PATH", scanner);
                    break;
                case 11:
                    COMPATIBLE_DEV_ARFF_PATH = pathAldaketa("COMPATIBLE_DEV_ARFF_PATH", scanner);
                    break;
                case 12:
                    System.out.println("Menu nagusira bueltatzen...");
                    exitMenu = true;
                    break;
                default:
                    System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
            }
        }
    }


    private static String pathAldaketa(String pathIzena, Scanner scanner) {
        System.out.println("Hurrengo path-erako balio berria sakatu " + pathIzena + ": ");
        String pathBerria = scanner.next();
        System.out.println(pathIzena + " -ren path berria: " + pathBerria + " -ra");
        return pathBerria;
    }


    private static void settingsAldaketa() {
        Boolean exitMenu = false;
        while (!exitMenu){
            Scanner scanner = new Scanner(System.in);


            System.out.println("Setting-en aldaketa:");
            System.out.println("1. HOLD_OUT_PERCENTAGE: " + HOLD_OUT_PERCENTAGE);
            System.out.println("2. IRAGARPEN_MODELOA: " + IRAGARPEN_MODELOA);
            System.out.println("3. FSS_WORDS_TO_KEEP: " + FSS_WORDS_TO_KEEP);
            System.out.println("4. RF PARAMETROAK ALDATU");
            System.out.println("5. READ_CSV_EMOJIS: " + READ_CSV_EMOJIS);
            System.out.println("6. ANALIZATUTAKO DATUAK: " + ANALIZATUTAKO_DATUAK);


            System.out.println("10. Irten");


            int aukera = scanner.nextInt();


            switch (aukera) {
                case 1:
                    HOLD_OUT_PERCENTAGE = Integer.parseInt(aukeraAldatu("HOLD_OUT_PERCENTAGE",  scanner));
                    break;
                case 2:
                    System.out.println("Aukera posibleak: RF, J48, XGBoost");
                    IRAGARPEN_MODELOA = aukeraAldatu("IRAGARPEN_MODELOA",  scanner);
                    break;
                case 3:
                    FSS_WORDS_TO_KEEP = Integer.parseInt(aukeraAldatu("FSS_WORDS_TO_KEEP",  scanner));
                    break;
                case 4:
                    Boolean exitMenuRF = false;
                    while (!exitMenuRF) {
                        System.out.println("Random Forest Setting-en aldaketa:");
                        System.out.println("1. PN: " + PN);
                        System.out.println("2. BSP: " + BSP);
                        System.out.println("3. MD: " + MD);
                        System.out.println("4. NT: " + NT);
                        System.out.println("0. Irten");


                        int aukeraRF = scanner.nextInt();


                        switch (aukeraRF) {
                            case 1:
                                PN = Boolean.parseBoolean(aukeraAldatu("PN", scanner));
                                break;
                            case 2:
                                BSP = Boolean.parseBoolean(aukeraAldatu("BSP", scanner));
                                break;
                            case 3:
                                MD = Boolean.parseBoolean(aukeraAldatu("MD", scanner));
                                break;
                            case 4:
                                NT = Boolean.parseBoolean(aukeraAldatu("NT", scanner));
                                break;
                            case 0:
                                System.out.println("Menu nagusira bueltatzen...");
                                exitMenuRF = true;
                                break;
                        }
                    }
                    break;
                case 5:
                    READ_CSV_EMOJIS = Boolean.parseBoolean(aukeraAldatu("READ_CSV_EMOJIS",  scanner));
                    break;
                case 6:
                    System.out.println("Aukera posibleak: train, dev");
                    ANALIZATUTAKO_DATUAK = aukeraAldatu("ANALIZATUTAKO_DATUAK",  scanner);
                    break;
                case 10:
                    System.out.println("Menu nagusira bueltatzen...");
                    exitMenu = true;
                    break;
                default:
                    System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
            }
        }
    }


    private static String aukeraAldatu(String settingIzena, Scanner scanner) {
        System.out.println(settingIzena + "-ren balio sakatu: ");
        String newValue = scanner.next();
        System.out.println(settingIzena + " -ren balioa hurrengoa da orain: " + newValue );


        return newValue;
    }


}

