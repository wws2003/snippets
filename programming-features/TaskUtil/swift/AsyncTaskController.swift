//
//  AsyncTaskManager.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/2/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

@objc class AsyncTaskController {
    private var m_taskDelegate : PrtAsyncTaskDelegate!;
    private unowned var m_dispatchQueueService : PrtDispatchQueueService;
    
    init(taskDelegate : PrtAsyncTaskDelegate!) {
        m_taskDelegate = taskDelegate;
        m_dispatchQueueService = ServiceLocator.getInstance().getDispatchQueueService();
    }
    
    func executeAsyncTask(task : PrtTask) {
        m_taskDelegate?.taskWillExecute(task);
        
        var globalDispatchQueue  : dispatch_queue_t = m_dispatchQueueService.getNonSerialDispatchQueue();
        var taskBlock : dispatch_block_t = {() -> Void in self.executeTaskInBackground(task)};
        
        dispatch_async(globalDispatchQueue, taskBlock);
    }
    
    func executeAsyncTaskSerially(task : PrtTask!) {
        m_taskDelegate?.taskWillExecute(task);
        
        var serialDispatchQueue : dispatch_queue_t = m_dispatchQueueService.getSerialDispatchQueue();
        var taskBlock : dispatch_block_t = {() -> Void in self.executeTaskInBackground(task)};
        
        dispatch_async(serialDispatchQueue, taskBlock);
    }
    
    func executeSyncTask(task : PrtTask!) {
        m_taskDelegate?.taskWillExecute(task);
        
        var serialDispatchQueue  : dispatch_queue_t = m_dispatchQueueService.getSerialDispatchQueue();
        var taskBlock : dispatch_block_t = {() -> Void in self.executeTaskInBackground(task)};
        
        dispatch_sync(serialDispatchQueue, taskBlock);
    }
    
    func executeTaskInBackground(task : PrtTask!) {
        task?.execute();
        var mainDispatchQueue : dispatch_queue_t = dispatch_get_main_queue();
        if(m_taskDelegate != nil) {
            var block : dispatch_block_t! = {() -> Void in self.m_taskDelegate.taskDidExecute()};
            dispatch_async(mainDispatchQueue, block);
        }
    }
}