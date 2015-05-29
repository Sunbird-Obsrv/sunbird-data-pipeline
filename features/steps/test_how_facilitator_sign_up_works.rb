require_relative '../../data-generator/user_generator.rb'
require_relative '../../data-generator/session_generator.rb'
require 'json'

class Spinach::Features::TestHowFacilitatorSignUpWorks < Spinach::FeatureSteps
  include CommonSteps::Database
  include CommonSteps::UserSimulation

  USER_NAME = "B R Swamy"
  USER_MOBILE = "1234567890"

  SECOND_USER_NAME = "B R Raju"
  SECOND_USER_MOBILE = "2345678901"

  USER_NAME_WITH_DOT = "B. R. Swamy"
  NUMERIC_USER_NAME = "123"
  ALPHA_NUMERIC_USER_NAME = "AB123"
  SPECIAL_CHAR_USER_NAME = "DS#123"
  SINGLE_ALPHABET_USER_NAME = "g"
  TWO_ALPHABET_USER_NAME = "mi"

  BLANK_USER_NAME=""
  BLANK_MOBILE_NUMBER=""
  BLANK_PASSWORD=""
  BLANK_CONFIRM_PASSWORD=""

  SHORT_USER_MOBILE = "98765423"
  LONG_USER_MOBILE = "987634561289"
  ALPHA_NUMERIC_MOBILE = "98765423as"

  USER_PASSWORD = "test123@"
  USER_CONFIRM_PASSWORD = "test123@"

  DIFFERENT_CONFIRM_PASSWORD = "test"

  SUCCESS_STATUS = "successful"
  FAILED_STATUS = "failed"

  RECORD_EXIST_ERROR = "RECORD_EXISTS"
  VALIDATION_ERROR = "VALIDATION_ERROR"

  NAME_EMPTY_ERROR_MESSAGE = "name must not be empty"
  MOBILE_NUMBER_ALREADY_REGISTERED_ERROR_MESSAGE = "mobile number already registered"
  MOBILE_NUMBER_EMPTY_ERROR_MESSAGE = "mobile must not be empty"
  PASSWORD_EMPTY_ERROR_MESSAGE = "password must not be empty"
  PASSWORD_MISMATCH_ERROR_MESSAGE = "password and confirmPassword must match "
  INVALID_MOBILE_NUMBER_ERROR_MESSAGE = "mobile must be 10 digit number"
  SHORT_USER_NAME_ERROR_MESSAGE = "name must have minimum length : 2 "
  INVALID_USER_NAME_ERROR_MESSAGE = "name must have only alphabets, spaces and dot(.)"

  step 'I have not signed up as a facilitator' do
    remove_all_users
    users = get_all_users
    users.count.should == 0
  end

  step 'I sign up as a facilitator from a new device' do
    device = ::Generator::Device.new
    user = ::UserGenerator::User.new(USER_NAME, USER_MOBILE, USER_PASSWORD,USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I should be signed up as a facilitator' do
    @response["params"]["status"].should == SUCCESS_STATUS
    @response["params"]["err"].should == ""
    user_count_should_be_one
  end

  step 'I sign up as a facilitator from an existing device' do
    user = ::UserGenerator::User.new(SECOND_USER_NAME, SECOND_USER_MOBILE, USER_PASSWORD,USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    user = ::UserGenerator::User.new(USER_NAME, USER_MOBILE, USER_PASSWORD,USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I have already signed up as a facilitator' do
    i_have_not_signed_up_as_a_facilitator
    i_sign_up_as_a_facilitator_from_a_new_device
  end

  step 'I should get facilitator already exist error' do
    i_should_get_mobile_number_already_exist_error
  end

  step 'I sign up as a facilitator using an already registered mobile number' do
    i_have_not_signed_up_as_a_facilitator
    i_sign_up_as_a_facilitator_from_a_new_device
    user = ::UserGenerator::User.new(SECOND_USER_NAME, USER_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I should get mobile number already exist error' do
    @response["params"]["status"].should == FAILED_STATUS
    @response["params"]["err"].should == RECORD_EXIST_ERROR
    @response["params"]["errmsg"].should == MOBILE_NUMBER_ALREADY_REGISTERED_ERROR_MESSAGE
    user_count_should_be_one
  end

  step 'I sign up as a facilitator using blank name' do
    user = ::UserGenerator::User.new(BLANK_USER_NAME, USER_MOBILE, USER_PASSWORD,USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I should get name cannot be blank error' do
    @response["params"]["status"].should == FAILED_STATUS
    @response["params"]["err"].should == VALIDATION_ERROR
    @response["params"]["errmsg"].should == NAME_EMPTY_ERROR_MESSAGE
    user_count_should_be_zero
  end

  step 'I sign up as a facilitator using blank mobile number' do
    device = ::Generator::Device.new
    user = ::UserGenerator::User.new(USER_NAME, BLANK_MOBILE_NUMBER, USER_PASSWORD,USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I should get mobile number cannot be blank error' do
    @response["params"]["status"].should == FAILED_STATUS
    @response["params"]["err"].should == VALIDATION_ERROR
    @response["params"]["errmsg"].should == MOBILE_NUMBER_EMPTY_ERROR_MESSAGE
    user_count_should_be_zero
  end

  step 'I sign up as a facilitator using blank password' do
    device = ::Generator::Device.new
    user = ::UserGenerator::User.new(USER_NAME, USER_MOBILE, BLANK_PASSWORD,BLANK_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I should get password cannot be blank error' do
    @response["params"]["status"].should == FAILED_STATUS
    @response["params"]["err"].should == VALIDATION_ERROR
    @response["params"]["errmsg"].should == PASSWORD_EMPTY_ERROR_MESSAGE
    user_count_should_be_zero
  end

  step 'I sign up as a facilitator with requester id as blank' do
    user = ::UserGenerator::User.new(USER_NAME, USER_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
    pending 'step not implemented'
  end

  step 'I should get invalid data error' do
    pending 'step not implemented'
  end

  step 'I sign up as a facilitator using different password and confirm password' do
    user = ::UserGenerator::User.new(USER_NAME, USER_MOBILE, USER_PASSWORD, DIFFERENT_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I should get invalid password error' do
    @response["params"]["status"].should == FAILED_STATUS
    @response["params"]["err"].should == VALIDATION_ERROR
    @response["params"]["errmsg"].should == PASSWORD_MISMATCH_ERROR_MESSAGE
    user_count_should_be_zero
  end

  step 'I sign up as a facilitator using only numbers in the name field' do
    user = ::UserGenerator::User.new(NUMERIC_USER_NAME, USER_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I should get short name error' do
    @response["params"]["status"].should == FAILED_STATUS
    @response["params"]["err"].should == VALIDATION_ERROR
    @response["params"]["errmsg"].should == SHORT_USER_NAME_ERROR_MESSAGE
    user_count_should_be_zero
  end

  step 'I should get invalid name error' do
    @response["params"]["status"].should == FAILED_STATUS
    @response["params"]["err"].should == VALIDATION_ERROR
    @response["params"]["errmsg"].should == INVALID_USER_NAME_ERROR_MESSAGE
    user_count_should_be_zero
  end

  step 'I sign up as a facilitator using alpha numeric characters in the name field' do
    user = ::UserGenerator::User.new(ALPHA_NUMERIC_USER_NAME, USER_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I sign up as a facilitator using special characters in the name field' do
    user = ::UserGenerator::User.new(SPECIAL_CHAR_USER_NAME, USER_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I sign up as a facilitator using alphabets and dot in the name field' do
    user = ::UserGenerator::User.new(USER_NAME_WITH_DOT, USER_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I sign up as a facilitator using single alphabet in the name field' do
    user = ::UserGenerator::User.new(SINGLE_ALPHABET_USER_NAME, USER_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I sign up as a facilitator using two alphabets in the name field' do
    user = ::UserGenerator::User.new(TWO_ALPHABET_USER_NAME, USER_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I sign up as a facilitator using less than 10 digits in the mobile number' do
    user = ::UserGenerator::User.new(USER_NAME, SHORT_USER_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I should get an invalid mobile number error' do
    @response["params"]["status"].should == FAILED_STATUS
    @response["params"]["err"].should == VALIDATION_ERROR
    @response["params"]["errmsg"].should == INVALID_MOBILE_NUMBER_ERROR_MESSAGE
    user_count_should_be_zero
  end

  step 'I sign up as a facilitator using more than 10 digits in the mobile number' do
    user = ::UserGenerator::User.new(USER_NAME, LONG_USER_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'I sign up as a facilitator using non numeric characters in the mobile number' do
    user = ::UserGenerator::User.new(USER_NAME, ALPHA_NUMERIC_MOBILE, USER_PASSWORD, USER_CONFIRM_PASSWORD, device)
    resp=user.post_newuserrequest
    @response = JSON.parse(resp.body)
  end

  step 'user count should be one' do
    users = get_users(USER_MOBILE)
    users.count.should == 1
  end

  step 'user count should be zero' do
    users = get_users(USER_MOBILE)
    users.count.should == 0
  end

  step 'I should be able to log in as facilitator' do
    pending 'step not implemented'
  end
end
