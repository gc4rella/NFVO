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

import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.validation.Valid;
import org.openbaton.catalogue.security.Key;
import org.openbaton.exceptions.AlreadyExistingException;
import org.openbaton.exceptions.BadFormatException;
import org.openbaton.exceptions.NotFoundException;
import org.openbaton.nfvo.core.interfaces.KeyManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/keys")
public class RestKeys {

  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired private KeyManagement keyManagement;
  @Autowired private Gson gson;

  /**
   * Adds a new Key
   *
   * @param key object containing the key which needs to be uploaded
   */
  @ApiOperation(
    value = " Importing a Key",
    notes =
        "Pass the Key as JSON content in the Request Body. The Key should contain the name and public key."
  )
  @RequestMapping(
    method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Key importKey(
      @RequestHeader(value = "project-id") String projectId, @RequestBody @Valid Key key)
      throws UnsupportedEncodingException, NoSuchAlgorithmException, BadFormatException,
          AlreadyExistingException {
    return keyManagement.addKey(projectId, key.getName(), key.getPublicKey());
  }

  /**
   * Generate a new Key with the given name for the given project
   *
   * @param name : name of the key to be created
   */
  @ApiOperation(
    value = "Generate a Key",
    notes =
        "Generates a new key for the given project from a name that is passed in the Request Body"
  )
  @RequestMapping(
    value = "generate",
    method = RequestMethod.POST,
    produces = MediaType.TEXT_PLAIN_VALUE
  )
  @ResponseStatus(HttpStatus.CREATED)
  public String generateKey(
      @RequestHeader(value = "project-id") String projectId, @RequestBody String name)
      throws NoSuchAlgorithmException, IOException, AlreadyExistingException {
    log.debug("Generating key with name: " + name);
    return keyManagement.generateKey(projectId, name);
  }

  /**
   * Removes the Key from the key repository
   *
   * @param id : the id of the key to be removed
   */
  @ApiOperation(value = "Remove a Key", notes = "The id of the key is specified in the URL")
  @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @PathVariable("id") String id, @RequestHeader(value = "project-id") String projectId)
      throws NotFoundException {
    keyManagement.delete(projectId, id);
  }

  @ApiOperation(
    value = "Remove multiple Keys",
    notes = "The ids of the Keys is passed in a list in the Request Body"
  )
  @RequestMapping(
    value = "/multipledelete",
    method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void multipleDelete(
      @RequestHeader(value = "project-id") String projectId, @RequestBody @Valid List<String> ids)
      throws NotFoundException {
    for (String id : ids) {
      keyManagement.delete(projectId, id);
    }
  }

  /**
   * Returns the list of the Users available
   *
   * @return List<User>: The list of Users available
   */
  @ApiOperation(
    value = "Retrieve all Keys",
    notes = "Returns all the keys created for the project with id specified in the header"
  )
  @RequestMapping(method = RequestMethod.GET)
  public List<Key> findAll(@RequestHeader(value = "project-id") String projectId) {
    return (List<Key>) keyManagement.query(projectId);
  }

  /**
   * Returns the Key selected by id
   *
   * @param id : The id of the Key
   * @return User: The Key selected
   */
  @ApiOperation(value = "Retrieve a key", notes = "The id of the key is specified in the URL")
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public Key findById(
      @RequestHeader(value = "project-id") String projectId, @PathVariable("id") String id)
      throws NotFoundException {
    Key key = keyManagement.queryById(projectId, id);
    if (key == null) throw new NotFoundException("No key found with ID " + id);
    return key;
  }
}
