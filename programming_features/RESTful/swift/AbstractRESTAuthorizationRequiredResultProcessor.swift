//
//  AbstractRESTAuthorizationRequiredResultProcessor.swift
//  Growby
//
//  Created by wws2003 on 3/10/15.
//  Copyright (c) 2015 apecsa. All rights reserved.
//

import Foundation

class AbstractRESTAuthorizationRequiredResultProcessor : PrtRESTResultProcessor {
    
    private var m_firstResult: NSError!;
    private var m_successResults: [Any!] = [];

    init() {
        
    }
    
    //Conform
    func processRESTResults(restResult : NSData!, error : NSError!)  {
        m_firstResult = NSError(domain: UserDataServiceErrorDomain.AUTHENTICATION.rawValue, code: UserDataServiceErrorCode.UNKNOWN.rawValue, userInfo: nil);
        
        if(error == ErrorNone) {
            let result = processRESTResultWhenErrorNotDetected(restResult);
            m_firstResult = result.firstResult;
            if(m_firstResult == ErrorNone) {
                m_successResults = result.successResults;
            }
        }
        else {
            NSLog("Error in REST api %@", error.localizedDescription);
            //Try to comprehend the error from url request error. This should not happen but there are cases...
            
            //Detect status code from the error, normally a HTTP error status
            let restStatus = detectRESTStatusFromError(error);
            
            //Then convert to Growby domain error
            m_firstResult = getErrorFromStatus(restStatus);
        }
    }
    
    //Conform
    func getFinalResults() -> [Any!] {
        if(m_firstResult != ErrorNone) {
            return [m_firstResult];
        }
        else {
            var results : [Any!] = [m_firstResult];
            for successResult in m_successResults {
                results.append(successResult);
            }
            return results;
        }
    }
    
    func detectRESTStatusFromError(error: NSError) -> RESTConstants.REST_STATUS {
        //Implement in sub-class
        return RESTConstants.REST_STATUS.ERROR_UNKNOWN;
    }
    
    func getErrorFromStatus(errorStatus : RESTConstants.REST_STATUS) -> NSError! {
        //Should be overridden in subclass for other errors
        
        switch errorStatus {
        case RESTConstants.REST_STATUS.SUCCESS:
            return ErrorNone;
            
        case RESTConstants.REST_STATUS.ERROR_AUTHENTICATION:
            return NSError(domain: UserDataServiceErrorDomain.AUTHENTICATION.rawValue, code: UserDataServiceErrorCode.AUTHENTICATION_FAILED_WRONG_USERNAME_PASSWORD.rawValue, userInfo: nil);
            
        default:
            return NSError(domain: UserDataServiceErrorDomain.AUTHENTICATION.rawValue, code: UserDataServiceErrorCode.UNKNOWN.rawValue, userInfo: nil);
        }
    }
    
    func processRESTResultWhenErrorNotDetected(restResult : NSData!) -> (firstResult: NSError!, successResults: [Any!]) {
        
        let jsonDict : JSONDictionary! = ConvertUtil.JSONDataToJSONDictionary(restResult);
        if(jsonDict != nil) {
            let successResults = getSuccessResults(jsonDict);
            return (ErrorNone, successResults);
            
        }
        
        return (NSError(domain: UserDataServiceErrorDomain.RECEIVED_DATA.rawValue, code: UserDataServiceErrorCode.RECEIVED_DATA_PARSING_ERROR.rawValue, userInfo: nil), []);
        
    }
    
    func getSuccessResults(jsonDict : JSONDictionary) -> [Any!] {
        //Implement in sub-class
        return [];
    }

}