//
//  URLFetcherImpl.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/16/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

class URLFetcherImpl : PrtURLFetcher {
    private var m_dataFetcheDelegate : PrtDataFetchDelegate!;
    
    init(dataFetcheDelegate : PrtDataFetchDelegate!) {
        m_dataFetcheDelegate = dataFetcheDelegate;
    }
    
    //Conform
    func fetchURL(url : String!) {
        if(url != nil && !url.isEmpty) {
            m_dataFetcheDelegate.willFetchData();
            
            if(isHttpURL(url)) {
                //Handle the case of web data by making GET request
                let request : NSMutableURLRequest = NSMutableURLRequest()
                request.URL = NSURL(string: url);
                request.HTTPMethod = "GET";
                
                var response : NSURLResponse? = nil;
                
                NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue(), completionHandler: {
                    (response, data, error) -> Void in
                    self.profilePictureWasFetched(response, data: data, error: error);
                });
            }
            else {
                //Handle the case of local data
                let fileManger = NSFileManager.defaultManager();
                let path : String = FileUtil.getUserDataPath() + "/" + url;
                let data : NSData! = fileManger.contentsAtPath(path);
                
                //The case able to read data
                if(data != nil) {
                    m_dataFetcheDelegate.dataFetchedSuccess(data);
                }
                else {
                    let error : NSError = NSError(domain: "URLFetcher", code: -1, userInfo: nil);
                    m_dataFetcheDelegate.dataFetchedFailed(error);
                }
            }
        }
    }
    
    
    private func isHttpURL(url : String) -> Bool {
        let httpString : String = "http";
        let urlLowcase : String = url.lowercaseString;
        return urlLowcase.hasPrefix(httpString);
    }
    
    private func profilePictureWasFetched(response : NSURLResponse!, data : NSData!, error :NSError!) {
        if(possibleToShowImage(response, error: error)) {
            m_dataFetcheDelegate.dataFetchedSuccess(data);
        }
        else {
            m_dataFetcheDelegate.dataFetchedFailed(error);
        }
    }
    
    private func possibleToShowImage(response : NSURLResponse!, error : NSError!) -> Bool {
        return (error == nil);
    }

}