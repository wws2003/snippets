//
//  PrtRESTAuthorizationObj.swift
//  Growby
//
//  Created by wws2003 on 3/8/15.
//  Copyright (c) 2015 apecsa. All rights reserved.
//

import Foundation

protocol PrtRESTAuthorizationObj : class {
    func getAuthorizationHeaderAttribute() -> String!;
}