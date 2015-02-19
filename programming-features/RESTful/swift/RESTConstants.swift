//
//  RESTConstants.swift
//  SwiftSingleViewSample
//
//  Created by wws2003 on 2/10/15.
//  Copyright (c) 2015 wws2003. All rights reserved.
//

import Foundation

struct RESTConstants {
    /*
    -URL for add (register) user: https://{REST_SERVER_NAME}:{REST_SERVER_PORT}/users/add
        -Method: POST
        -Params: {"username" : ?, "fullname" : ?, "gender" : ?, "email" : ?, "password" : ?, "dob" : ?}
        -Returns {"result" : "success"/"failed", "message" = ? (failed reason...)}
    
    -URL for update user: https://{REST_SERVER_NAME}:{REST_SERVER_PORT}/users/update
        -Method: PUT
        -Params: {"id" : ?, "username" : ?, "fullname" : ?, "gender" : ?, "email" : ?, "password" : ?, "dob" : ?}
        -Returns {"result" : "success"/"failed", "message" = ? (failed reason...)}

    -URL for login: https://{REST_SERVER_NAME}:{REST_SERVER_PORT}/login
        -Method: GET
        -Params: None
        -Returns: {"result" : "success"/"failed", "message" = ? (failed reason...), "user" : {"id" : ?, "username" : ?, "fullname" : ?, "gender" : ?, "email" : ?, "password" : ?, "dob" : ?} (in the case of successful login)}
    
    -URL for retrieve user profile: https://{REST_SERVER_NAME}:{REST_SERVER_PORT}/user/{SourceUserId}/{TargetUserId}
        -Method: GET
        -Params: None
        -Returns: {"id" : ?, "username" : ?, "fullname" : ?, "gender" : ?, "email" : ?, "password" : ?, "dob" : ?} or {"":""}, in the case of permission denied
   
    -URL for delete user: https://{REST_SERVER_NAME}:{REST_SERVER_PORT}/users/delete/{UserId}
        -Method: DELETE
        -Params: None
        -Returns {"result" : "success"/"failed", "message" = ? (failed reason...)}
    */
    
    static let REST_SERVER_NAME = "";
    static let REST_SERVER_PORT = "";
    
    static let REST_USERS_URL = "users";
    static let REST_USER_URL = "user";
    static let REST_USER_LOGIN_URL = "login";
    
    static let REST_USER_ACTION_ADD = "add";
    static let REST_USER_ACTION_DELETE = "delete";
    static let REST_USER_ACTION_UPDATE = "update";
    
    static let REST_USER_PARAM_USERNAME = "username";
    static let REST_USER_PARAM_LOGIN_TYPE = "logintype";
    static let REST_USER_PARAM_FULLNAME = "fullname";
    static let REST_USER_PARAM_GENDER = "gender";
    static let REST_USER_PARAM_DOB = "dob";
    static let REST_USER_PARAM_EMAIL = "email";
    static let REST_USER_PARAM_PASSWORD = "password";
    
    static let REST_REGISTER_RESULT = "result";
    static let REST_LOGIN_RESULT = "result";
    static let REST_UPDATE_RESULT = "result";
    static let REST_DELETE_RESULT = "result";
   
    static let REST_REGISTER_RESULT_SUCCESS = "success";
    static let REST_REGISTER_RESULT_FAILED = "failed";
    
    static let REST_LOGIN_RESULT_SUCCESS = "success";
    static let REST_LOGIN_RESULT_FAILED = "failed";
    static let REST_LOGIN_RESULT_USER = "user";
    
    static let REST_UPDATE_RESULT_SUCCESS = "success";
    static let REST_UPDATE_RESULT_FAILED = "failed";
    
    static let REST_DELETE_RESULT_SUCCESS = "success";
    static let REST_DELETE_RESULT_FAILED = "failed";
    
    static let REST_USER_RESULT_ID = "id";
    static let REST_USER_RESULT_USERNAME = "username";
    static let REST_USER_RESULT_FULLNAME = "fullname";
    static let REST_USER_RESULT_GENDER = "gender";
    static let REST_USER_RESULT_DOB = "dob";
    static let REST_USER_RESULT_EMAIL = "email";
    static let REST_USER_RESULT_PASSWORD = "password";
}