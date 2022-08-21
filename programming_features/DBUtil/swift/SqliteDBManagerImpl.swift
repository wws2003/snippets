//
//  SqliteDBManagerImpl.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/3/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

public class SqliteDBManagerImpl : PrtDBManager {
    private var m_pDB : COpaquePointer = nil;
    private var m_dbFileName : String!;
    private var m_dbVersionKeyName : String!; //TODO Pass through constructor
    
    public init(version: Int, dbFileName : String, dbVersionKeyName : String) {
        m_dbFileName = dbFileName;
        m_dbVersionKeyName = dbVersionKeyName;
        initByVersion(version);
    }
    
    deinit {
        //closeConnection();
    }
    
    private func initByVersion(version : Int) {
        var openDBResult = openConnection();
        if(openDBResult == SQLITE_OK) {
            if(newVersion(version)) {
                dropSchema();
                createSchema();
                updateVersion(version);
            }
        }
    }
    
    public func dropSchema() {
        // Drop tables. Should be implemented in subclass ?
    }
    
    public func createSchema() {
        // Create tables if not existed. Should be implemented in subclass ?
    }
    
    //Conform
    public func connectToLocalDB() -> DBConnectResult {
        //Open connection to db
        
        return openConnection() == SQLITE_OK ? DBConnectResult.CONNECT_SUCCESS : DBConnectResult.CONNECT_FAILED;
    }
    
    //Conform
    public func disconnectToLocalDB() -> DBDisConnectResult {
        return closeConnection() == SQLITE_OK ? DBDisConnectResult.DISCONNECT_SUCCESS : DBDisConnectResult.DISCONNECT_FAILED;
    }
    
    //Conform
    public func executeSQL(sql: String) -> SqlExecuteResult {
        var emptyOutputTuples : [TupleMap!]! = nil;
        return executeSQL(sql, outputTuples: &emptyOutputTuples);
    }
    
    //Conform
    public func executeSQL(sql: String, inout outputTuples: [TupleMap!]!) -> SqlExecuteResult {
        //Prepare statement
        //TODO [Optimize] Parse params from sql if possible
        var pStatement : COpaquePointer = nil;
        var cSql : [CChar]! = sql.cStringUsingEncoding(NSUTF8StringEncoding);
        var prepareStatementResult : Int32 = sqlite3_prepare_v2(m_pDB, cSql, -1, &pStatement, nil);
        if(prepareStatementResult != SQLITE_OK) {
            NSLog("Error %@", NSString(CString: sqlite3_errmsg(m_pDB), encoding: NSUTF8StringEncoding)!);
            return SqlExecuteResult.EXECUTE_FAILED;
        }
        
        //Execute
        var executeResult : Int32;
        
        do {
            executeResult = sqlite3_step(pStatement);
            if(outputTuples != nil && executeResult == SQLITE_ROW) {
                var columnCount = sqlite3_column_count(pStatement);
                var newTuple : TupleMap = [ : ];
                for column in 0...columnCount - 1 {
                    bindColumnsToTuple(pStatement, column: column, outputTuple: &newTuple);
                }
                outputTuples.append(newTuple);
            }
        }
        while(executeResult == SQLITE_ROW);
        
        //Finalize
        sqlite3_finalize(pStatement);
        
        //Need to free statement pointer? May be not
        
        return executeResult == SQLITE_DONE ? SqlExecuteResult.EXECUTE_SUCCESS : SqlExecuteResult.EXECUTE_FAILED;
    }
    
    //Conform
    public func beginTransaction() {
        //TODO Implement
    }
    
    //Conform
    public func commitTransaction() {
        //TODO Implement
    }
    
    //Conform
    public func rollbackTransaction() {
        //TODO Implement
    }
    
    private func openConnection() -> Int32 {
        //Check db status before really open a new connection. If the connection is already avaiable, do nothing
        
        if(checkDBConnectionStatus() == SQLITE_OK) {
            return SQLITE_OK;
        }
        
        var cFileName : [CChar]! = getDBFilePath();
        var flags : Int32 = SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE;
        var zVfs : UnsafePointer<Int8> = nil;

        return sqlite3_open_v2(cFileName, &self.m_pDB, flags, zVfs);
    }
    
    private func checkDBConnectionStatus() -> Int32 {
        if(m_pDB != nil) {
            var cur : Int32 = 0;
            var hiwtr : Int32 = 0;
            let dbStatus = sqlite3_db_status(m_pDB, SQLITE_DBSTATUS_LOOKASIDE_USED, &cur, &hiwtr, 0);
            NSLog("-----------------------DB Status %d %d", dbStatus, cur);
            return dbStatus;
        }
        return SQLITE_CANTOPEN;
    }
    
    private func newVersion(version : Int) -> Bool {
        var currentVersion = PlistUtil.getAttributeValue(m_dbVersionKeyName) as Int!;
        if(currentVersion == nil) {
            return true;
        }
        return version > currentVersion || (version == 0);
    }

    private func updateVersion(version : Int) {
        PlistUtil.saveAttributeValue(m_dbVersionKeyName, value: version);
    }
    
    private func closeConnection() -> Int32 {
        var ret :Int32 = sqlite3_close(m_pDB);
        //FIXME Need to free m_pDB manualy ?
        m_pDB = nil; //Just to make sure connection can not be closed more than once
        return ret;
    }
    
    private func bindColumnsToTuple(pStatement : COpaquePointer, column: Int32, inout outputTuple: TupleMap) {
        var columnType = sqlite3_column_type(pStatement, column);
        var cColumnName = sqlite3_column_name(pStatement, column); //FIXME Need to free this pointer manualy ?
        var columnName : String! = NSString(CString: cColumnName, encoding: NSUTF8StringEncoding);
        
        switch columnType {
        case SQLITE_INTEGER:
            let intValue = Int(sqlite3_column_int64(pStatement, column));
            outputTuple[columnName] = TupleValue(type: SqliteConstants.DB_TYPE_INTEGER, value: intValue);
            break;
        case SQLITE_TEXT:
            let cTextValue = UnsafePointer<Int8>(sqlite3_column_text(pStatement, column)); //FIXME Need to free this pointer manualy ?

            let textValue = NSString(CString: cTextValue, encoding: NSUTF8StringEncoding);
            outputTuple[columnName] = TupleValue(type: SqliteConstants.DB_TYPE_TEXT, value: textValue);
            break;
        case SQLITE_FLOAT:
            let doubleValue = Double(sqlite3_column_double(pStatement, column));
            outputTuple[columnName] = TupleValue(type: SqliteConstants.DB_TYPE_FLOAT, value: doubleValue);
            break;
        case SQLITE_BLOB:
            let cBlobValue = sqlite3_column_blob(pStatement, column);
            if(cBlobValue == nil) {
                outputTuple[columnName] = nil;
            }
            else {
                let columnBytes = sqlite3_column_bytes(pStatement, column); //FIXME Need to free this pointer manualy ?
                let data = NSData(bytes: cBlobValue, length: Int(columnBytes));
                outputTuple[columnName] = TupleValue(type: SqliteConstants.DB_TYPE_BLOB, value: data);
            }
            break;
        case SQLITE_NULL:
            outputTuple[columnName] = nil;
            break;
        default:
            break;
        }
    }
    
    private func getDBFilePath() -> [CChar]! {
        let documentPath: String! = FileUtil.getUserDataPath();
        let dbFilePath = documentPath + "/" + m_dbFileName;
        NSLog("DB file path expected %@", dbFilePath);
        
        return dbFilePath.cStringUsingEncoding(NSUTF8StringEncoding);
    }
}