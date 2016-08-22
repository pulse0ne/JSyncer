/*
 * Copyright 2016 Tyler Snedigar.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details.
 */
package com.snedigart.jsyncer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.snedigart.jsync.SyncOptions;
import com.snedigart.jsync.SyncOptions.SyncOptionsBuilder;
import com.snedigart.jsync.SyncResults;
import com.snedigart.jsync.Syncer;
import com.snedigart.jsync.filter.SyncFilter;
import com.snedigart.jsyncer.filter.FileExtensionFilterField;
import com.snedigart.jsyncer.filter.FileSizeFilterField;
import com.snedigart.jsyncer.filter.FilenameFilterField;
import com.snedigart.jsyncer.filter.FilterField;
import com.snedigart.jsyncer.filter.LastModifiedFilterField;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

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
    private TitledPane filtersTitledPane;

    @FXML
    private VBox filtersVBox;

    @FXML
    private MenuButton addFilterMenuButton;

    @FXML
    private MenuItem filenameMenuItem;

    @FXML
    private MenuItem extensionMenuItem;

    @FXML
    private MenuItem sizeMenuItem;

    @FXML
    private MenuItem lastModifiedMenuItem;

    @FXML
    private ChoiceBox<String> includeChoiceBox;

    @FXML
    private ChoiceBox<String> excludeChoiceBox;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label messageLabel;

    @FXML
    private Label progressLabel;

    @FXML
    private Button syncButton;

    @FXML
    private GridPane filtersGridPane;

    private static final String KB = "KB";

    private static final String MB = "MB";

    private static final long KILOBYTE = 1024L;

    private static final long MEGABYTE = KILOBYTE * KILOBYTE;

    private final Map<String, Long> chunkSizeMap = new HashMap<>();

    private final List<FilterField> filterFields = new ArrayList<>();

    @FXML
    private void initialize() {
        initChunkSizeMap();
        configureButtons();
        configureTextFields();
        configureChoiceBox();
        configureTitledPanes();
        configureMenuItemActions();
    }

    private void initChunkSizeMap() {
        chunkSizeMap.clear();
        for (int b = 512; b > 0; b /= 2) {
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
            final boolean matchAllIncl = includeChoiceBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("All");
            final boolean matchAllExcl = excludeChoiceBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("All");
            final List<SyncFilter> includeFilters = collectFilters(f -> f.isInclusive());
            final List<SyncFilter> excludeFilters = collectFilters(f -> !f.isInclusive());

            final SyncOptions opts;
            final SyncOptionsBuilder builder = new SyncOptionsBuilder();
            builder.chunkSize(chunkSize).deleteUnmatchedTargets(delete).smartCopy(smartCopy);
            builder.setInclusionFilters(includeFilters).setExclusionFilters(excludeFilters);
            builder.matchAllInclusionFilters(matchAllIncl).matchAllExclusionFilters(matchAllExcl);

            opts = builder.build();

            fileGridPane.setDisable(true);
            optionsTitledPane.setDisable(true);
            syncButton.setDisable(true);

            ThreadManager.getInstance().submitJob(() -> {
                try {
                    final File src = new File(sourceTextField.getText().trim());
                    final File tgt = new File(targetTextField.getText().trim());
                    final Syncer syncer = new Syncer(src, tgt, opts);
                    SyncResults results = syncer.synchronize((r, t, m) -> Platform.runLater(() -> {
                        progressLabel.setText((t - r) + " of " + t);
                        messageLabel.setText(m);
                        if (t != 0) {
                            progressBar.setProgress((((double) t - (double) r) / (double) t));
                        }
                    }));

                    System.out.println(results);
                } catch (IOException x) {
                    // TODO: handle exceptions
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

    private List<SyncFilter> collectFilters(Predicate<? super FilterField> test) {
        return filterFields.stream().filter(test)
                .map((Function<? super FilterField, ? extends SyncFilter>) f -> f.getFilter())
                .collect(Collectors.toList());
    }

    private void resizeStage() {
        ((Stage) rootVBox.getScene().getWindow()).sizeToScene();
    }

    private void configureTextFields() {
    }

    private void configureChoiceBox() {
        ObservableList<String> items = FXCollections.observableArrayList(chunkSizeMap.keySet());
        items.sort((s1, s2) -> (Long.compare(chunkSizeMap.get(s1), chunkSizeMap.get(s2)) * -1));
        chunkSizeChoiceBox.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        chunkSizeChoiceBox.setItems(items);
        chunkSizeChoiceBox.getSelectionModel().select("16MB");

        ObservableList<String> includeItems = FXCollections.observableArrayList("All", "Any");
        includeChoiceBox.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        includeChoiceBox.setItems(includeItems);
        includeChoiceBox.getSelectionModel().selectFirst();

        ObservableList<String> excludeItems = FXCollections.observableArrayList("All", "Any");
        excludeChoiceBox.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        excludeChoiceBox.setItems(excludeItems);
        excludeChoiceBox.getSelectionModel().selectFirst();
    }

    private void configureTitledPanes() {
        optionsTitledPane.expandedProperty().addListener((obs, nv, ov) -> {
            Platform.runLater(() -> resizeStage());
        });

        filtersTitledPane.expandedProperty().addListener((obs, nv, ov) -> {
            Platform.runLater(() -> resizeStage());
        });
    }

    private void configureMenuItemActions() {
        addFilterMenuButton.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        filenameMenuItem.setOnAction(event -> {
            addFilterField(new FilenameFilterField());
        });

        extensionMenuItem.setOnAction(event -> {
            addFilterField(new FileExtensionFilterField());
        });

        sizeMenuItem.setOnAction(event -> {
            addFilterField(new FileSizeFilterField());
        });

        lastModifiedMenuItem.setOnAction(event -> {
            addFilterField(new LastModifiedFilterField());
        });
    }

    private void addFilterField(FilterField field) {
        field.getCloseButton().setOnAction(e -> removeFilterField(field));
        filtersGridPane.addRow(filtersGridPane.getChildren().size(), field.getInclusiveChoiceBox(),
                field.getInputNode(), field.getCloseButton());
        filterFields.add(field);
        resizeStage();
    }

    private void removeFilterField(FilterField field) {
        filterFields.remove(field);
        filtersGridPane.getChildren().removeAll(field.getInclusiveChoiceBox(), field.getInputNode(),
                field.getCloseButton());
        resizeStage();
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
}
