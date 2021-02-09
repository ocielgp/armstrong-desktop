package com.digitalpersona.uareu;

import com.digitalpersona.uareu.dpfj.QualityImpl;
import com.digitalpersona.uareu.dpfj.CompressionImpl;
import com.digitalpersona.uareu.dpfj.ImporterImpl;
import com.digitalpersona.uareu.dpfj.EngineImpl;
import com.digitalpersona.uareu.dpfpdd.ReaderCollectionImpl;

public final class UareUGlobal {
    private static ReaderCollectionImpl m_collection;
    private static EngineImpl m_engine;
    private static ImporterImpl m_importer;
    private static CompressionImpl m_compression;
    private static QualityImpl m_quality;

    public static ReaderCollection GetReaderCollection() throws UareUException {
        if (null == UareUGlobal.m_collection) {
            try {
                (UareUGlobal.m_collection = (ReaderCollectionImpl) Class.forName("com.digitalpersona.uareu.dpfpdd.ReaderCollectionImpl").newInstance()).Initialize();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (ClassNotFoundException e3) {
                e3.printStackTrace();
            } catch (UareUException e4) {
                UareUGlobal.m_collection = null;
                throw e4;
            }
        }
        return UareUGlobal.m_collection;
    }

    public static void DestroyReaderCollection() throws UareUException {
        if (null != UareUGlobal.m_collection) {
            UareUGlobal.m_collection.Release();
            UareUGlobal.m_collection = null;
        }
    }

    public static Engine GetEngine() {
        if (null == UareUGlobal.m_engine) {
            try {
                UareUGlobal.m_engine = (EngineImpl) Class.forName("com.digitalpersona.uareu.dpfj.EngineImpl").newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (ClassNotFoundException e3) {
                e3.printStackTrace();
            }
        }
        return UareUGlobal.m_engine;
    }

    public static Importer GetImporter() {
        if (null == UareUGlobal.m_importer) {
            try {
                UareUGlobal.m_importer = (ImporterImpl) Class.forName("com.digitalpersona.uareu.dpfj.ImporterImpl").newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (ClassNotFoundException e3) {
                e3.printStackTrace();
            }
        }
        return UareUGlobal.m_importer;
    }

    public static Compression GetCompression() {
        if (null == UareUGlobal.m_compression) {
            try {
                UareUGlobal.m_compression = (CompressionImpl) Class.forName("com.digitalpersona.uareu.dpfj.CompressionImpl").newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (ClassNotFoundException e3) {
                e3.printStackTrace();
            }
        }
        return UareUGlobal.m_compression;
    }

    public static Quality GetQuality() {
        if (null == UareUGlobal.m_quality) {
            try {
                UareUGlobal.m_quality = (QualityImpl) Class.forName("com.digitalpersona.uareu.dpfj.QualityImpl").newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (ClassNotFoundException e3) {
                e3.printStackTrace();
            }
        }
        return UareUGlobal.m_quality;
    }

    static {
        UareUGlobal.m_collection = null;
        UareUGlobal.m_engine = null;
        UareUGlobal.m_importer = null;
        UareUGlobal.m_compression = null;
        UareUGlobal.m_quality = null;
    }
}