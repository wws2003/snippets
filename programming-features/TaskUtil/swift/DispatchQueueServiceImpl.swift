//
//  DispatchQueueServiceImpl.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/19/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

class DispatchQueueServiceImpl : PrtDispatchQueueService {
    
    struct SerialTaskQueueManager {
        static var g_serialQueue : dispatch_queue_t!;
        static let SERIAL_QUEUE_NAME = "SERIAL_QUEUE_GB";
        
        static func getSerialQueue() -> dispatch_queue_t {
            if(g_serialQueue == nil) {
                g_serialQueue = dispatch_queue_create(SERIAL_QUEUE_NAME, nil);
            }
            return g_serialQueue;
        }
    }
    
    func getSerialDispatchQueue()->dispatch_queue_t {
        return SerialTaskQueueManager.getSerialQueue();
    }
    
    func getNonSerialDispatchQueue()->dispatch_queue_t {
        return dispatch_get_global_queue(QOS_CLASS_BACKGROUND, 0);
    }
}