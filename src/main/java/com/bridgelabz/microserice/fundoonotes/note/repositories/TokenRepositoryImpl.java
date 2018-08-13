package com.bridgelabz.microserice.fundoonotes.note.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TokenRepositoryImpl implements TokenRepository {

	@Value("${key}")
	private String key;

	private RedisTemplate<String, String> redisTemplate;

	private HashOperations<String, String, String> hashOperations;

	@Autowired
	public TokenRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
		hashOperations = this.redisTemplate.opsForHash();
	}

	@Override
	public void save(String token, String userId) {
		hashOperations.put(key, token, userId);
	}

	@Override
	public String get(String token) {
		return hashOperations.get(key, token);
	}

	@Override
	public void delete(String token) {
		hashOperations.delete(key, token);
	}
}