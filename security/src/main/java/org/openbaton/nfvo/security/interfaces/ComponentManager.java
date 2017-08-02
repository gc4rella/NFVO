package org.openbaton.nfvo.security.interfaces;

import org.openbaton.catalogue.nfvo.ManagerCredentials;
import org.openbaton.catalogue.nfvo.ServiceMetadata;
import org.openbaton.exceptions.NotFoundException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/** Created by lto on 04/04/2017. */
public interface ComponentManager {

  String registerService(String body) throws
                                      IllegalBlockSizeException,
                                      InvalidKeyException,
                                      BadPaddingException,
                                      NoSuchAlgorithmException,
                                      NoSuchPaddingException, NotFoundException;

  String createService(String serviceName, String projectId)
      throws NoSuchAlgorithmException, IOException;

  ManagerCredentials enableManager(String message) throws IOException;

  boolean isService(String token)
      throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
          IllegalBlockSizeException, NoSuchPaddingException;

  Iterable<ServiceMetadata> listServices();

  void removeService(String id);

  void removeTokens();
}
