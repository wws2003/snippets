//
//  PrtRESTUpdateObject.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/12/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

protocol PrtRESTUpdateObject : class {
    func toJSONParamsString() -> String!;
    func getUpdateURL() -> String!;
}