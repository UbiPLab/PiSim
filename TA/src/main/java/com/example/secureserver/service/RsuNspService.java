package com.example.secureserver.service;

import org.ujmp.core.objectmatrix.calculation.Unique;

public interface RsuNspService {
    boolean insert(String PubKey, String Unique_id);
    boolean getRsuNspKey(String PubKey);
}
