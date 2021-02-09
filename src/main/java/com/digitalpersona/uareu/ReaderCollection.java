package com.digitalpersona.uareu;

import java.util.List;

public interface ReaderCollection extends List<Reader> {
    void GetReaders() throws UareUException;
}