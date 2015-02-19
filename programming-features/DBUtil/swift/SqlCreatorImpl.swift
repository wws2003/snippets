//
//  SqlCreatorImpl.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/6/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

class SqlCreatorImpl : PrtSqlCreator {
    
    //Conform
    func createInsertQuery(tableName : String, columnsValueMap : TupleMap!) -> String! {
        var sql : String;
        var insertPart = "INSERT INTO " + tableName;
        var keyParts = "";
        var valuesParts = "";
        
        for (columnName, columnValue) in columnsValueMap {
            let prefix = (keyParts.isEmpty) ? "" : ",";
            let valueWrapper = (columnValue.type == SqliteConstants.DB_TYPE_TEXT || columnValue.type == SqliteConstants.DB_TYPE_SHORT_TEXT) ? "'" : "";
            
            keyParts += prefix + columnName;
            valuesParts += prefix + valueWrapper + (columnValue.value as String) + valueWrapper;
        }
        
        keyParts = "(" + keyParts + ")";
        valuesParts = "VALUES (" + valuesParts + ")";
        
        sql = insertPart + keyParts + valuesParts;
        return sql;
    }
    
    //Conform
    func createSelectQuery(tableName : String, condition: String) -> String! {
        var sql : String = "SELECT * FROM " + tableName + " WHERE " + condition;
        return sql;
    }
    
    //Conform
    func createUpdateQuery(tableName : String, columnsValueMap : TupleMap!, condition: String) -> String! {
        var updatePart : String = "UPDATE " + tableName;
        
        var setPart : String =  "";
        
        for (columnName, columnValue) in columnsValueMap {
            let prefix = (setPart.isEmpty) ? "" : ",";
            let valueWrapper = (columnValue.type == SqliteConstants.DB_TYPE_TEXT || columnValue.type == SqliteConstants.DB_TYPE_SHORT_TEXT) ? "'" : "";
            
            setPart += prefix + columnName + "=" + valueWrapper + (columnValue.value as String) + valueWrapper;
        }
        setPart = " SET " + setPart + " ";
        
        var conditionPart : String = "WHERE ";
        conditionPart += condition;
        
        var sql : String = updatePart + setPart + conditionPart;
        
        return sql;
    }

}