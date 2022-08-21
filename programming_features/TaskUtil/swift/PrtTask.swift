//
//  PrtTask.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/2/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

protocol PrtTask : class {
    func execute();
    func getResults() -> [Any!];
}