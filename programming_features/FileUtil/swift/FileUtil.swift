//
//  FileUtil.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/17/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

class FileUtil {
    
    class func getUserDataPath() -> String! {
        let documentPaths = NSSearchPathForDirectoriesInDomains(
            NSSearchPathDirectory.DocumentDirectory,
            NSSearchPathDomainMask.UserDomainMask, true);
        let documentPath: String = documentPaths[0] as String;
        return documentPath;
    }
    
    //Try to store data to specified path in local storage
    
    class func storeDataToPath(data : NSData!, parentDirectoryPath : String!, fileName : String!) -> Bool {
        let fileManager : NSFileManager! = NSFileManager.defaultManager();
        let filePath : String = parentDirectoryPath + "/" + fileName;
        return fileManager.createDirectoryAtPath(parentDirectoryPath, withIntermediateDirectories: true, attributes: nil, error: nil) && fileManager.createFileAtPath(filePath, contents: data, attributes: nil);
    }
}