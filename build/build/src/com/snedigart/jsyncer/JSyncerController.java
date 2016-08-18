package com.snedigart.jsyncer;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.snedigart.jsync.SyncOptions;
import com.snedigart.jsync.SyncOptions.SyncOptionsBuilder;
import com.snedigart.jsync.Syncer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

public class JSyncerController {
    @FXML
    private VBox rootVBox;

    @FXML
    private GridPane fileGridPane;

    @FXML
    private TextField sourceTextField;

    @FXML
    private TextField targetTextField;

    @FXML
    private Button sourceBrowseButton;

    @FXML
    private Button targetBrowseButton;

    @FXML
    private TitledPane optionsTitledPane;

    @FXML
    private CheckBox deleteUnmatchedCheckBox;

    @FXML
    private CheckBox smartCopyCheckBox;

    @FXML
    private ChoiceBox<String> chunkSizeChoiceBox;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label messageLabel;

    @FXML
    private Label progressLabel;

    @FXML
    private Button syncButton;

    private static final String KB = "KB";

    private static final String MB = "MB";

    private static final long KILOBYTE = 1024L;

    private static final long MEGABYTE = KILOBYTE * KILOBYTE;

    private final Map<String, Long> chunkSizeMap = new HashMap<>();

    private final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

    public void initialize() {
        initChunkSizeMap();
        configureButtons();
        configureTextFields();
        configureChoiceBox();
    }

    private void initChunkSizeMap() {
        chunkSizeMap.clear();
        for (int b = 32; b > 0; b /= 2) {
            chunkSizeMap.put(b + MB, b * MEGABYTE);
            chunkSizeMap.put(b + KB, b * KILOBYTE);
        }
    }

    private void configureButtons() {
        sourceBrowseButton.setOnAction(e -> showFileBrowser("Source", sourceTextField));
        targetBrowseButton.setOnAction(e -> showFileBrowser("Target", targetTextField));

        syncButton.setOnAction(e -> {
            final long chunkSize = chunkSizeMap.get(chunkSizeChoiceBox.getSelectionModel().getSelectedItem());
            final boolean delete = deleteUnmatchedCheckBox.isSelected();
            final boolean smartCopy = smartCopyCheckBox.isSelected();
            final SyncOptions opts = new SyncOptionsBuilder().chunkSize(chunkSize).deleteUnmatchedTargets(delete)
                    .smartCopy(smartCopy).build();

            fileGridPane.setDisable(true);
            optionsTitledPane.setDisable(true);
            syncButton.setDisable(true);

            threadExecutor.submit(() -> {
                try {
                    final File src = new File(sourceTextField.getText().trim());
                    final File tgt = new File(targetTextField.getText().trim());
                    Syncer syncer = new Syncer(src, tgt, opts);
                    syncer.synchronize((r, t, m) -> {
                        Platform.runLater(() -> {
                            // TODO: 0 of 0
                            progressLabel.setText((t - r) + " of " + t);
                            messageLabel.setText(m);
                            if (t != 0) {
                                progressBar.setProgress((((double) t - (double) r) / (double) t));
                            }
                        });
                    });
                } catch (IOException x) {
                    // TODO:
                    x.printStackTrace();
                }
                Platform.runLater(() -> {
                    fileGridPane.setDisable(false);
                    optionsTitledPane.setDisable(false);
                    syncButton.setDisable(false);
                });
            });
        });
    }

    private void configureTextFields() {
        sourceTextField.setOnAction(e -> {

        });
    }

    private void configureChoiceBox() {
        ObservableList<String> items = FXCollections.observableArrayList(chunkSizeMap.keySet());
        items.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return (Long.compare(chunkSizeMap.get(s1), chunkSizeMap.get(s2)) * -1);
            }
        });
        chunkSizeChoiceBox.setStyle("-fx-font-size: 11.0");
        chunkSizeChoiceBox.setItems(items);
        chunkSizeChoiceBox.getSelectionModel().selectFirst();
        chunkSizeChoiceBox.getSelectionModel().selectNext();
    }

    private void showFileBrowser(String title, TextField field) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Open " + title);
        if (!field.getText().trim().isEmpty()) {
            File file = new File(field.getText().trim());
            if (file.exists()) {
                dc.setInitialDirectory(file);
            }
        }

        File f = dc.showDialog(rootVBox.getScene().getWindow());
        if (f != null) {
            field.setText(f.getAbsolutePath());
        }
    }

    public void onExit() {
        threadExecutor.shutdownNow();
    }
}
