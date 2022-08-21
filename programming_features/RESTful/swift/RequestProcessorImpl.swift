
//
//  RequestProcessorImpl.swift
//  Growby
//
//  Created by wws2003 on 3/6/15.
//  Copyright (c) 2015 apecsa. All rights reserved.
//

import Foundation

class RequestProcessorImpl: PrtRequestProcessor {
    
    //Conform
    func processRequest(request : NSURLRequest) -> (data: NSData!, error: NSError!) {
        
        var response : NSURLResponse? = nil;
        var error : NSError?
        var data : NSData! = NSURLConnection.sendSynchronousRequest(request, returningResponse: &response, error: &error);
        
        //In the case there is no error detected, still need to check HTTP response code
        if(error == ErrorNone) {
            let httpResponse : NSHTTPURLResponse! = (response as? NSHTTPURLResponse!)!;
            
            if(httpResponse != nil) {
                let httpResponseCode = httpResponse.statusCode;
                
                if(httpResponseCode != 200) {
                    error = NSError(domain: NSURLErrorDomain, code: httpResponseCode, userInfo: nil);
                }
            }
        }
        
        return (data, error);
    }
}