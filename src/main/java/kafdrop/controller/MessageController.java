package kafdrop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kafdrop.config.MessageFormatConfiguration.MessageFormatProperties;
import kafdrop.config.ProtobufDescriptorConfiguration.ProtobufDescriptorProperties;
import kafdrop.config.SchemaRegistryConfiguration.SchemaRegistryProperties;
import kafdrop.form.SearchMessageForm;
import kafdrop.model.MessageVO;
import kafdrop.model.TopicPartitionVO;
import kafdrop.model.TopicVO;
import kafdrop.model.UserVO; // Assuming UserVO is defined elsewhere in the project
import kafdrop.service.KafkaMonitor;
import kafdrop.service.KafkaMonitorImpl; // Assuming KafkaMonitorImpl is defined elsewhere in the project
import kafdrop.service.MessageInspector;
import kafdrop.service.TopicNotFoundException;
import kafdrop.service.UserService; // Assuming UserService is defined elsewhere in the project
import kafdrop.util.AvroMessageDeserializer;
import kafdrop.util.DefaultMessageDeserializer;
import kafdrop.util.Deserializers;
import kafdrop.util.KeyFormat;
import kafdrop.util.MessageDeserializer;
import kafdrop.util.MessageFormat;
import kafdrop.util.MsgPackMessageDeserializer;
import kafdrop.util.ProtobufMessageDeserializer;
import kafdrop.util.ProtobufSchemaRegistryMessageDeserializer;
import kafdrop.util.TokenUtil; // Assuming TokenUtil is defined elsewhere in the project
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Tag(name = "message-controller", description = "Message Controller")
@Controller
public final class MessageController {
  @Autowired private UserService userService; // Assuming UserService is defined elsewhere in the project
  @Autowired private KafkaMonitorImpl kafkaMonitorImpl;

  private final KafkaMonitor kafkaMonitor;

  private final MessageInspector messageInspector;

  private final MessageFormatProperties messageFormatProperties;

  private final SchemaRegistryProperties schemaRegistryProperties;

  private final ProtobufDescriptorProperties protobufProperties;

  private final TokenUtil tokenUtil; // Assuming TokenUtil is defined elsewhere in the project

  public MessageController(KafkaMonitor kafkaMonitor, MessageInspector messageInspector,
                           MessageFormatProperties messageFormatProperties,
                           SchemaRegistryProperties schemaRegistryProperties,
                           ProtobufDescriptorProperties protobufProperties,
                           TokenUtil tokenUtil) {
    this.kafkaMonitor = kafkaMonitor;
    this.messageInspector = messageInspector;
    this.messageFormatProperties = messageFormatProperties;
    this.schemaRegistryProperties = schemaRegistryProperties;
    this.protobufProperties = protobufProperties;
    this.tokenUtil = tokenUtil;
  }

  @PostMapping("/register")
  public String registerUser(@Valid @RequestBody UserVO user, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      // Handle validation errors
      // ...
      return "redirect:/user/error";
    }
    String hashedPassword = kafkaMonitorImpl.hashPassword(user.getPassword());
    user.setPasswordHash(hashedPassword);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    // Save the user to the database
    // ...
    return "redirect:/user/success";
  }

  @PostMapping(value = "/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserLoginRequest loginRequest, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      // Handle validation errors
      return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
    }

    UserVO user = userService.findByUsername(loginRequest.getUsername());
    if (user == null) {
      return new ResponseEntity<>("Invalid username or password.", HttpStatus.UNAUTHORIZED);
    }

    if (user.getAccountLocked()) {
      return new ResponseEntity<>("Account is locked due to multiple failed login attempts.", HttpStatus.FORBIDDEN);
    }

    boolean passwordMatch = kafkaMonitorImpl.validatePassword(loginRequest.getPassword(), user.getPasswordHash());
    if (!passwordMatch) {
      userService.incrementFailedLoginAttempts(user);
      return new ResponseEntity<>("Invalid username or password.", HttpStatus.UNAUTHORIZED);
    }

    userService.resetFailedLoginAttempts(user);
    HashMap<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId());
    String token = tokenUtil.generateToken(claims, user.getUsername());

    return ResponseEntity.ok(new UserLoginResponse(token));
  }

  public static class UserLoginRequest {
    private String username;
    private String password;
    // Getters and setters omitted for brevity
  }

  public static class UserLoginResponse {
    private String token;
    // Getters and setters omitted for brevity
  }

  // Existing methods...

  // Rest of the MessageController class as provided in the current code...
}