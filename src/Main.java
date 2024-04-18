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
    /**
     * Programa honen bidez beste programa guztien exekuzioa ahalbidetzen da menu baten bidez.
     * Menuan exekutatu daitezkeen aukerak adierazten dira, "()" erabiliz setting espezifikoak adierazteko.
     * Exekuzio setting hainbat aldatu ahal dira eta baita path-ak ere. Texto moduko parametroetarako aukera eta testua sakatu beharko da eta boolean bezalako balioentzarako zenbakia baino ez zakatuz balio aldatzeko nahikoa da.
     *
     */

    // ------------------------------ [PATHS] --------------------------------------

    //---GET RAW DATA---
    private static String PATH_IN = "src/x_in/";
    private static String PATH_GENERAL_OUT = "src/x_out/";
    private static String PATH_OUT = PATH_GENERAL_OUT + "Data/";

    private static String MODIFIED_PATH = "/modified_";
    private static String FINAL_PATH = "/final_";
    private static String EMOJI_LIST = "src/x_in/emoji.txt";

    //---ANALISIS---
    private static String ANALISIS_TRAIN_TXT_PATH = PATH_OUT + "train/analisis.txt";
    private static String ANALISIS_DEV_TXT_PATH = PATH_OUT + "dev/analisis.txt";

    //---TRAIN---
    private static String TRAIN_ARFF_PATH = PATH_OUT + "train/train.arff";
    private static String NEW_TRAIN_ARFF_PATH = PATH_OUT + "train/new_train.arff";
    private static String BOW_TRAIN_ARFF_PATH = PATH_OUT + "train/BoW_train.arff";
    private static String FSS_TRAIN_ARFF_PATH = PATH_OUT + "train/FSS_train.arff";

    //---DEV---
    private static String DEV_ARFF_PATH = PATH_OUT + "dev/dev.arff";
    private static String NEW_DEV_ARFF_PATH = PATH_OUT + "dev/new_dev.arff";
    private static String COMPATIBLE_DEV_ARFF_PATH = PATH_OUT + "dev/compatible_dev.arff";

    //---TEST---
    private static String TEST_ARFF_PATH = PATH_OUT + "test/test.arff";
    private static String COMPATIBLE_TEST_PATH = PATH_OUT + "test/compatible_test.arff";

    //---COMBINED---
    private static String COMBINED_ARFF_PATH = PATH_OUT + "combined.arff";
    private static String BOW_COMBINED_ARFF_PATH = PATH_OUT + "BoW_combined.arff";
    private static String FSS_COMBINED_ARFF_PATH = PATH_OUT + "FSS_combined.arff";

    //---MODELS---
    private static String J48_MODEL_PATH = PATH_GENERAL_OUT + "Sailkatzailea/j48.model";
    private static String RANDOMFOREST_MODEL_PATH = PATH_GENERAL_OUT + "Sailkatzailea/rf.model";
    private static String RF_EKORKETA_DATUAK = PATH_GENERAL_OUT + "Sailkatzailea/EkorketaDatuakRF.csv";
    private static String RF_PARAMETRO_OPT = PATH_GENERAL_OUT + "Sailkatzailea/RF_parametroOpt.csv";
    private static String XGBOOST_MODEL_PATH = PATH_GENERAL_OUT + "Sailkatzailea/boost.model";

    //---EXTRAS---
    private static String DICTIONARY_TXT_PATH = PATH_OUT + "dictionary.txt";
    private static String IRAGARPENAK_PATH = PATH_GENERAL_OUT + "Iragarpenak/iragarpenakBaseLine.txt";

    // ------------------------------ [SETTINGS] --------------------------------------
    private static boolean READ_CSV_EMOJIS = true;
    private static int HOLD_OUT_PERCENTAGE = 80;
    private static int FSS_WORDS_TO_KEEP = 2000;
    private static String IRAGARPEN_MODELOA = "RF";
    private static String IRAGARPEN_MOTA = "User"; //User, Message
    private static String ANALIZATUTAKO_DATUAK = "RAW Train"; //RAW Train, RAW Dev, RAW Test, RAW Combined, NEW Train, NEW Dev, BOW Train, FSS Train, Compatible Dev

    // ---- (RF PARAMETERS) ----S
    private static Boolean PN = true;
    private static Boolean BSP = false;
    private static Boolean MD = false;
    private static Boolean NT = true;

    // ------------------------------ [MAIN] --------------------------------------
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // Menú de opciones
            System.out.println("\n\n\n--------------MENU NAGUSIA-----------------");
            System.out.println("PROGRAMAK");
            System.out.println("1. Modeloa eraiki (" + IRAGARPEN_MODELOA + ")" );
            System.out.println("2. Baseline kalitatea");
            System.out.println("3. RandomForest ekorketa + kalitatea");
            System.out.println("4. Iragarpenak egin (" + IRAGARPEN_MODELOA + ") erabiliz");
            System.out.println("5. (" + ANALIZATUTAKO_DATUAK + ") analizatu");


            System.out.println("\nSETTINGS");
            System.out.println("6. Fitxategien kokapena aldatu");
            System.out.println("7. Programen exekuzio aukerak aldatu");
            //System.out.println("8. XGBoost ekorketa ");

            System.out.println("\n0. IRTEN");

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
                    iragarpenakEgin();
                    break;
                case 5:
                    analizeData();
                    break;
                case 6:
                    fitxategiKokapenAldaketa();
                    break;
                case 7:
                    settingsAldaketa();
                    break;
                //case 8:
                //prestatuData();
                //XGBoostExec();
                //break;
                case 0:
                    System.out.println("Irtetzen...");
                    return;
                default:
                    System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
            }
        }
    }

    // ------------------------------ [METODOAK] --------------------------------------
    private static void prestatuData() throws Exception {
        //Data.newCSV();
        Data.GetRawData.GetRawData("train", TRAIN_ARFF_PATH,PATH_IN,PATH_OUT,MODIFIED_PATH,FINAL_PATH,EMOJI_LIST,READ_CSV_EMOJIS,IRAGARPEN_MOTA);
        Data.GetRawData.GetRawData("dev", DEV_ARFF_PATH,PATH_IN,PATH_OUT,MODIFIED_PATH,FINAL_PATH,EMOJI_LIST,READ_CSV_EMOJIS,IRAGARPEN_MOTA);

        AnalizeData.main(TRAIN_ARFF_PATH, ANALISIS_TRAIN_TXT_PATH);
        AnalizeData.main(DEV_ARFF_PATH, ANALISIS_DEV_TXT_PATH);

        Data.NewStratifiedHoldOut.NewStratifiedHoldOut(HOLD_OUT_PERCENTAGE, TRAIN_ARFF_PATH, NEW_TRAIN_ARFF_PATH,  DEV_ARFF_PATH, NEW_DEV_ARFF_PATH, COMBINED_ARFF_PATH);

        Data.S2WData.S2WData(NEW_TRAIN_ARFF_PATH, BOW_TRAIN_ARFF_PATH);
        Data.FSS.FSS(BOW_TRAIN_ARFF_PATH, FSS_TRAIN_ARFF_PATH, DICTIONARY_TXT_PATH,FSS_WORDS_TO_KEEP);

        Data.MakeCompatible.MakeCompatible(NEW_DEV_ARFF_PATH, COMPATIBLE_DEV_ARFF_PATH, DICTIONARY_TXT_PATH);
    }

    private static void analizeData() throws Exception {
        //RAW Train, RAW Dev, RAW Test, RAW Combined, NEW Train, NEW Dev, BOW Train, FSS Train, Compatible Dev
        System.out.println(ANALIZATUTAKO_DATUAK + " aztertzen...");
        if (ANALIZATUTAKO_DATUAK.equals("RAW Train")){
            Data.AnalizeData.main(TRAIN_ARFF_PATH,ANALISIS_TRAIN_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_TRAIN_TXT_PATH);

        }
        else if (ANALIZATUTAKO_DATUAK.equals("RAW Dev")){
            Data.AnalizeData.main(DEV_ARFF_PATH,ANALISIS_DEV_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_DEV_TXT_PATH);
        }
        else if (ANALIZATUTAKO_DATUAK.equals("RAW Test")){
            Data.AnalizeData.main(TEST_ARFF_PATH,ANALISIS_DEV_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_DEV_TXT_PATH);
        }
        else if (ANALIZATUTAKO_DATUAK.equals("RAW Combined")){
            Data.AnalizeData.main(COMBINED_ARFF_PATH,ANALISIS_DEV_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_DEV_TXT_PATH);
        }
        else if (ANALIZATUTAKO_DATUAK.equals("NEW Train")){
            Data.AnalizeData.main(NEW_TRAIN_ARFF_PATH,ANALISIS_DEV_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_DEV_TXT_PATH);
        }
        else if (ANALIZATUTAKO_DATUAK.equals("NEW Dev")){
            Data.AnalizeData.main(NEW_DEV_ARFF_PATH,ANALISIS_DEV_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_DEV_TXT_PATH);
        }
        else if (ANALIZATUTAKO_DATUAK.equals("BOW Train")){
            Data.AnalizeData.main(BOW_TRAIN_ARFF_PATH,ANALISIS_DEV_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_DEV_TXT_PATH);
        }
        else if (ANALIZATUTAKO_DATUAK.equals("FSS Train")){
            Data.AnalizeData.main(FSS_TRAIN_ARFF_PATH,ANALISIS_DEV_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_DEV_TXT_PATH);
        }
        else if (ANALIZATUTAKO_DATUAK.equals("Compatible Dev")){
            Data.AnalizeData.main(COMPATIBLE_DEV_ARFF_PATH,ANALISIS_DEV_TXT_PATH);
            System.out.println("Analisia hurrengo fitxategian gorde da:" + ANALISIS_DEV_TXT_PATH);
        }
    }

    private static void baselineExec() throws Exception {
        Sailkatzailea.J48BaseLine.main(FSS_TRAIN_ARFF_PATH,COMPATIBLE_DEV_ARFF_PATH);
    }

    private static void randomForestExec() throws Exception {
        ParametroEkorketa.main(new String[]{FSS_TRAIN_ARFF_PATH, COMPATIBLE_DEV_ARFF_PATH, RF_EKORKETA_DATUAK, RF_PARAMETRO_OPT},PN,BSP,MD,NT);
    }

    private static void XGBoostExec() throws Exception {
        //ParametroEkorketaXGB.main(new String[]{FSS_TRAIN_ARFF_PATH, COMPATIBLE_DEV_ARFF_PATH});
        System.out.println("Funtzio hau ez dago erabilgarria momentu honetan");
    }

    private static void modeloaEraiki() throws Exception {
        //DATA PRESTATU
        Data.GetRawData.GetRawData("train", TRAIN_ARFF_PATH,PATH_IN,PATH_OUT,MODIFIED_PATH,FINAL_PATH,EMOJI_LIST,READ_CSV_EMOJIS,IRAGARPEN_MOTA);
        Data.GetRawData.GetRawData("dev", DEV_ARFF_PATH,PATH_IN,PATH_OUT,MODIFIED_PATH,FINAL_PATH,EMOJI_LIST,READ_CSV_EMOJIS,IRAGARPEN_MOTA);
        Data.NewStratifiedHoldOut.NewStratifiedHoldOut(HOLD_OUT_PERCENTAGE, TRAIN_ARFF_PATH, NEW_TRAIN_ARFF_PATH,  DEV_ARFF_PATH, NEW_DEV_ARFF_PATH, COMBINED_ARFF_PATH);

        Data.S2WData.S2WData(COMBINED_ARFF_PATH, BOW_COMBINED_ARFF_PATH);
        Data.FSS.FSS(BOW_COMBINED_ARFF_PATH, FSS_COMBINED_ARFF_PATH, DICTIONARY_TXT_PATH,FSS_WORDS_TO_KEEP);

        Data.GetRawData.GetRawData("test", TEST_ARFF_PATH,PATH_IN,PATH_OUT,MODIFIED_PATH,FINAL_PATH,EMOJI_LIST,READ_CSV_EMOJIS,IRAGARPEN_MOTA);
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

            System.out.println("\n\nFitxategien kokapenaren aldaketa:");

            System.out.println("\nKarpetak");
            System.out.println("1. PATH_IN: " + PATH_IN);
            System.out.println("2. PATH_OUT: " + PATH_OUT);

            System.out.println("\nFitxategiak");
            System.out.println("3. MODIFIED_PATH: " + MODIFIED_PATH);
            System.out.println("4. FINAL_PATH: " + FINAL_PATH);
            System.out.println("5. EMOJI_LIST: " + EMOJI_LIST);
            System.out.println("6. ANALISIS_TRAIN_TXT_PATH: " + ANALISIS_TRAIN_TXT_PATH);
            System.out.println("7. ANALISIS_DEV_TXT_PATH: " + ANALISIS_DEV_TXT_PATH);
            System.out.println("8. TRAIN_ARFF_PATH: " + TRAIN_ARFF_PATH);
            System.out.println("9. NEW_TRAIN_ARFF_PATH: " + NEW_TRAIN_ARFF_PATH);
            System.out.println("10. BOW_TRAIN_ARFF_PATH: " + BOW_TRAIN_ARFF_PATH);
            System.out.println("11. FSS_TRAIN_ARFF_PATH: " + FSS_TRAIN_ARFF_PATH);
            System.out.println("12. DEV_ARFF_PATH: " + DEV_ARFF_PATH);
            System.out.println("13. NEW_DEV_ARFF_PATH: " + NEW_DEV_ARFF_PATH);
            System.out.println("14. COMPATIBLE_DEV_ARFF_PATH: " + COMPATIBLE_DEV_ARFF_PATH);
            System.out.println("15. TEST_ARFF_PATH: " + TEST_ARFF_PATH);
            System.out.println("16. COMPATIBLE_TEST_PATH: " + COMPATIBLE_TEST_PATH);
            System.out.println("17. COMBINED_ARFF_PATH: " + COMBINED_ARFF_PATH);
            System.out.println("18. BOW_COMBINED_ARFF_PATH: " + BOW_COMBINED_ARFF_PATH);
            System.out.println("19. FSS_COMBINED_ARFF_PATH: " + FSS_COMBINED_ARFF_PATH);
            System.out.println("20. J48_MODEL_PATH: " + J48_MODEL_PATH);
            System.out.println("21. RANDOMFOREST_MODEL_PATH: " + RANDOMFOREST_MODEL_PATH);
            System.out.println("22. RF_EKORKETA_DATUAK: " + RF_EKORKETA_DATUAK);
            System.out.println("23. RF_PARAMETRO_OPT: " + RF_PARAMETRO_OPT);
            System.out.println("24. XGBOOST_MODEL_PATH: " + XGBOOST_MODEL_PATH);
            System.out.println("25. DICTIONARY_TXT_PATH: " + DICTIONARY_TXT_PATH);
            System.out.println("26. IRAGARPENAK_PATH: " + IRAGARPENAK_PATH);
            System.out.println("0. Irten");

            int aukera = scanner.nextInt();

            switch (aukera) {
                case 1:
                    PATH_IN = pathAldaketa("PATH_IN",  scanner);
                    break;
                case 2:
                    PATH_OUT = pathAldaketa("PATH_OUT", scanner);
                    break;
                case 3:
                    MODIFIED_PATH = pathAldaketa("MODIFIED_PATH", scanner);
                    break;
                case 4:
                    FINAL_PATH = pathAldaketa("FINAL_PATH", scanner);
                    break;
                case 5:
                    EMOJI_LIST = pathAldaketa("EMOJI_LIST", scanner);
                    break;
                case 6:
                    ANALISIS_TRAIN_TXT_PATH = pathAldaketa("ANALISIS_TRAIN_TXT_PATH", scanner);
                    break;
                case 7:
                    ANALISIS_DEV_TXT_PATH = pathAldaketa("ANALISIS_DEV_TXT_PATH", scanner);
                    break;
                case 8:
                    TRAIN_ARFF_PATH = pathAldaketa("TRAIN_ARFF_PATH", scanner);
                    break;
                case 9:
                    NEW_TRAIN_ARFF_PATH = pathAldaketa("NEW_TRAIN_ARFF_PATH", scanner);
                    break;
                case 10:
                    BOW_TRAIN_ARFF_PATH = pathAldaketa("BOW_TRAIN_ARFF_PATH", scanner);
                    break;
                case 11:
                    FSS_TRAIN_ARFF_PATH = pathAldaketa("FSS_TRAIN_ARFF_PATH", scanner);
                    break;
                case 12:
                    DEV_ARFF_PATH = pathAldaketa("DEV_ARFF_PATH", scanner);
                    break;
                case 13:
                    NEW_DEV_ARFF_PATH = pathAldaketa("NEW_DEV_ARFF_PATH", scanner);
                    break;
                case 14:
                    COMPATIBLE_DEV_ARFF_PATH = pathAldaketa("COMPATIBLE_DEV_ARFF_PATH", scanner);
                    break;
                case 15:
                    TEST_ARFF_PATH = pathAldaketa("TEST_ARFF_PATH", scanner);
                    break;
                case 16:
                    COMPATIBLE_TEST_PATH = pathAldaketa("COMPATIBLE_TEST_PATH", scanner);
                    break;
                case 17:
                    COMBINED_ARFF_PATH = pathAldaketa("COMBINED_ARFF_PATH", scanner);
                    break;
                case 18:
                    BOW_COMBINED_ARFF_PATH = pathAldaketa("BOW_COMBINED_ARFF_PATH", scanner);
                    break;
                case 19:
                    FSS_COMBINED_ARFF_PATH = pathAldaketa("FSS_COMBINED_ARFF_PATH", scanner);
                    break;
                case 20:
                    J48_MODEL_PATH = pathAldaketa("J48_MODEL_PATH", scanner);
                    break;
                case 21:
                    RANDOMFOREST_MODEL_PATH = pathAldaketa("RANDOMFOREST_MODEL_PATH", scanner);
                    break;
                case 22:
                    RF_EKORKETA_DATUAK = pathAldaketa("RF_EKORKETA_DATUAK", scanner);
                    break;
                case 23:
                    RF_PARAMETRO_OPT = pathAldaketa("RF_PARAMETRO_OPT", scanner);
                    break;
                case 24:
                    XGBOOST_MODEL_PATH = pathAldaketa("XGBOOST_MODEL_PATH", scanner);
                    break;
                case 25:
                    DICTIONARY_TXT_PATH = pathAldaketa("DICTIONARY_TXT_PATH", scanner);
                    break;
                case 26:
                    IRAGARPENAK_PATH = pathAldaketa("IRAGARPENAK_PATH", scanner);
                    break;
                case 0:
                    exitMenu = true;
                    break;
                default:
                    System.out.println("Aukera okerra. Mesedez, aukeratu beste bat.");
                    break;
            }
        }
    }

    private static String pathAldaketa(String izena, Scanner scanner) {
        System.out.println(izena + " aldatu:");
        String path = scanner.next();
        System.out.println(izena + " aldatu da.");
        return path;
    }

    private static void settingsAldaketa() {
        Boolean exitMenu = false;
        while (!exitMenu){
            Scanner scanner = new Scanner(System.in);

            System.out.println("\n\nSetting-en aldaketa:");
            System.out.println("1. HOLD_OUT_PERCENTAGE: " + HOLD_OUT_PERCENTAGE);
            System.out.println("2. IRAGARPEN_MODELOA: " + IRAGARPEN_MODELOA);
            System.out.println("3. FSS_WORDS_TO_KEEP: " + FSS_WORDS_TO_KEEP);
            System.out.println("4. RF PARAMETROAK ALDATU");
            System.out.println("5. READ_CSV_EMOJIS: " + READ_CSV_EMOJIS);
            System.out.println("6. ANALIZATUTAKO DATUAK: " + ANALIZATUTAKO_DATUAK);
            System.out.println("7. IRAGARPEN MOTA: " + IRAGARPEN_MOTA);

            System.out.println("0. Irten");

            int aukera = scanner.nextInt();

            switch (aukera) {
                case 1:
                    HOLD_OUT_PERCENTAGE = Integer.parseInt(aukeraAldatu("HOLD_OUT_PERCENTAGE",  scanner));
                    break;
                case 2:
                    System.out.println("Aukera posibleak: RF, J48");
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
                                PN = !PN;
                                break;
                            case 2:
                                BSP = !BSP;
                                break;
                            case 3:
                                MD = !MD;
                                break;
                            case 4:
                                NT = !NT;
                                break;
                            case 0:
                                System.out.println("Menu nagusira bueltatzen...");
                                exitMenuRF = true;
                                break;
                        }
                    }
                    break;
                case 5:
                    READ_CSV_EMOJIS = !READ_CSV_EMOJIS;
                    break;
                case 6:
                    System.out.println("Aukera posibleak:");
                    System.out.println("1. RAW Train");
                    System.out.println("2. RAW Dev");
                    System.out.println("3. RAW Test");
                    System.out.println("4. RAW Combined");
                    System.out.println("5. NEW Train");
                    System.out.println("6. NEW Dev");
                    System.out.println("7. BOW Train");
                    System.out.println("8. FSS Train");
                    System.out.println("9. Compatible Dev");

                    int aukera6 = scanner.nextInt();
                    switch (aukera6) {
                        case 1:
                            ANALIZATUTAKO_DATUAK = "RAW Train";
                            break;
                        case 2:
                            ANALIZATUTAKO_DATUAK = "RAW Dev";
                            break;
                        case 3:
                            ANALIZATUTAKO_DATUAK = "RAW Test";
                            break;
                        case 4:
                            ANALIZATUTAKO_DATUAK = "RAW Combined";
                            break;
                        case 5:
                            ANALIZATUTAKO_DATUAK = "NEW Train";
                            break;
                        case 6:
                            ANALIZATUTAKO_DATUAK = "NEW Dev";
                            break;
                        case 7:
                            ANALIZATUTAKO_DATUAK = "BOW Train";
                            break;
                        case 8:
                            ANALIZATUTAKO_DATUAK = "FSS Train";
                            break;
                        case 9:
                            ANALIZATUTAKO_DATUAK = "Compatible Dev";
                            break;
                        default:
                            System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
                            break;
                    }
                    break;
                case 7:
                    System.out.println("Aukera posibleak:");
                    System.out.println("1. User");
                    System.out.println("2. Message");

                    int aukera7 = scanner.nextInt();
                    switch (aukera7) {
                        case 1:
                            IRAGARPEN_MOTA = "User";
                            break;
                        case 2:
                            IRAGARPEN_MOTA = "Message";
                            break;
                        default:
                            System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
                            break;
                    }
                case 0:
                    System.out.println("Menu nagusira bueltatzen...");
                    exitMenu = true;
                    break;
                case 143:
                    System.out.println("Why do I keep getting attracted?\n" +
                            "니 모습만 떠올라\n" +
                            "I cannot explain this emotion\n" +
                            "One-Four-Three, I love you");
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