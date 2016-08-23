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
package com.snedigart.jsyncer.filter;

import com.snedigart.jsync.filter.FileSizeSyncFilter;
import com.snedigart.jsync.filter.SyncFilter;
import com.snedigart.jsyncer.JSyncerConstants;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * FileExtensionFilterField class
 * 
 * @author Tyler Snedigar
 * @version 1.0
 */
public class FileSizeFilterField extends FilterField {

    private static final String NUMERIC_REGEX = "\\d*";

    private static final String NUMERIC_REPLACE = "[^\\d]";

    private static final long KILOBYTE = 1024L;

    private final TextField lowerField;

    private final TextField upperField;

    private final ChoiceBox<FileSizeEnum> lowerChoiceBox;

    private final ChoiceBox<FileSizeEnum> upperChoiceBox;

    private final HBox inputField;

    private static enum FileSizeEnum {
        B, KB, MB, GB
    }

    public FileSizeFilterField() {
        super();
        lowerField = new TextField();
        upperField = new TextField();
        lowerChoiceBox = new ChoiceBox<>();
        upperChoiceBox = new ChoiceBox<>();
        initializeFields();

        inputField = new HBox();
        inputField.setSpacing(5.0);

        final Label fromLabel = new Label("From");
        fromLabel.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        inputField.getChildren().addAll(fromLabel, lowerField, lowerChoiceBox);

        inputField.getChildren().add(new Separator(Orientation.VERTICAL));

        final Label toLabel = new Label("To");
        toLabel.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        inputField.getChildren().addAll(toLabel, upperField, upperChoiceBox);

        inputField.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    public SyncFilter getFilter() {
        long lowerSize = getSize(lowerField, lowerChoiceBox);
        long upperSize = getSize(upperField, upperChoiceBox);
        return new FileSizeSyncFilter(Math.min(lowerSize, upperSize), Math.max(lowerSize, upperSize));
    }

    /**
     * @see com.snedigart.jsyncer.filter.FilterField#getInputNode()
     */
    @Override
    public Node getInputNode() {
        return inputField;
    }

    private void initializeFields() {
        lowerField.textProperty().addListener((obs, ov, nv) -> {
            if (!nv.matches(NUMERIC_REGEX)) {
                lowerField.setText(nv.replaceAll(NUMERIC_REPLACE, ""));
            }
        });
        upperField.textProperty().addListener((obs, ov, nv) -> {
            if (!nv.matches(NUMERIC_REGEX)) {
                upperField.setText(nv.replaceAll(NUMERIC_REPLACE, ""));
            }
        });

        lowerChoiceBox.getItems().addAll(FileSizeEnum.values());
        lowerChoiceBox.getSelectionModel().select(FileSizeEnum.KB);
        upperChoiceBox.getItems().addAll(FileSizeEnum.values());
        upperChoiceBox.getSelectionModel().select(FileSizeEnum.KB);

        lowerField.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        upperField.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        lowerChoiceBox.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        upperChoiceBox.setStyle(JSyncerConstants.FONT_SIZE_STYLE);

        lowerField.setPrefWidth(JSyncerConstants.CONTROL_WIDTH / 2.0);
        upperField.setPrefWidth(JSyncerConstants.CONTROL_WIDTH / 2.0);
    }

    private long getSize(TextField field, ChoiceBox<FileSizeEnum> size) {
        if (field.getText().trim().isEmpty()) {
            return 0;
        }
        long sizeInBytes = 0;
        long sizeFromField = Long.valueOf(field.getText().trim());
        switch (size.getValue()) {
        case B:
            sizeInBytes = sizeFromField;
            break;
        case KB:
            sizeInBytes = sizeFromField * KILOBYTE;
            break;
        case MB:
            sizeInBytes = sizeFromField * (long) Math.pow(KILOBYTE, 2.0);
            break;
        case GB:
            sizeInBytes = sizeFromField * (long) Math.pow(KILOBYTE, 3.0);
            break;
        default:
            break;

        }
        return sizeInBytes;
    }
}
