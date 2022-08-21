//
//  PrtDispatchQueueService.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/19/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

protocol PrtDispatchQueueService : class {
    func getSerialDispatchQueue()->dispatch_queue_t;
    func getNonSerialDispatchQueue()->dispatch_queue_t;
}