//
//  PrtRESTUpdateBinaryObject].swift
//  Growby
//
//  Created by wws2003 on 3/12/15.
//  Copyright (c) 2015 apecsa. All rights reserved.
//

import Foundation

protocol PrtRESTUpdateBinaryObject : PrtRESTAuthorizationObj {
    func getUpdateURL() -> String!;
    func getBinaryData() -> NSData!;
}