//
//  ProfileViewController.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/6/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import UIKit

class ProfileViewController : UIViewController, UIAlertViewDelegate {
    
    private let SGID_LOGOUT = "SGID_LOGOUT";
    
    @IBOutlet weak var m_imvProfilePicture: UIImageView!
    @IBOutlet weak var m_aidProfilePictureFetch: UIActivityIndicatorView!
    @IBOutlet weak var m_btnTakePicture: UIButton!
    
    @IBOutlet weak var m_lbUsername: UILabel!
    @IBOutlet weak var m_lbFullname: UILabel!
    @IBOutlet weak var m_lbGender: UILabel!
    @IBOutlet weak var m_lbEmail: UILabel!
    
    private var m_user : User!;
    
    private var m_urlFetcher : PrtURLFetcher!;
    
    private var m_pictureTaker : PrtPictureTaker!;
    
    //Resource string for alert of choosing photo taking mode. Consider move to another place
    private let STR_OPTION_TAKE_PHOTO = NSLocalizedString("STR_TAKE_PHOTO", comment: "");
    private let STR_OPTION_CHOOSE_PHOTO = NSLocalizedString("STR_CHOOSE_PHOTO", comment: "");
    private let STR_OPTION_CANCEL = NSLocalizedString("STR_CANCEL", comment: "");
    private let STR_PHOTO_MENU_TITLE = NSLocalizedString("STR_CHOOSE_PHOTO_MODE_MENU_TITLE", comment: "");
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        fillView();
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidAppear(animated: Bool) {
        if(m_user != nil) {
            showProfilePicture(m_user.profilePictureURL());
        }
    }
    
    @IBAction func onBtnLogoutClicked(sender: UIButton) {
        if(m_user != nil) {
            getLogoutDelegate().userWillLogout(m_user);
            
            //Release properties to save memory
            releaseProperties();
        }
        performSegueWithIdentifier(SGID_LOGOUT, sender: sender);
    }
    
    @IBAction func onBtnTakePictureClicked(sender: UIButton) {
        /*
        -Show an alert view with 2 choices: Taking photo or access photo from library
        -self is acting as the alert view delegate
        -[FIXME] UIAlertView is deprecated since iOS 8.0, but the alternative is not avaiable in
        previous iOS version
        */
        let alertView : UIAlertView! = UIAlertView(title: STR_PHOTO_MENU_TITLE, message: "", delegate: self, cancelButtonTitle: STR_OPTION_CANCEL, otherButtonTitles: STR_OPTION_TAKE_PHOTO, STR_OPTION_CHOOSE_PHOTO);
        alertView.show();
    }
    
    //Conform to UIAlertViewDelegate
    func alertView(alertView: UIAlertView, clickedButtonAtIndex buttonIndex: Int) {
        let chosenOptionTitle = alertView.buttonTitleAtIndex(buttonIndex);
        
        //Get proper picture taker respecting user option
        m_pictureTaker = getPictureTaker(chosenOptionTitle == STR_OPTION_TAKE_PHOTO);
        
        //Transfer to proper photo taking view controller
        let pictureTakerController : UIImagePickerController! = m_pictureTaker.getPicturePickerController();
        if(pictureTakerController != nil) {
            self.presentViewController(pictureTakerController, animated: false, completion: nil);
        }
    }
    
    func setUser(user : User) {
        m_user = user;
    }
    
    private func getLogoutDelegate() -> PrtLogoutDelegate {
        return LogoutDelegateImpl();
    }
    
    private func releaseProperties() {
        m_user = nil;
        m_pictureTaker = nil;
        m_urlFetcher = nil;
        m_imvProfilePicture.image = nil; //This line helps to eliminate the memory leakage, suprisingly
    }
    
    private func fillView() {
        m_aidProfilePictureFetch.hidden = true;
        //m_btnTakePicture.hidden = true; //TODO This line is commented out for test purpose only
        if(m_user != nil) {
            m_lbUsername.text = m_user.username();
            m_lbFullname.text = m_user.fullname();
            m_lbGender.text = ConvertUtil.genderToString(m_user.gender());
            m_lbEmail.text = m_user.email();
        }
    }
    
    private func showProfilePicture(profilePictureURL : String!) {
        if(profilePictureURL != nil && !profilePictureURL.isEmpty) {
            m_urlFetcher = getURLFetcher();
            m_urlFetcher.fetchURL(profilePictureURL);
        }
        else {
            enableTakingPhoto();
        }
    }
    
    private func getURLFetcher() -> PrtURLFetcher! {
        return URLFetcherImpl(dataFetcheDelegate: UserProfilePictureFetchDelegateImpl(imvProfilePicture: m_imvProfilePicture, aidProfilePictureFetch: m_aidProfilePictureFetch));
    }
    
    private func enableTakingPhoto() {
        m_btnTakePicture.hidden = false;
    }
    
    private func getPictureTaker(useCamera : Bool) -> PrtPictureTaker {
        return SamplePictureTakerImpl(takingPictureDelegate: TakingPictureDelegateImpl(imvProfilePicture: m_imvProfilePicture, btnTakePicture: m_btnTakePicture, aidUpdateProfilePicture : m_aidProfilePictureFetch, user: m_user), useCamera: useCamera);
    }
}

class UserProfilePictureFetchDelegateImpl : PrtDataFetchDelegate {
    private weak var m_imvProfilePicture: UIImageView!
    private weak var m_aidProfilePictureFetch: UIActivityIndicatorView!;
    
    init(imvProfilePicture: UIImageView!, aidProfilePictureFetch: UIActivityIndicatorView!) {
        m_imvProfilePicture = imvProfilePicture;
        m_aidProfilePictureFetch = aidProfilePictureFetch;
    }
    
    //Conform
    func willFetchData() {
        showURLFetchActivityIndicator();
    }
    
    //Conform
    func dataFetchedSuccess(data : NSData!) {
        NSLog("Data fetched successfully !");
        hideURLFetchActivityIndicator();
        let uiImage : UIImage! = UIImage(data: data);
        if(uiImage != nil) {
            m_imvProfilePicture.image = uiImage;
        }
        NSLog("Data show successfully ! Data size = %d", data.length);
    }
    
    //Conform
    func dataFetchedFailed(error : NSError!) {
        hideURLFetchActivityIndicator();
        NSLog("Error in fetching user profile image %@", error.localizedDescription);
    }
    
    private func showURLFetchActivityIndicator() {
        m_aidProfilePictureFetch.hidden = false;
        m_aidProfilePictureFetch.startAnimating();
    }
    
    private func hideURLFetchActivityIndicator() {
        m_aidProfilePictureFetch.stopAnimating();
        m_aidProfilePictureFetch.hidden = true;
    }
}

class TakingPictureDelegateImpl : AbstractUIAsyncTaskDelegate, PrtTakingPictureDelegate {
    
    private weak var m_imvProfilePicture: UIImageView!;
    private weak var m_btnTakePicture : UIButton!;
    private weak var m_user : User!;
    private weak var m_aidUpdateProfilePicture : UIActivityIndicatorView!;

    private var m_picture : UIImage!;

    init(imvProfilePicture: UIImageView!, btnTakePicture : UIButton!, aidUpdateProfilePicture : UIActivityIndicatorView!, user: User!) {
        super.init();
        m_imvProfilePicture = imvProfilePicture;
        m_btnTakePicture = btnTakePicture;
        m_aidUpdateProfilePicture = aidUpdateProfilePicture;
        m_user = user;
    }
    
    //Conform to PrtTakingPictureDelegate
    func pictureTaken(picture : UIImage!) {
        
        //Retain picture for future purpose
        m_picture = picture;
        
        //Update user info
        updateUserProfilePicture(picture);
    }
    
    override func toWaitingScreenMode() {
        //Show the activity indicator
        m_aidUpdateProfilePicture.hidden = false;
        m_aidUpdateProfilePicture.startAnimating();
    }
    
    override func processTaskResults(results : [Any!]) {
        //Show user profile picture is task success
        let result : UserProfilePictureUpdateTaskResult! = results[0] as UserProfilePictureUpdateTaskResult;
        
        if(result != nil && result == UserProfilePictureUpdateTaskResult.UPDATE_SUCCESS) {
            m_imvProfilePicture.image = m_picture;
            //m_btnTakePicture.hidden = true; //Allow user to take the picture any times he wants
            //m_btnTakePicture.enabled = false;
        }
        else {
            NSLog("Some error occured, please try again");
            //TODO Show some notification
        }
    }
    
    override func cleanUp() {
        super.cleanUp();
        
        //Also clean the picture instance
        m_picture = nil;
    }
    
    override func exitWaitingScreenMode() {
        //Hide the activity indicator
        m_aidUpdateProfilePicture.stopAnimating();
        m_aidUpdateProfilePicture.hidden = true;
    }
    
    private func updateUserProfilePicture(picture : UIImage!) {
        let pictureData = ConvertUtil.imageToData(picture);
        
        //Start an async task UserProfilePictureUpdateTask with self as task delegate
        
        let taskController : AsyncTaskController! = AsyncTaskController(taskDelegate: self);
        let task = UserProfilePictureUpdateTask(pictureData: pictureData, userDataService: ServiceLocator.getInstance().getUserDataService(), user: m_user);
        taskController.executeAsyncTaskSerially(task);
    }
    
}