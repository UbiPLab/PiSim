package com.example.secureserver.dao;

import com.example.secureserver.bean.RsuNsp;

public interface RsuNspDao {
    boolean insert(String PubKey,String Unique_id) ;
    boolean getRsuNspKey(String PubKey);
}
