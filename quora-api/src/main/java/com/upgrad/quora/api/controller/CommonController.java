package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getuser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws UserNotFoundException, AuthorizationFailedException {
        final User user = userBusinessService.getUser(userId, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse.firstName(user.getFirstName());
        userDetailsResponse.lastName(user.getLastName());
        userDetailsResponse.userName(user.getUserName());
        userDetailsResponse.emailAddress(user.getEmail());
        userDetailsResponse.country(user.getCountry());
        userDetailsResponse.aboutMe(user.getAboutMe());
        userDetailsResponse.dob(user.getDob());
        userDetailsResponse.contactNumber(user.getContactNumber());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }

}
