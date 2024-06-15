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

package kafdrop.service;

import kafdrop.model.AclVO;
import kafdrop.model.BrokerVO;
import kafdrop.model.ClusterSummaryVO;
import kafdrop.model.ConsumerPartitionVO;
import kafdrop.model.ConsumerTopicVO;
import kafdrop.model.ConsumerVO;
import kafdrop.model.CreateTopicVO;
import kafdrop.model.MessageVO;
import kafdrop.model.SearchResultsVO;
import kafdrop.model.TopicPartitionVO;
import kafdrop.model.TopicVO;
import kafdrop.util.Deserializers;
import org.apache.kafka.clients.admin.ConfigEntry.ConfigSource;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Service
public final class KafkaMonitorImpl implements KafkaMonitor {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaMonitorImpl.class);
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final String jwtSecret = "secret"; // This should be moved to a config file
  private final long jwtExpirationInMs = 3600000; // 1 hour in milliseconds

  private final KafkaHighLevelConsumer highLevelConsumer;
  private final KafkaHighLevelAdminClient highLevelAdminClient;

  public KafkaMonitorImpl(KafkaHighLevelConsumer highLevelConsumer, KafkaHighLevelAdminClient highLevelAdminClient) {
    this.highLevelConsumer = highLevelConsumer;
    this.highLevelAdminClient = highLevelAdminClient;
  }

  // ... (rest of the unchanged methods)

  public String hashPassword(String password) {
    if (password == null || password.isEmpty()) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }
    return passwordEncoder.encode(password);
  }

  public boolean validatePassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  public String generateToken(Map<String, Object> claims, String subject) {
    final Date now = new Date();
    final Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  // This method should be called after successful authentication to reset the login attempts
  public void resetFailedLoginAttempts(String username) {
    // Implementation to reset the failed login attempts in the database
    // This is a placeholder, actual implementation will depend on the database access code
  }

  // This method should be called to increment failed login attempts and lock the account if necessary
  // This is a placeholder, actual implementation will depend on the database access code

  // ... (rest of the unchanged methods)
}