//
//  SamplePictureTakerImpl.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/16/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import UIKit
import MobileCoreServices

class SamplePictureTakerImpl : NSObject, PrtPictureTaker, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    private var m_takingPictureDelegate : PrtTakingPictureDelegate!;
    private var m_pictureSourceType : UIImagePickerControllerSourceType! = UIImagePickerControllerSourceType.PhotoLibrary;
    
    init(takingPictureDelegate : PrtTakingPictureDelegate!, useCamera : Bool = false) {
        m_takingPictureDelegate = takingPictureDelegate;
        if(useCamera) {
            m_pictureSourceType = UIImagePickerControllerSourceType.Camera;
        }
    }
    
    //Conform to PrtPictureTaker
    func getPicturePickerController() -> UIImagePickerController! {
        let uiImagePickerController : UIImagePickerController! = UIImagePickerController();
        uiImagePickerController.sourceType = m_pictureSourceType;
        uiImagePickerController.mediaTypes = [kUTTypeImage];
        uiImagePickerController.delegate = self;
        return uiImagePickerController;
    }
    
    //Conform to UIImagePickerControllerDelegate
    func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [NSObject : AnyObject]) {
        let mediaType : String! = info[UIImagePickerControllerMediaType] as String!;
        NSLog("Taken media type :%@", mediaType);
        if(mediaType == kUTTypeImage) {
            let uiImage = info[UIImagePickerControllerOriginalImage] as UIImage!;
            if(uiImage != nil && m_takingPictureDelegate != nil) {
                m_takingPictureDelegate.pictureTaken(uiImage);
            }
        }
        picker.dismissViewControllerAnimated(false, completion: nil);
    }
}