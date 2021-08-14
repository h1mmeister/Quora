package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController {
    
    @Autowired
    private UserBusinessService userBusinessService;

    /**
     * This method registers a user with all the details provided and handles the
     * scenario when user provides empty or invalid username/email and throws an error message
     *
     * @param signupUserRequest holds all the details keyed in by the user at the time of signup
     * @return UUID of the registered user for further login
     * @throws SignUpRestrictedException if the user provides invalid username/email
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        final User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setFirstName(signupUserRequest.getFirstName());
        user.setLastName(signupUserRequest.getLastName());
        user.setUserName(signupUserRequest.getUserName());
        user.setEmail(signupUserRequest.getEmailAddress());
        user.setPassword(signupUserRequest.getPassword());
        user.setCountry(signupUserRequest.getCountry());
        user.setAboutMe(signupUserRequest.getAboutMe());
        user.setDob(signupUserRequest.getDob());
        user.setContactNumber(signupUserRequest.getContactNumber());

        final User createdUser = userBusinessService.signup(user);
        SignupUserResponse signupUserResponse = new SignupUserResponse();
        signupUserResponse.id(createdUser.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(signupUserResponse, HttpStatus.CREATED);
    }

    /**
     * This method is used to sign in a user who has successfully registered
     * If not,throws an error message that the username does not exist or password is wrong
     *
     * @param authorization this contains the encoded username and password
     * @return signin response which contains user UUID and message stating whether user has successfully signed in or not
     * @throws AuthenticationFailedException will be thrown when the username or password does not match
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        UserAuthEntity userAuthEntity = userBusinessService.signin(authorization);
        User user = userAuthEntity.getUser();
        SigninResponse signinResponse = new SigninResponse();
        signinResponse.id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", userAuthEntity.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {
        String uuid = userBusinessService.getUserUUID(authorization);
        SignoutResponse signoutResponse = new SignoutResponse();
        signoutResponse.setId(uuid);
        signoutResponse.setMessage("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }


}
