//
//  PrtRequestProcessor.swift
//  Growby
//
//  Created by wws2003 on 3/6/15.
//  Copyright (c) 2015 apecsa. All rights reserved.
//

import Foundation

protocol PrtRequestProcessor : class {
    func processRequest(request : NSURLRequest) -> (data: NSData!, error: NSError!);
}