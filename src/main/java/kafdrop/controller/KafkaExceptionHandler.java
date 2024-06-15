/*
 * Copyright 2017 Kafdrop contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package kafdrop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import kafdrop.service.NotInitializedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public final class KafkaExceptionHandler {
  @ExceptionHandler(NotInitializedException.class)
  public String notInitialized() {
    return "not-initialized";
  }

  @ExceptionHandler(AccountLockedException.class)
  public ResponseEntity<String> handleAccountLocked(AccountLockedException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  // Assuming AccountLockedException and InvalidCredentialsException are defined elsewhere in the project
  // These exceptions should carry appropriate error messages that will be returned in the response
  // The HttpStatus codes are set according to the API documentation requirements
}