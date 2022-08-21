//
//  RESTProcessorImpl.swift
//  Growby
//
//  Created by wws2003 on 3/15/15.
//  Copyright (c) 2015 apecsa. All rights reserved.
//

import Foundation

class RESTProcessorImpl : PrtRESTProcessor {
    
    private var m_urlRequestProcessor: PrtRequestProcessor!;
    private let MULTIPART_DATA_BOUNDARY_STRING = "-------------------------0123456789ABCDEFGH";
    
    init(urlRequestProcessor: PrtRequestProcessor! = nil) {
        m_urlRequestProcessor = urlRequestProcessor;
    }
    
    //Conform
    func create(restObject : PrtRESTCreateObject, resultProcessor: PrtRESTResultProcessor){
        let createURL = restObject.getCreateURL();
        
        if(createURL != nil) {
            let jsonData : NSData! = ConvertUtil.JSONStringToNSData(restObject.toJSONParamsString());
            
            //Make a http request to the create url. Set json for content type and accept header field
            let authorizationHeaderAttribute = restObject.getAuthorizationHeaderAttribute();
            let request = jsonify(getCreateURLRequest(createURL, authorizationHeaderAttribute: authorizationHeaderAttribute, jsonData: jsonData));
            
            processRequest(request, resultProcessor: resultProcessor);
        }
    }
    
    //Conform
    func delete(restObject : PrtRESTDeleteObject, resultProcessor: PrtRESTResultProcessor){
        let deleteURL = restObject.getDeleteURL();
        
        if(deleteURL != nil) {
            
            //Make a http request to the delete url. Set json for content type and accept header field
            let authorizationHeaderAttribute = restObject.getAuthorizationHeaderAttribute();
            let request = jsonify(getDeleteURLRequest(deleteURL, authorizationHeaderAttribute: authorizationHeaderAttribute));
            
            processRequest(request, resultProcessor: resultProcessor);
        }
    }
    
    //Conform
    func retrieve(restObject : PrtRESTRetrieveObject, resultProcessor: PrtRESTResultProcessor){
        let retrieveURL = restObject.getRetrieveURL();
        
        if(retrieveURL != nil) {
            let jsonDataString : String! = restObject.toJSONParamsString();
            
            let jsonData : NSData! = jsonDataString != nil ? ConvertUtil.JSONStringToNSData(jsonDataString) : nil;
            
            //Make a http request to the retrieve url
            let authorizationHeaderAttribute = restObject.getAuthorizationHeaderAttribute();
            
            let request : NSURLRequest = getRetrieveURLRequest(retrieveURL, authorizationHeaderAttribute: authorizationHeaderAttribute, retrieveDataType: restObject.getRetrieveDataType(), data: jsonData);
            
            processRequest(request, resultProcessor: resultProcessor);
        }
    }
    
    //Conform
    func update(restObject : PrtRESTUpdateObject, resultProcessor: PrtRESTResultProcessor){
        var updateURL = restObject.getUpdateURL();
        
        if(updateURL != nil) {
            let jsonData : NSData! = ConvertUtil.JSONStringToNSData(restObject.toJSONParamsString());
            
            //Make a http request to the update url. Set json for content type and accept header field
            let authorizationHeaderAttribute = restObject.getAuthorizationHeaderAttribute();
            var request : NSURLRequest = jsonify(getUpdateURLRequest(updateURL, authorizationHeaderAttribute: authorizationHeaderAttribute, data: jsonData));
            
            processRequest(request, resultProcessor: resultProcessor);
        }
    }
    
    //Conform
    func updateBinary(restObject: PrtRESTUpdateBinaryObject, resultProcessor: PrtRESTResultProcessor) {
        var updateURL = restObject.getUpdateURL();
        
        if(updateURL != nil) {
            //Get binary data in appropriate format with http body
            let binaryData : NSData! = getBinaryHTTPBody(restObject.getBinaryData());
            
            //Make a http request to the update url. Set binary mode for content type
            let authorizationHeaderAttribute = restObject.getAuthorizationHeaderAttribute();
            var request : NSURLRequest = blobify(getUpdateURLRequest(updateURL, authorizationHeaderAttribute: authorizationHeaderAttribute, data: binaryData), dataLength: binaryData.length);
            
            processRequest(request, resultProcessor: resultProcessor);
        }
    }
    
    /* 
    ----Begin of methods to get request object: Create, retrieve, update, delete----
    */
    
    private func getCreateURLRequest(createURL : String, authorizationHeaderAttribute: String!, jsonData: NSData!) -> NSMutableURLRequest {
        var request = getAuthorizedRequest(createURL, authorizationHeaderAttribute: authorizationHeaderAttribute, method: "POST");
        request.HTTPBody = jsonData;
        return request;
    }
    
    private func getRetrieveURLRequest(retrieveURL : String, authorizationHeaderAttribute: String!, retrieveDataType: RETRIEVE_DATA_TYPE, data: NSData!) -> NSURLRequest {
        
        var request = getAuthorizedRequest(retrieveURL, authorizationHeaderAttribute: authorizationHeaderAttribute);
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type");
        request.addValue(getHttpAcceptFromRetrieveDataType(retrieveDataType), forHTTPHeaderField: "Accept");
        
        //If there is no data attached, consider as GET request. Otherwise is POST request
        if(data == nil) {
            request.HTTPMethod = "GET";
        }
        else {
            request.HTTPMethod = "POST";
            request.HTTPBody = data;
        }
        
        return request;
    }
    
    private func getDeleteURLRequest(deleteURL: String, authorizationHeaderAttribute: String!) -> NSMutableURLRequest {
        return getAuthorizedRequest(deleteURL, authorizationHeaderAttribute: authorizationHeaderAttribute, method: "DELETE");
    }
    
    private func getUpdateURLRequest(updateURL: String, authorizationHeaderAttribute: String!, data: NSData! ) -> NSMutableURLRequest {
        
        //PUT method is fixed for update request, as a convenience in Growby
        var request = getAuthorizedRequest(updateURL, authorizationHeaderAttribute: authorizationHeaderAttribute, method: "PUT");
        request.HTTPBody = data;
        return request;
    }
    
    /*
    ----End of methods to get request object----
    */

    private func jsonify(request: NSMutableURLRequest) -> NSURLRequest {
        request.addValue("application/json", forHTTPHeaderField: "Content-Type");
        request.addValue("application/json", forHTTPHeaderField: "Accept");
        return request;
    }
    
    private func blobify(request: NSMutableURLRequest, dataLength: Int) -> NSURLRequest {
        let headerContentType = String(format: "multipart/form-data; boundary=%@", MULTIPART_DATA_BOUNDARY_STRING);
        request.addValue(headerContentType, forHTTPHeaderField: "Content-Type");
        request.addValue(String(dataLength), forHTTPHeaderField: "Content-Length");
        return request;
    }
    
    private func getHttpAcceptFromRetrieveDataType(type: RETRIEVE_DATA_TYPE) -> String {
        switch type {
        case RETRIEVE_DATA_TYPE.JSON:
            return "application/json";
        case RETRIEVE_DATA_TYPE.IMAGE_JPEG:
            return "image/jpeg";
        }
    }
    
    private func getBinaryHTTPBody(data: NSData!) -> NSData! {
        var body : NSMutableData = NSMutableData();
        
        //Append meta data for multipart reader
        let boundaryLineString : String = String(format: "--%@\r\n", MULTIPART_DATA_BOUNDARY_STRING);
        body.appendData(boundaryLineString.dataUsingEncoding(NSUTF8StringEncoding)!);
        
        //Currently fixed binary data type as image only. TODO Generalize
        
        let contentDispositionString : String = "Content-Disposition: form-data; name=\"image\"; filename=\"picture.jpg\"\r\n\r\n";
        body.appendData(contentDispositionString.dataUsingEncoding(NSUTF8StringEncoding)!);
        
        //FIXME: Adding the below 2 lines cause server parsing image problem. Don't understand !
        //let contentTypeString : String = "Content-Type: image/jpeg\r\n\r\n";
        //body.appendData(contentTypeString.dataUsingEncoding(NSUTF8StringEncoding)!);
        
        //Append main data
        body.appendData(data);
        
        let endOfMainData : String = "\r\n\r\n";
        body.appendData(endOfMainData.dataUsingEncoding(NSUTF8StringEncoding)!);
        
        //Close trail of data
        let closeString : String = String(format: "--%@--\r\n", MULTIPART_DATA_BOUNDARY_STRING);
        body.appendData(closeString.dataUsingEncoding(NSUTF8StringEncoding)!);
        
        return body;
    }
    
    private func getAuthorizedRequest(url: String, authorizationHeaderAttribute: String!, method: String! = nil) -> NSMutableURLRequest {
        
        //Make a simple request given the authorization attribute
        var request : NSMutableURLRequest = NSMutableURLRequest()
        request.URL = NSURL(string: url);
        
        if(method != nil) {
            request.HTTPMethod = method;
        }
        
        if(authorizationHeaderAttribute != nil) {
            request.addValue(authorizationHeaderAttribute, forHTTPHeaderField: "Authorization");
        }
        return request;
    }
    
    private func processRequest(request: NSURLRequest, resultProcessor: PrtRESTResultProcessor) {
        let requestResult = m_urlRequestProcessor.processRequest(request);
        resultProcessor.processRESTResults(requestResult.data, error: requestResult.error);
    }

}