package textprocessing;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TextProcessingApp extends Application {
    private final TextArea inputArea = new TextArea();
    private final TextArea cipherOutputArea = new TextArea();
    private final TextField shiftField = new TextField();
    private final CheckBox useFullTextForCipher = new CheckBox("Use full text for cipher");
    private final Label shiftErrorLabel = new Label();

    private final Map<String, Label> overviewValues = new LinkedHashMap<>();
    private final Map<String, Label> vowelValues = new LinkedHashMap<>();
    private final Map<String, Label> readabilityValues = new LinkedHashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Java Text Processing & Analysis Toolkit");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setLeft(buildInputPanel());
        root.setCenter(buildOutputPanel());

        clearOutputs();

        Scene scene = new Scene(root, 1120, 650);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildInputPanel() {
        Label inputLabel = new Label("Input Text");
        inputLabel.setStyle("-fx-font-weight: bold;");

        inputArea.setPromptText("Type or paste text here...");
        inputArea.setWrapText(true);
        inputArea.setPrefRowCount(20);

        Button analyzeButton = new Button("Analyze");
        analyzeButton.setOnAction(event -> analyzeInput());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> {
            inputArea.clear();
            clearOutputs();
        });

        Button sampleButton = new Button("Load Sample Text");
        sampleButton.setOnAction(event -> inputArea.setText(TextAnalyzer.getReferenceText()));

        HBox buttons = new HBox(8, analyzeButton, clearButton, sampleButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox panel = new VBox(8, inputLabel, inputArea, buttons);
        panel.setPadding(new Insets(0, 10, 0, 0));
        panel.setPrefWidth(500);
        VBox.setVgrow(inputArea, Priority.ALWAYS);
        return panel;
    }

    private TabPane buildOutputPanel() {
        overviewValues.put("Total characters", new Label());
        overviewValues.put("Characters (no spaces)", new Label());
        overviewValues.put("Total words", new Label());
        overviewValues.put("Total sentences", new Label());
        overviewValues.put("Total lines", new Label());
        overviewValues.put("Longest word", new Label());
        overviewValues.put("Shortest word", new Label());
        overviewValues.put("Average word length", new Label());
        overviewValues.put("Most frequent word", new Label());
        overviewValues.put("Most frequent count", new Label());
        overviewValues.put("Total digits", new Label());

        vowelValues.put("Count (a)", new Label());
        vowelValues.put("Count (e)", new Label());
        vowelValues.put("Count (i)", new Label());
        vowelValues.put("Count (o)", new Label());
        vowelValues.put("Count (u)", new Label());
        vowelValues.put("Total vowels", new Label());
        vowelValues.put("Vowel percentage", new Label());

        readabilityValues.put("Flesch score", new Label());
        readabilityValues.put("Readability level", new Label());

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().add(new Tab("Overview", buildStatsGrid(overviewValues)));
        tabs.getTabs().add(new Tab("Vowels", buildStatsGrid(vowelValues)));
        tabs.getTabs().add(new Tab("Readability", buildStatsGrid(readabilityValues)));
        tabs.getTabs().add(new Tab("Cipher", buildCipherPane()));
        return tabs;
    }

    private GridPane buildStatsGrid(Map<String, Label> values) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        int row = 0;
        for (Map.Entry<String, Label> entry : values.entrySet()) {
            Label nameLabel = new Label(entry.getKey() + ":");
            nameLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-weight: bold;");
            grid.add(nameLabel, 0, row);
            grid.add(entry.getValue(), 1, row);
            row++;
        }
        return grid;
    }

    private VBox buildCipherPane() {
        Label shiftLabel = new Label("Shift (1-25):");
        shiftField.setPrefWidth(80);

        Button encryptButton = new Button("Encrypt");
        encryptButton.setOnAction(event -> transformCipher(false));

        Button decryptButton = new Button("Decrypt");
        decryptButton.setOnAction(event -> transformCipher(true));

        shiftErrorLabel.setStyle("-fx-text-fill: #b00020;");

        HBox controls = new HBox(8, shiftLabel, shiftField, encryptButton, decryptButton);
        controls.setAlignment(Pos.CENTER_LEFT);

        cipherOutputArea.setEditable(false);
        cipherOutputArea.setWrapText(true);
        cipherOutputArea.setPromptText("Cipher result will appear here...");

        VBox box = new VBox(8, controls, useFullTextForCipher, shiftErrorLabel, cipherOutputArea);
        box.setPadding(new Insets(10));
        VBox.setVgrow(cipherOutputArea, Priority.ALWAYS);
        return box;
    }

    private void analyzeInput() {
        AnalysisResult result = TextAnalyzer.analyze(inputArea.getText());

        overviewValues.get("Total characters").setText(String.valueOf(result.getTotalCharacters()));
        overviewValues.get("Characters (no spaces)").setText(String.valueOf(result.getCharactersNoSpaces()));
        overviewValues.get("Total words").setText(String.valueOf(result.getTotalWords()));
        overviewValues.get("Total sentences").setText(String.valueOf(result.getTotalSentences()));
        overviewValues.get("Total lines").setText(String.valueOf(result.getTotalLines()));
        overviewValues.get("Longest word").setText(result.getLongestWord());
        overviewValues.get("Shortest word").setText(result.getShortestWord());
        overviewValues.get("Average word length").setText(formatDecimal(result.getAverageWordLength(), 2));
        overviewValues.get("Most frequent word").setText(result.getMostFrequentWord());
        overviewValues.get("Most frequent count").setText(String.valueOf(result.getMostFrequentCount()));
        overviewValues.get("Total digits").setText(String.valueOf(result.getTotalDigits()));

        int[] vowelCounts = result.getVowelCounts();
        vowelValues.get("Count (a)").setText(String.valueOf(vowelCounts[0]));
        vowelValues.get("Count (e)").setText(String.valueOf(vowelCounts[1]));
        vowelValues.get("Count (i)").setText(String.valueOf(vowelCounts[2]));
        vowelValues.get("Count (o)").setText(String.valueOf(vowelCounts[3]));
        vowelValues.get("Count (u)").setText(String.valueOf(vowelCounts[4]));
        vowelValues.get("Total vowels").setText(String.valueOf(result.getTotalVowels()));
        vowelValues.get("Vowel percentage").setText(formatDecimal(result.getVowelPercent(), 1) + "%");

        readabilityValues.get("Flesch score").setText(formatDecimal(result.getFleschScore(), 2));
        readabilityValues.get("Readability level").setText(result.getReadabilityLevel());
    }

    private void transformCipher(boolean decrypt) {
        Integer shift = parseShift();
        if (shift == null) {
            return;
        }

        shiftErrorLabel.setText("");
        String source = TextAnalyzer.getCipherSourceText(inputArea.getText(), useFullTextForCipher.isSelected());
        String result = decrypt
            ? TextAnalyzer.decryptCaesar(source, shift)
            : TextAnalyzer.encryptCaesar(source, shift);
        cipherOutputArea.setText(result);
    }

    private Integer parseShift() {
        String shiftText = shiftField.getText().trim();
        try {
            int shift = Integer.parseInt(shiftText);
            if (shift < 1 || shift > 25) {
                shiftErrorLabel.setText("Shift must be an integer between 1 and 25.");
                return null;
            }
            return shift;
        } catch (NumberFormatException exception) {
            shiftErrorLabel.setText("Shift must be an integer between 1 and 25.");
            return null;
        }
    }

    private void clearOutputs() {
        shiftField.clear();
        shiftErrorLabel.setText("");
        useFullTextForCipher.setSelected(false);
        cipherOutputArea.clear();

        for (Label value : overviewValues.values()) {
            value.setText("0");
        }
        overviewValues.get("Longest word").setText("N/A");
        overviewValues.get("Shortest word").setText("N/A");
        overviewValues.get("Average word length").setText("0.00");
        overviewValues.get("Most frequent word").setText("N/A");

        for (Label value : vowelValues.values()) {
            value.setText("0");
        }
        vowelValues.get("Vowel percentage").setText("0.0%");

        readabilityValues.get("Flesch score").setText("0.00");
        readabilityValues.get("Readability level").setText("N/A");
    }

    private String formatDecimal(double value, int places) {
        return String.format(Locale.ROOT, places == 1 ? "%.1f" : "%.2f", value);
    }
}
