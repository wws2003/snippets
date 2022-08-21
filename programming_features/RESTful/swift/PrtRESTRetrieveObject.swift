//
//  PrtRESTRetrieveObject.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/12/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

enum RETRIEVE_DATA_TYPE : Int{
    case JSON = 0;
    case IMAGE_JPEG = 1;
}

protocol PrtRESTRetrieveObject : PrtRESTAuthorizationObj {
    func getRetrieveURL() -> String!;
    
    //This method is for cases of POST request
    func toJSONParamsString() -> String!;
    
    //Get type of data expected to retrieve
    func getRetrieveDataType() -> RETRIEVE_DATA_TYPE;
}