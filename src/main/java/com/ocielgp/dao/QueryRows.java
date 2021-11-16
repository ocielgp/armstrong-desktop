package com.ocielgp.dao;

import javafx.collections.ObservableList;

public class QueryRows {
    private final ObservableList<?> data;
    public final Integer pages;
    public Integer rows;

    public QueryRows(ObservableList<?> data, int rows, int pages) {
        this.data = data;
        this.rows = rows;
        this.pages = pages;
    }

    public ObservableList<?> getData() {
        return data;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getPages() {
        return pages;
    }
}
