/*
 * Copyright (c) 2016 Open Baton (http://openbaton.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openbaton.nfvo.api.admin;

import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.validation.Valid;
import org.openbaton.catalogue.nfvo.ServiceCredentials;
import org.openbaton.exceptions.BadRequestException;
import org.openbaton.nfvo.security.interfaces.ComponentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/components")
public class RestComponents {

  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired private ComponentManager componentManager;

  /**
   * Create a new Service. This generates a new AES Key that can be used for registering the
   * Service.
   *
   * @param projectId
   * @param serviceCreateBody
   * @return
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  @ApiOperation(
    value = "Create Service",
    notes =
        "Enable a new Service. This generates a new AES Key that must be used in the Service SDK"
  )
  @RequestMapping(
    value = "/services/create",
    method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
  )
  @ResponseStatus(HttpStatus.CREATED)
  public byte[] createService(
      @RequestHeader(value = "project-id") String projectId,
      @RequestBody @Valid JsonObject serviceCreateBody)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, BadRequestException {

    if (!serviceCreateBody.has("name"))
      throw new BadRequestException("The request's json body has to contain a name property.");

    String serviceName = null;
    try {
      serviceName = serviceCreateBody.getAsJsonPrimitive("name").getAsString();
    } catch (ClassCastException e1) {
      throw new BadRequestException(
          "The request's json body has to have this form: {'name':'examplename'}");
    } catch (IllegalStateException e2) {
      throw new BadRequestException(
          "The request's json body has to have this form: {'name':'examplename'}");
    }

    return componentManager.createService(serviceName, projectId);
  }

  /**
   * Registers a Service. For this to work, the Service has to be already created. The request body
   * is expected to be a String. This String has to be the word 'register' encrypted with the Key
   * obtained while creating the Service. This method returns a token which can be used by the
   * Service to issue requests to the NFVO API.
   *
   * @param serviceRegisterBody
   * @return
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  @ApiOperation(value = "Register Service", notes = "Register an already created Service.")
  @RequestMapping(
    value = "/services/register",
    method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.CREATED)
  public ServiceCredentials registerService(@RequestBody byte[] serviceRegisterBody)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

    return componentManager.registerService(serviceRegisterBody);
  }

  //  /** Enable a new Manager. this generates a new Rabbit User that must be used in the Manager SDK */
  //  @ApiOperation(
  //    value = "Enable Manager",
  //    notes =
  //        "Enable a new Manager. this generates a Rabbit user that must be used in the Manager SDK"
  //  )
  //  @RequestMapping(
  //    value = "/managers",
  //    method = RequestMethod.POST,
  //    consumes = MediaType.APPLICATION_JSON_VALUE,
  //    produces = MediaType.APPLICATION_JSON_VALUE
  //  )
  //  @ResponseStatus(HttpStatus.CREATED)
  //  public ManagerCredentials enableManager(
  //      @RequestHeader(value = "project-id") String projectId,
  //      @RequestBody @Valid JsonObject serviceRegisterBody)
  //      throws IOException {
  //
  //    return componentManager.enableManager(serviceRegisterBody);
  //  }
}
