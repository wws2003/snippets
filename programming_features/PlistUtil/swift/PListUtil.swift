//
//  PListUtil.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/5/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

public class PlistUtil {
    
    private struct Folders {
        static let INFO = "Info";
        static let PREFERENCES  = "Preferences";
        static let APP_PREFERENCES = "com.apecsa.growby.Preferences";
    }
    
    public class func getReadOnlyAttributeValue(key: String) -> AnyObject! {
        let mainBundle : NSBundle! = NSBundle.mainBundle();
        return mainBundle.objectForInfoDictionaryKey(key);
    }
    
    public class func getAttributeValue(key: String) -> AnyObject! {
        var myDict: NSMutableDictionary?
        if let path = getUserPreferencePath() {
            myDict = NSMutableDictionary(contentsOfFile: path)
        }
        if let dict = myDict {
            return dict.objectForKey(key);
        }
        return nil;
    }
    
    public class func saveAttributeValue(key: String, value: AnyObject!) {
        let path = getUserPreferencePath();
        if (path != nil) {
            var myDict: NSMutableDictionary? = NSMutableDictionary(contentsOfFile: path)
            var dict : NSMutableDictionary! = myDict;
            if(dict == nil) {
                dict = NSMutableDictionary();
            }
            dict.setObject(value, forKey: key);
            dict.writeToFile(path!, atomically: true);
        }
    }
    
    public class func removeAttribute(key : String) {
        var myDict: NSMutableDictionary?
        if let path = getUserPreferencePath() {
            myDict = NSMutableDictionary(contentsOfFile: path)
        }
        if let dict = myDict {
            let path = getUserPreferencePath();
            dict.removeObjectForKey(key);
            dict.writeToFile(path!, atomically: true);
        }
    }
    
    private class func getUserPreferencePath() -> String! {
        let libraryPaths = NSSearchPathForDirectoriesInDomains(
            NSSearchPathDirectory.LibraryDirectory,
            NSSearchPathDomainMask.UserDomainMask, true);
        
        if(libraryPaths == nil || libraryPaths.isEmpty) {
            return nil;
        }
        let libraryPath: String = libraryPaths[0] as String;
        
        var preferencePath = libraryPath
        preferencePath += "/";
        preferencePath += Folders.PREFERENCES;
        preferencePath +=  "/";
        preferencePath +=  Folders.APP_PREFERENCES;
        preferencePath += ".plist";
        
        return preferencePath;
    }
}