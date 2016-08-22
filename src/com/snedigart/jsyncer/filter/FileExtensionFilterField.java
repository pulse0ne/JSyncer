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

import com.snedigart.jsync.filter.FileExtensionSyncFilter;
import com.snedigart.jsync.filter.SyncFilter;

/**
 * FileExtensionFilterField class
 * 
 * @author Tyler Snedigar
 * @version 1.0
 */
public class FileExtensionFilterField extends FilenameFilterField {

    public FileExtensionFilterField() {
        super();
        field.setPromptText("example: .exe");
    }

    @Override
    public SyncFilter getFilter() {
        return new FileExtensionSyncFilter(field.getText().trim());
    }
}
