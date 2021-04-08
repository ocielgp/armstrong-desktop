package com.ocielgp.database;

import javafx.collections.ObservableList;

public class QueryRows {
    private final ObservableList data;
    private final int rows;
    private final int pages;

    public QueryRows(ObservableList data, int rows, int pages) {
        this.data = data;
        this.rows = rows;
        this.pages = pages;
    }

    public ObservableList getData() {
        return data;
    }

    public int getRows() {
        return rows;
    }

    public int getPages() {
        return pages;
    }
}
