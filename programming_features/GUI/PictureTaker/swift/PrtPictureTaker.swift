//
//  PrtPictureTaker.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/16/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import UIKit

protocol PrtPictureTaker : class {
    func getPicturePickerController() -> UIImagePickerController!
}