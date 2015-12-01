//
//  RequestProcessorMockImpl.swift
//  Growby
//
//  Created by wws2003 on 3/7/15.
//  Copyright (c) 2015 apecsa. All rights reserved.
//

import Foundation

class RequestProcessorMockImpl: PrtRequestProcessor {
    
    private var m_jsonString: String!;
    
    init(jsonString: String!) {
        m_jsonString = jsonString;
    }
    
    func processRequest(request : NSURLRequest) -> (data: NSData!, error: NSError!) {
        return (ConvertUtil.JSONStringToNSData(m_jsonString), ErrorNone);
    }
}