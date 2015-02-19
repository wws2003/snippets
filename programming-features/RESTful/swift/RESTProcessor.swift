//
//  RESTProcessor.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/10/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

class RESTProcessor {
    
    init() {
        
    }
    
    func create(restObject : PrtRESTCreateObject, restResultProcessor : PrtRESTResultProcessor!) {
        let createURL = restObject.getCreateURL();
    
        if(createURL != nil) {
            var request : NSMutableURLRequest = NSMutableURLRequest()
            request.URL = NSURL(string: createURL);
            request.HTTPMethod = "POST";
            let jsonData : NSData! = ConvertUtil.JSONStringToNSData(restObject.toJSONParamsString());
            request.HTTPBody = jsonData;
        
            var response : NSURLResponse? = nil;
            var data : NSData! = NSURLConnection.sendSynchronousRequest(request, returningResponse: &response, error: nil);
            restResultProcessor.processRESTResults(data);
        }
    }
    
    func delete(restObject : PrtRESTDeleteObject, restResultProcessor : PrtRESTResultProcessor!) {
        let deleteURL = restObject.getDeleteURL();
        
        if(deleteURL != nil) {
            var request : NSMutableURLRequest = NSMutableURLRequest()
            request.URL = NSURL(string: deleteURL);
            request.HTTPMethod = "DELETE";
        
            var response : NSURLResponse? = nil;
            var data : NSData! = NSURLConnection.sendSynchronousRequest(request, returningResponse: &response, error: nil);
            restResultProcessor.processRESTResults(data);
        }
    }
    
    func retrieve(restObject : PrtRESTRetrieveObject, restResultProcessor : PrtRESTResultProcessor!) {
        let retrieveURL = restObject.getRetrieveURL();
        
        if(retrieveURL != nil) {
            var request : NSMutableURLRequest = NSMutableURLRequest()
            request.URL = NSURL(string: retrieveURL);
            request.HTTPMethod = "GET";
        
            var response : NSURLResponse? = nil;
            var data : NSData! = NSURLConnection.sendSynchronousRequest(request, returningResponse: &response, error: nil);
            restResultProcessor.processRESTResults(data);
        }
    }
    
    func update(restObject : PrtRESTUpdateObject, restResultProcessor : PrtRESTResultProcessor!) {
        var updateURL = restObject.getUpdateURL();
        
        if(updateURL != nil) {
            var request : NSMutableURLRequest = NSMutableURLRequest()
            request.URL = NSURL(string: updateURL);
            request.HTTPMethod = "PUT";
            let jsonData : NSData! = ConvertUtil.JSONStringToNSData(restObject.toJSONParamsString());
            request.HTTPBody = jsonData;
        
            var response : NSURLResponse? = nil;
            var data : NSData! = NSURLConnection.sendSynchronousRequest(request, returningResponse: &response, error: nil);
            restResultProcessor.processRESTResults(data);
        }
    }
}